package io.github.erkko68.filament.compose.testutils

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Material
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.compose.scene.GltfAsset
import io.github.erkko68.filament.gltfio.AssetLoader
import io.github.erkko68.filament.gltfio.FilamentAsset
import io.github.erkko68.filament.gltfio.Gltfio
import io.github.erkko68.filament.gltfio.ResourceLoader
import io.github.erkko68.filament.gltfio.UbershaderProvider
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base fixture for Tier-B compose tests: a real ([Engine.Backend.DEFAULT]) engine + a [Scene], for the
 * GPU-resource composables (primitives, materials, textures) that the NOOP driver panics on. Mirrors
 * the core [io.github.erkko68.filament.testutils.RenderingTestFixture] gating — [engine] is null when no
 * backend is available here, so every test must early-return (`val engine = engine ?: return`) and skip
 * gracefully instead of failing. Assertions stay binding-level (handles valid / managers report the
 * component), never pixels.
 *
 * Materials built via [materialInstance] are tracked and destroyed in [tearDown], so a test can hand a
 * live [MaterialInstance] to a primitive composable without owning its lifecycle.
 */
open class TierBSceneFixture {
    protected var engine: Engine? = null
    protected var scene: Scene? = null

    private val materials = mutableListOf<Material>()
    private val materialInstances = mutableListOf<MaterialInstance>()
    private val gltfAssets = mutableListOf<FilamentAsset>()
    private val gltfLoaders = mutableListOf<AssetLoader>()
    private val gltfProviders = mutableListOf<UbershaderProvider>()

    @BeforeTest
    fun awaitGraphics(): GraphicsReady = awaitGraphicsReady()

    @BeforeTest
    fun setUp() {
        Filament.init()
        // See RenderingTestFixture: don't attempt Engine.create on a host with no GPU/display — Filament
        // aborts on its driver thread there, which a try/catch can't recover.
        if (!TestEnv.gpuBackendAvailable) return
        val e = try {
            Engine.create(Engine.Backend.DEFAULT).takeIf { it.isValid }
        } catch (t: Throwable) {
            null
        } ?: return
        engine = e
        scene = e.createScene()
    }

    @AfterTest
    fun tearDown() {
        engine?.let { e ->
            // glTF first: assets belong to their loader, and the loader's materials to the provider.
            gltfAssets.forEachIndexed { i, a -> gltfLoaders.getOrNull(i)?.destroyAsset(a) }
            gltfLoaders.forEach { AssetLoader.destroy(it) }
            gltfProviders.forEach { it.destroy() }
            materialInstances.forEach { e.destroyMaterialInstance(it) }
            materials.forEach { e.destroyMaterial(it) }
            scene?.let { e.destroyScene(it) }
            e.flushAndWait()
            e.destroy()
        }
        gltfAssets.clear()
        gltfLoaders.clear()
        gltfProviders.clear()
        materialInstances.clear()
        materials.clear()
        scene = null
        engine = null
    }

    /**
     * Loads a glb into a ready [GltfAsset] **synchronously on the calling (test) thread**, or null
     * when no engine is available. The asset, its loader and its material provider are destroyed in
     * [tearDown].
     *
     * Deliberately not `rememberGltfAsset`: that uploads resources from a `LaunchedEffect`, which
     * Compose runs on the UI dispatcher (`AWT-EventQueue-0` on JVM desktop) while this fixture
     * creates the [Engine] on the JUnit worker thread. gltfio's `ResourceLoader` is thread-affine,
     * so the mismatch trips a native `PreconditionPanic` that takes the whole test process down with
     * SIGABRT. Real apps never hit it — `rememberFilamentEngine` creates the engine on the same
     * dispatcher the effects run on — so it is a harness artifact, not a library bug. Loading here
     * keeps every engine call on one thread and leaves the composable under test to be the only
     * thing being exercised.
     */
    protected fun gltfAsset(bytes: ByteArray): GltfAsset? {
        val e = engine ?: return null
        if (bytes.isEmpty()) return null
        Gltfio.init()
        val provider = UbershaderProvider(e).also { gltfProviders += it }
        val loader = AssetLoader.create(e, provider, e.entityManager).also { gltfLoaders += it }
        val filamentAsset = loader.createAsset(bytes)?.also { gltfAssets += it } ?: return null

        val resourceLoader = ResourceLoader(e, true)
        try {
            resourceLoader.asyncBeginLoad(filamentAsset)
            // Bounded: the embedded glb has no external URIs, so this settles in a few iterations —
            // the guard just stops a stuck load from hanging the suite.
            var guard = 0
            while (resourceLoader.asyncGetLoadProgress() < 1.0f && guard++ < 1000) {
                resourceLoader.asyncUpdateLoad()
            }
        } finally {
            resourceLoader.destroy()
        }
        return GltfAsset(filamentAsset, loader).apply { isReady = true }
    }

    /**
     * Builds a [MaterialInstance] from the bundled emissive material, or null when the material bytes
     * aren't available on this target (JS/native/android — see [TestMaterials]). The returned instance
     * and its base material are destroyed in [tearDown], outliving any composable that uses them.
     */
    protected fun materialInstance(): MaterialInstance? {
        val e = engine ?: return null
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return null
        val material = Material.Builder().payload(bytes).build(e).also { materials += it }
        return material.createInstance().also { materialInstances += it }
    }
}
