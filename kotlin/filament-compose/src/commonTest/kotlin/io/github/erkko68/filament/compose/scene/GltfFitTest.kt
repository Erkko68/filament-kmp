package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.testutils.ComposeTestFixture
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.withFilamentScene
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertTrue

/** [GltfInstance]'s `fitToSize`: the bounds' centre lands on `position` and their largest side becomes `fitToSize × scale`; and [GltfAsset]'s bounds. */
class GltfFitTest : ComposeTestFixture() {

    private class Placed(val root: FloatArray, val centre: FloatArray, val side: Float) {
        /** [p] through the root's column-major transform. */
        fun apply(p: FloatArray) = FloatArray(3) { r -> root[r] * p[0] + root[4 + r] * p[1] + root[8 + r] * p[2] + root[12 + r] }

        /** The uniform scale the root applies: column 0's length. */
        val factor get() = sqrt(root[0] * root[0] + root[1] * root[1] + root[2] * root[2])
    }

    /**
     * Mounts [content] and runs [check] on where it placed the duck. [check] runs inside the harness and the
     * harness result is returned: on web the harness is asynchronous (see `composeScene`).
     */
    @OptIn(ExperimentalTestApi::class)
    private fun place(
        content: @Composable FilamentSceneScope.(GltfAsset, (GltfInstanceScope) -> Unit) -> Unit,
        check: (Placed) -> Unit,
    ) = run {
        val asset = gltfAsset(TestGlb.getDuckGlbBytes())
        val box = asset.filamentAsset.boundingBox
        val half = box.extent()
        withFilamentScene(engine, scene) { setContent ->
            var root: FloatArray? = null
            setContent { content(asset) { scope -> root = scope.engine.transformManager.let { tm -> tm.getTransform(tm.getInstance(scope.instance.root)) } } }
            waitForIdle()
            check(Placed(checkNotNull(root), box.center(), 2f * maxOf(half[0], half[1], half[2])))
        }
    }

    private fun assertNear(expected: Float, actual: Float, what: String) =
        assertTrue(abs(expected - actual) < 1e-3f * maxOf(1f, abs(expected)), "$what: expected $expected, got $actual")

    private fun assertAt(expected: Position, actual: FloatArray, what: String) {
        assertNear(expected.x, actual[0], "$what x"); assertNear(expected.y, actual[1], "$what y"); assertNear(expected.z, actual[2], "$what z")
    }

    @Test
    fun largestSideBecomesFitToSizeCentredOnPosition() =
        place({ duck, read -> GltfInstance(duck, position = Position(1f, 2f, 3f), fitToSize = 2f, onUpdate = { read(this) }) }) { placed ->
        assertAt(Position(1f, 2f, 3f), placed.apply(placed.centre), "centre")
        assertNear(2f, placed.factor * placed.side, "largest side")
    }

    @Test
    fun scaleAndRotationApplyAfterTheFit() = place({ duck, read ->
        GltfInstance(duck, position = Position(1f, 0f, 0f), rotation = Rotation.euler(yaw = 90f), scale = Scale(3f),
            fitToSize = 2f, onUpdate = { read(this) })
    }) { placed ->
        assertAt(Position(1f, 0f, 0f), placed.apply(placed.centre), "centre")
        assertNear(6f, placed.factor * placed.side, "largest side")
    }

    /** The pivot is in fitted units: one unit off the centre is 1/f in mesh space. */
    @Test
    fun pivotIsInFittedSpace() = place({ duck, read ->
        GltfInstance(duck, position = Position(0f), pivot = Position(1f, 0f, 0f), fitToSize = 2f, onUpdate = { read(this) })
    }) { placed ->
        val f = 2f / placed.side
        val pivotInMesh = floatArrayOf(placed.centre[0] + 1f / f, placed.centre[1], placed.centre[2])
        assertAt(Position(0f), placed.apply(pivotInMesh), "pivot")
    }

    /** `center`/`radius` are gltfio's asset box: its centre, and the half-diagonal that reaches every corner. */
    @Test
    fun centerAndRadiusBoundTheAssetBox() {
        val duck = gltfAsset(TestGlb.getDuckGlbBytes())
        val box = duck.filamentAsset.boundingBox
        val c = box.center()
        val h = box.extent()
        assertAt(Position(c[0], c[1], c[2]), floatArrayOf(duck.center.x, duck.center.y, duck.center.z), "center")
        assertNear(sqrt(h[0] * h[0] + h[1] * h[1] + h[2] * h[2]), duck.radius, "radius")
    }

    @Test
    fun nullKeepsTheModelAsAuthored() = place({ duck, read -> GltfInstance(duck, onUpdate = { read(this) }) }) { placed ->
        assertAt(Position(placed.centre[0], placed.centre[1], placed.centre[2]), placed.apply(placed.centre), "centre")
        assertNear(1f, placed.factor, "scale")
    }
}
