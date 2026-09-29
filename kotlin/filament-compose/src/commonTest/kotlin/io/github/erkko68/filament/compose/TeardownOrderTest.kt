package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.SubcomposeLayoutState
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfAsset
import io.github.erkko68.filament.compose.scene.Group
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.StandardMaterial
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberStandardMaterial
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.assertDestroyed
import io.github.erkko68.filament.compose.testutils.skippedComposeTest
import io.github.erkko68.filament.compose.testutils.withUiThreadFilamentScene
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Teardown order across every shape Compose can tear a tree down in: in-place removal, deactivation for reuse
 * (what LazyColumn does to recycled items), an abandoned pass, and parents in a different composition from
 * their children. A wrong order is a Filament panic that aborts the process, so "the test finishes" is half
 * the assertion; the other half is that nothing leaks.
 */
@OptIn(ExperimentalTestApi::class)
class TeardownOrderTest {

    private val red = LinearColor(1f, 0f, 0f)
    private val blue = LinearColor(0f, 0f, 1f)

    // ── Composition-owned engine ────────────────────────────────────────────────────────────────

    @Test
    fun aTreeLeavingWithItsEngineDestroysTheEngineLast() = teardownTest { setContent ->
        var engine: Engine? = null
        setContent {
            val e = rememberFilamentEngine().also { engine = it }
            rememberFilamentScene(e) {
                DirectionalLight()
                Group { Cube(material = rememberColorMaterialInstance(red)) }
                Sphere(material = rememberColorMaterialInstance(blue))
            }
        }
        setContent {}
        assertFalse(assertNotNull(engine).isValid, "the engine goes with its tree")
    }

    @Test
    fun aSubcomposedSceneLeavingWithTheEngineAboveIt() = teardownTest { setContent ->
        var engine: Engine? = null
        setContent {
            val e = rememberFilamentEngine().also { engine = it }
            BoxWithConstraints {
                rememberFilamentScene(e) { Cube(material = rememberColorMaterialInstance(red)) }
            }
        }
        setContent {}
        assertFalse(assertNotNull(engine).isValid)
    }

    // ── LazyColumn ──────────────────────────────────────────────────────────────────────────────
    // Recycling alone is fine; the crash was an item leaving before content that arrives a frame late
    // had settled, which fast scrolls and jumps both do.

    @Test
    fun jumpingAwayFromItemsWithTheirOwnEngines() = lazyListTest(hoistEngine = false) { list, scope -> jumpBackAndForth(list, scope) }

    @Test
    fun jumpingAwayFromItemsOnAHoistedEngine() = lazyListTest(hoistEngine = true) { list, scope -> jumpBackAndForth(list, scope) }

    @Test
    fun flingingThroughItemsWithTheirOwnEngines() = lazyListTest(hoistEngine = false) { list, _ ->
        repeat(2) {
            for (delta in listOf(260f, -260f)) repeat(35) {
                runOnUiThread { list.dispatchRawDelta(delta) }
                mainClock.advanceTimeByFrame()
            }
        }
    }

    private fun ComposeUiTest.jumpBackAndForth(list: LazyListState, scope: CoroutineScope) = repeat(6) { i ->
        runOnUiThread { scope.launch { list.scrollToItem(if (i % 2 == 0) 40 else 0) } }
        frames(2)
    }

    /** A list of scenes whose items each get a second instance a frame after they appear. */
    private fun lazyListTest(hoistEngine: Boolean, scroll: ComposeUiTest.(LazyListState, CoroutineScope) -> Unit) = teardownTest { setContent ->
        val list = LazyListState()
        var scope: CoroutineScope? = null
        val engines = mutableListOf<Engine>()
        setContent {
            scope = rememberCoroutineScope()
            val shared = if (hoistEngine) rememberFilamentEngine() else null
            LazyColumn(state = list, modifier = Modifier.height(400.dp)) {
                items(50) {
                    val e = shared ?: rememberFilamentEngine()
                    remember(e) { engines += e }
                    BoxWithConstraints(Modifier.height(200.dp)) {
                        rememberFilamentScene(e) {
                            var late by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { withFrameNanos { }; late = true }
                            Cube(material = rememberColorMaterialInstance(red))
                            if (late) Sphere(material = rememberColorMaterialInstance(blue))
                        }
                    }
                }
            }
        }
        scroll(list, assertNotNull(scope))
        setContent {}
        assertTrue(engines.isNotEmpty() && engines.none { it.isValid }, "every engine goes with the list")
    }

    @Test
    fun anOwnedEngineOutlivesAHoistedInstanceUsedInASubcomposition() = teardownTest { setContent ->
        var engine: Engine? = null
        setContent {
            val e = rememberFilamentEngine().also { engine = it }
            val instance = rememberMaterialInstance(rememberStandardMaterial(StandardMaterial.Lit, e), e)
            BoxWithConstraints {
                rememberFilamentScene(e) { Cube(material = instance) }
            }
        }
        setContent {}
        assertFalse(assertNotNull(engine).isValid)
    }

    // ── Abandoned pass ──────────────────────────────────────────────────────────────────────────

    @Test
    fun anAbandonedPassDestroysEverythingItCreated() = withUiThreadFilamentScene { setContent, engine, _ ->
        val slots = SubcomposeLayoutState()
        setContent { SubcomposeLayout(slots) { layout(0, 0) {} } }
        waitForIdle()

        var material: Material? = null
        var instance: MaterialInstance? = null
        var scene: FilamentScene? = null
        runOnUiThread {
            // Composed but never applied, then cancelled: every remember is abandoned, no effect ever runs.
            val pass = slots.createPausedPrecomposition(Unit) {
                val m = rememberStandardMaterial(StandardMaterial.Lit, engine).also { material = it }
                val i = rememberMaterialInstance(m, engine).also { instance = it }
                scene = rememberFilamentScene(engine) { Cube(material = i) }
            }
            while (!pass.resume { false }) Unit
            pass.cancel()
        }
        waitForIdle()

        val m = assertNotNull(material, "the pass should have composed")
        assertDestroyed("abandoned instance leaked") { engine.isValidExpensiveMaterialInstance(assertNotNull(instance)) }
        assertDestroyed("abandoned material leaked") { engine.isValidMaterial(m) }
        assertDestroyed("abandoned scene leaked") { engine.isValidScene(assertNotNull(scene).scene) }
    }

    // ── Parents the caller owns ─────────────────────────────────────────────────────────────────

    @Test
    fun aCallerOwnedMaterialIsDestroyedByItsOwnerAfterOurInstance() = withUiThreadFilamentScene { setContent, engine, _ ->
        var material: Material? = null
        setContent {
            // The documented FilamentEffect pattern: the caller builds and destroys the material.
            val m = remember { Material.Builder().payload(StandardMaterial.Lit.payload()).build(engine) }
            material = m
            DisposableEffect(m) { onDispose { engine.destroyMaterial(m) } }
            rememberFilamentScene(engine) { Cube(material = rememberMaterialInstance(m)) }
        }
        setContent {}
        assertDestroyed("the caller's material") { engine.isValidMaterial(assertNotNull(material)) }
    }

    @Test
    fun aCallerOwnedEngineIsDestroyedByItsOwnerAfterOurObjects() = teardownTest { setContent ->
        var engine: Engine? = null
        setContent {
            val e = remember { Filament.init(); Engine.create(Engine.Backend.DEFAULT) }.also { engine = it }
            DisposableEffect(e) { onDispose { e.destroy() } }
            rememberFilamentScene(e) {
                DirectionalLight()
                Cube(material = rememberColorMaterialInstance(red))
            }
        }
        setContent {}
        assertFalse(assertNotNull(engine).isValid)
    }

    // ── Replacing objects while others still use them ─────────────────────────────────────────

    @Test
    fun anInstanceDroppedWhileItsMeshSwitchesAway() = withUiThreadFilamentScene { setContent, engine, _ ->
        var useRed by mutableStateOf(true)
        var redInstance: MaterialInstance? = null
        var blueInstance: MaterialInstance? = null
        setContent {
            val r = if (useRed) rememberColorMaterialInstance(red).also { redInstance = it } else null
            val b = rememberColorMaterialInstance(blue).also { blueInstance = it }
            Cube(material = r ?: b)
        }
        useRed = false
        frames(2)
        assertDestroyed("the dropped instance") { engine.isValidExpensiveMaterialInstance(assertNotNull(redInstance)) }
        assertTrue(engine.isValidExpensiveMaterialInstance(assertNotNull(blueInstance)), "the new one stays")
        setContent {}
    }

    @Test
    fun switchingAStandardMaterialRebuildsItsChain() = withUiThreadFilamentScene { setContent, engine, _ ->
        var type by mutableStateOf(StandardMaterial.Lit)
        val materials = mutableListOf<Material>()
        val instances = mutableListOf<MaterialInstance>()
        setContent {
            val m = rememberStandardMaterial(type, engine)
            val i = rememberMaterialInstance(m, engine)!!
            remember(m) { materials += m; instances += i }
            Cube(material = i)
        }
        type = StandardMaterial.Unlit
        frames(2)
        assertDestroyed("the old instance") { engine.isValidExpensiveMaterialInstance(instances.first()) }
        assertDestroyed("the old material") { engine.isValidMaterial(materials.first()) }
        assertTrue(engine.isValidMaterial(materials.last()), "the new material is live")
        setContent {}
    }

    @Test
    fun resizingARenderTargetFreesTheOldTextures() = teardownTest { setContent ->
        var size by mutableStateOf(32)
        var engine: Engine? = null
        val textures = mutableListOf<Texture>()
        setContent {
            val e = rememberFilamentEngine().also { engine = it }
            val scene = rememberFilamentScene(e) { Cube(material = rememberColorMaterialInstance(red)) }
            rememberRenderTargetTexture(scene, width = size, height = size)?.let { t -> remember(t) { textures += t } }
        }
        size = 64
        frames(2)
        val e = assertNotNull(engine)
        assertEquals(2, textures.size)
        assertDestroyed("the old colour texture") { e.isValidTexture(textures.first()) }
        assertTrue(e.isValidTexture(textures.last()))
        setContent {}
        assertFalse(e.isValid)
    }

    // ── glTF ────────────────────────────────────────────────────────────────────────────────────

    @Test
    fun assetsSharingAGltfioContextLeaveIndependently() = teardownTest { setContent ->
        val bytes = TestGlb.getAnimatedMorphCubeGlbBytes()
        var showFirst by mutableStateOf(true)
        var engine: Engine? = null
        var second: GltfAsset? = null
        setContent {
            val e = rememberFilamentEngine().also { engine = it }
            if (showFirst) rememberGltfAsset(engine = e) { bytes }
            second = rememberGltfAsset(engine = e) { bytes.copyOf() }
            rememberGltfAsset(engine = e, onError = {}) { ByteArray(64) { 0x7F } }
        }
        repeat(60) { if (second?.isReady != true) frames(1) }
        val asset = assertNotNull(second, "the second asset should load")
        assertTrue(asset.isReady)

        showFirst = false
        frames(2)
        val e = assertNotNull(engine)
        assertTrue(e.entityManager.isAlive(asset.filamentAsset.root), "the survivor keeps its context and entities")

        setContent {}
        assertFalse(e.isValid)
    }

    @Test
    fun anAssetLeavingMidLoadWithItsEngine() = teardownTest { setContent ->
        val duck = TestGlb.getDuckGlbBytes()
        repeat(3) {
            var engine: Engine? = null
            var asset: GltfAsset? = null
            setContent {
                val e = rememberFilamentEngine().also { engine = it }
                asset = rememberGltfAsset(engine = e) { duck }
            }
            repeat(30) { if (asset == null) frames(1) }
            assertFalse(assertNotNull(asset, "the asset should parse").isReady, "still uploading when it leaves")
            setContent {}
            assertFalse(assertNotNull(engine).isValid)
        }
    }

    // ── Harness ─────────────────────────────────────────────────────────────────────────────────

    private fun ComposeUiTest.frames(n: Int) = repeat(n) {
        mainClock.advanceTimeByFrame()
        waitForIdle()
    }

    /** A GPU-gated host whose content can own its engine; swapping content in or out settles two frames. */
    private fun teardownTest(body: ComposeUiTest.(setContent: (@Composable () -> Unit) -> Unit) -> Unit) =
        if (!TestEnv.gpuBackendAvailable) skippedComposeTest() else runComposeUiTest {
            mainClock.autoAdvance = false
            var slot by mutableStateOf<@Composable () -> Unit>({})
            setContent { slot() }
            body { content -> slot = content; frames(2) }
            slot = {}
            waitForIdle()
            mainClock.autoAdvance = true
        }
}
