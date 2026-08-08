package io.github.erkko68.filament.compose.scene

import io.github.erkko68.filament.utils.Float3
import io.github.erkko68.filament.utils.Quaternion
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import androidx.compose.ui.graphics.Color as ComposeColor

/**
 * Covers the scene value types — [Position], [Direction], [Scale], [Rotation], [Color] — their
 * constructors, in-domain operators, and the [Float3]/[Quaternion]/Compose-UI interop hops.
 *
 * The `copiesFrom…` and `equalityIsByValue…` tests are the important ones: these types exist to be
 * *immutable* stable Compose inputs, so constructing from a mutable filament-utils value must
 * snapshot it rather than alias it, and equal values must compare equal.
 */
class TypesTest {

    /** sRGB channels survive a Compose round trip only to 8-bit precision (1/255 ≈ 0.004). */
    private val composeChannelTolerance = 0.005f

    private fun assertClose(expected: Float, actual: Float, tolerance: Float = 1e-5f) =
        assertTrue(kotlin.math.abs(expected - actual) < tolerance, "expected $expected but was $actual")

    private fun assertClose(expected: Direction, actual: Direction, tolerance: Float = 1e-5f) =
        assertTrue(
            kotlin.math.abs(expected.x - actual.x) < tolerance &&
                kotlin.math.abs(expected.y - actual.y) < tolerance &&
                kotlin.math.abs(expected.z - actual.z) < tolerance,
            "expected $expected but was $actual",
        )

    private fun assertClose(expected: Color, actual: Color, tolerance: Float = 1e-5f) =
        assertTrue(
            kotlin.math.abs(expected.r - actual.r) < tolerance &&
                kotlin.math.abs(expected.g - actual.g) < tolerance &&
                kotlin.math.abs(expected.b - actual.b) < tolerance,
            "expected $expected but was $actual",
        )

    // ── Position ──────────────────────────────────────────────────────────────

    @Test
    fun positionUniformConstructorSetsEveryAxis() {
        assertEquals(Position(2f, 2f, 2f), Position(2f))
    }

    @Test
    fun positionTranslatesByADirection() {
        val p = Position(1f, 2f, 3f)
        assertEquals(Position(11f, 22f, 33f), p + Direction(10f, 20f, 30f))
        assertEquals(Position(0f, 0f, 0f), p - Direction(1f, 2f, 3f))
    }

    @Test
    fun subtractingTwoPositionsGivesTheDisplacementBetweenThem() {
        val displacement: Direction = Position(4f, 6f, 8f) - Position(1f, 2f, 3f)
        assertEquals(Direction(3f, 4f, 5f), displacement)
    }

    @Test
    fun positionCopiesFromAFloat3RatherThanAliasingIt() {
        val source = Float3(1f, 2f, 3f)
        val p = Position(source)
        source.x = 99f
        assertEquals(Position(1f, 2f, 3f), p)
    }

    // ── Direction ─────────────────────────────────────────────────────────────

    @Test
    fun directionSupportsVectorArithmetic() {
        val a = Direction(1f, 2f, 3f)
        assertEquals(Direction(3f, 5f, 7f), a + Direction(2f, 3f, 4f))
        assertEquals(Direction(-1f, -1f, -1f), a - Direction(2f, 3f, 4f))
        assertEquals(Direction(2f, 4f, 6f), a * 2f)
        assertEquals(Direction(0.5f, 1f, 1.5f), a / 2f)
        assertEquals(Direction(-1f, -2f, -3f), -a)
    }

    @Test
    fun lengthIsEuclidean() {
        assertClose(5f, Direction(3f, 4f, 0f).length)
        assertClose(0f, Direction(0f, 0f, 0f).length)
    }

    @Test
    fun normalizedProducesAUnitVector() {
        val n = Direction(0f, 3f, 4f).normalized()
        assertClose(1f, n.length)
        assertClose(0.6f, n.y)
        assertClose(0.8f, n.z)
    }

    @Test
    fun normalizingAZeroVectorReturnsItUnchangedInsteadOfNaN() {
        assertEquals(Direction(0f, 0f, 0f), Direction(0f, 0f, 0f).normalized())
    }

    @Test
    fun directionCopiesFromAFloat3RatherThanAliasingIt() {
        val source = Float3(1f, 2f, 3f)
        val d = Direction(source)
        source.y = 99f
        assertEquals(Direction(1f, 2f, 3f), d)
    }

    // ── Scale ─────────────────────────────────────────────────────────────────

    @Test
    fun scaleUniformConstructorSetsEveryAxis() {
        assertEquals(Scale(3f, 3f, 3f), Scale(3f))
    }

    @Test
    fun scaleMultipliesByScalarAndComponentWise() {
        assertEquals(Scale(2f, 4f, 6f), Scale(1f, 2f, 3f) * 2f)
        assertEquals(Scale(2f, 6f, 12f), Scale(1f, 2f, 3f) * Scale(2f, 3f, 4f))
    }

    @Test
    fun scaleCopiesFromAFloat3RatherThanAliasingIt() {
        val source = Float3(1f, 2f, 3f)
        val s = Scale(source)
        source.z = 99f
        assertEquals(Scale(1f, 2f, 3f), s)
    }

    // ── Rotation ──────────────────────────────────────────────────────────────

    @Test
    fun identityLeavesADirectionUntouched() {
        assertClose(Direction(0f, 0f, 1f), Rotation.Identity * Direction(0f, 0f, 1f))
    }

    @Test
    fun axisAngleRotatesRightHandedAboutY() {
        // +90° about +Y takes +Z to +X.
        val r = Rotation.axisAngle(Direction(0f, 1f, 0f), 90f)
        assertClose(Direction(1f, 0f, 0f), r * Direction(0f, 0f, 1f))
    }

    @Test
    fun axisAngleNormalizesItsAxis() {
        val unit = Rotation.axisAngle(Direction(0f, 1f, 0f), 90f)
        val long = Rotation.axisAngle(Direction(0f, 7.5f, 0f), 90f)
        assertClose(unit * Direction(0f, 0f, 1f), long * Direction(0f, 0f, 1f))
    }

    @Test
    fun eulerAnglesMapToTheirNamedAxes() {
        // yaw is about Y, pitch about X, roll about Z — each must match the equivalent axisAngle.
        assertClose(
            Rotation.axisAngle(Direction(0f, 1f, 0f), 90f) * Direction(0f, 0f, 1f),
            Rotation.euler(yaw = 90f) * Direction(0f, 0f, 1f),
        )
        assertClose(
            Rotation.axisAngle(Direction(1f, 0f, 0f), 90f) * Direction(0f, 0f, 1f),
            Rotation.euler(pitch = 90f) * Direction(0f, 0f, 1f),
        )
        assertClose(
            Rotation.axisAngle(Direction(0f, 0f, 1f), 90f) * Direction(1f, 0f, 0f),
            Rotation.euler(roll = 90f) * Direction(1f, 0f, 0f),
        )
    }

    @Test
    fun composingRotationsIsAQuaternionProduct() {
        val yaw90 = Rotation.axisAngle(Direction(0f, 1f, 0f), 90f)
        // Two quarter turns about Y take +Z to -Z — a component-wise `times` would not.
        assertClose(Direction(0f, 0f, -1f), (yaw90 * yaw90) * Direction(0f, 0f, 1f))
    }

    @Test
    fun inverseUndoesTheRotation() {
        val r = Rotation.euler(pitch = 25f, yaw = 40f, roll = 15f)
        assertClose(Direction(0f, 0f, 1f), r.inverse() * (r * Direction(0f, 0f, 1f)))
    }

    @Test
    fun fromToGivesTheShortestArcBetweenTwoDirections() {
        val r = Rotation.fromTo(Direction(1f, 0f, 0f), Direction(0f, 1f, 0f))
        assertClose(Direction(0f, 1f, 0f), r * Direction(1f, 0f, 0f))
        assertClose(90f, r.angleTo(Rotation.Identity), 1e-3f)
    }

    @Test
    fun lookTowardsAimsLocalMinusZAlongTheTarget() {
        // The documented contract: local −Z (the glTF/Filament forward axis) ends up on `forward`.
        val forward = Direction(1f, 0f, 0f)
        assertClose(forward, Rotation.lookTowards(forward) * Direction(0f, 0f, -1f))
    }

    @Test
    fun lookTowardsKeepsLocalUpNearTheGivenUp() {
        val r = Rotation.lookTowards(Direction(1f, 0f, 0f))
        assertClose(Direction(0f, 1f, 0f), r * Direction(0f, 1f, 0f))
    }

    @Test
    fun lookTowardsDoesNotProduceNaNWhenForwardIsParallelToUp() {
        // Straight up would leave the basis degenerate; a NaN transform silently hides the entity.
        val r = Rotation.lookTowards(Direction(0f, 1f, 0f))
        assertTrue(!r.x.isNaN() && !r.y.isNaN() && !r.z.isNaN() && !r.w.isNaN(), "produced NaN: $r")
        assertClose(Direction(0f, 1f, 0f), r * Direction(0f, 0f, -1f))
    }

    @Test
    fun slerpHitsBothEndpointsAndTheMidpoint() {
        val a = Rotation.Identity
        val b = Rotation.axisAngle(Direction(0f, 1f, 0f), 90f)
        assertClose(Direction(0f, 0f, 1f), Rotation.slerp(a, b, 0f) * Direction(0f, 0f, 1f))
        assertClose(Direction(1f, 0f, 0f), Rotation.slerp(a, b, 1f) * Direction(0f, 0f, 1f))
        // Constant rate: halfway through a 90° turn is the 45° orientation.
        assertClose(45f, Rotation.slerp(a, b, 0.5f).angleTo(a), 1e-3f)
    }

    @Test
    fun nlerpHitsBothEndpoints() {
        val a = Rotation.Identity
        val b = Rotation.axisAngle(Direction(0f, 1f, 0f), 90f)
        assertClose(Direction(0f, 0f, 1f), Rotation.nlerp(a, b, 0f) * Direction(0f, 0f, 1f))
        assertClose(Direction(1f, 0f, 0f), Rotation.nlerp(a, b, 1f) * Direction(0f, 0f, 1f))
    }

    @Test
    fun toEulerRoundTripsThroughEuler() {
        val e = Rotation.euler(pitch = 20f, yaw = 35f, roll = 10f).toEuler()
        assertClose(20f, e.x, 1e-3f)
        assertClose(35f, e.y, 1e-3f)
        assertClose(10f, e.z, 1e-3f)
    }

    @Test
    fun angleToMeasuresTheSmallestAngleInDegrees() {
        val a = Rotation.axisAngle(Direction(0f, 1f, 0f), 10f)
        val b = Rotation.axisAngle(Direction(0f, 1f, 0f), 100f)
        assertClose(90f, a.angleTo(b), 1e-3f)
        assertClose(0f, a.angleTo(a), 1e-3f)
    }

    @Test
    fun normalizedProducesAUnitLengthRotation() {
        fun length(r: Rotation) = kotlin.math.sqrt(r.x * r.x + r.y * r.y + r.z * r.z + r.w * r.w)

        // An off-unit rotation (what drift from accumulated products looks like) is pulled back.
        val drifted = Rotation(0.3f, 0.4f, 0.5f, 0.6f)
        assertTrue(kotlin.math.abs(length(drifted) - 1f) > 1e-3f, "fixture should not already be unit")
        assertClose(1f, length(drifted.normalized()))

        // An already-unit rotation is left alone.
        val r = Rotation.axisAngle(Direction(0f, 1f, 0f), 30f)
        assertClose(Direction(0f, 0f, 1f).let { r * it }, r.normalized() * Direction(0f, 0f, 1f))
    }

    @Test
    fun rotationCopiesFromAQuaternionRatherThanAliasingIt() {
        val source = Quaternion(0f, 0f, 0f, 1f)
        val r = Rotation(source)
        source.x = 99f
        assertEquals(Rotation.Identity, r)
    }

    @Test
    fun quaternionInteropRoundTrips() {
        val q = Quaternion.fromAxisAngle(Float3(0f, 1f, 0f), 33f)
        assertEquals(Rotation(q), q.toRotation())
        assertEquals(q, Rotation(q).toQuaternion())
    }

    @Test
    fun toRotationMatrixIsColumnMajorAndRotatesTheBasis() {
        // +90° about +Y: +X ends up on −Z (column 0) and +Z on +X (column 2).
        val m = Rotation.axisAngle(Direction.Up, 90f).toRotationMatrix()
        assertEquals(9, m.size)
        assertClose(Direction(0f, 0f, -1f), Direction(m[0], m[1], m[2]))
        assertClose(Direction(0f, 1f, 0f), Direction(m[3], m[4], m[5]))
        assertClose(Direction(1f, 0f, 0f), Direction(m[6], m[7], m[8]))
    }

    // ── Rotation: length-of-input regressions ─────────────────────────────────
    // These builders take Directions, and a Direction is as often a displacement (`target - eye`)
    // as a unit vector. Each of them used to quietly assume unit length.

    @Test
    fun fromToDependsOnlyOnDirectionNotLength() {
        val unit = Rotation.fromTo(Direction(1f, 0f, 0f), Direction(0f, 1f, 0f))
        val scaled = Rotation.fromTo(Direction(3f, 0f, 0f), Direction(0f, 5f, 0f))
        // Un-normalized inputs used to give 172°, not 90°, for this quarter turn.
        assertClose(90f, scaled.angleTo(Rotation.Identity), 1e-3f)
        assertClose(unit * Direction.Forward, scaled * Direction.Forward, 1e-4f)
    }

    @Test
    fun lookTowardsDependsOnlyOnUpDirectionNotLength() {
        // A +Z up, so that the old fallback axis (+Y) is visibly the wrong answer.
        val forward = Direction(0f, 0.8f, 0.6f)
        val unit = Rotation.lookTowards(forward, Direction(0f, 0f, 1f))
        // dot(forward, up) is 1.2 here — over the parallel threshold, so this used to be taken
        // for a degenerate basis and rolled onto the substitute up axis instead.
        val scaled = Rotation.lookTowards(forward, Direction(0f, 0f, 2f))
        assertClose(unit * Direction.Up, scaled * Direction.Up, 1e-4f)
        assertClose(0f, unit.angleTo(scaled), 1e-3f)
    }

    @Test
    fun axisAngleWithAZeroAxisIsIdentityRatherThanNaN() {
        assertEquals(Rotation.Identity, Rotation.axisAngle(Direction.Zero, 45f))
    }

    // ── Rotation: drifted (off-unit) rotations ────────────────────────────────

    @Test
    fun inverseUndoesADriftedRotation() {
        // Twice the length of a 90°-about-Y rotation: same orientation, off-unit components.
        val drifted = Rotation.axisAngle(Direction.Up, 90f).let { Rotation(it.x * 2f, it.y * 2f, it.z * 2f, it.w * 2f) }
        assertClose(Direction.Back, drifted.inverse() * (drifted * Direction.Back), 1e-4f)
    }

    @Test
    fun rotatingByADriftedRotationDoesNotScaleTheVector() {
        val drifted = Rotation.axisAngle(Direction.Up, 90f).let { Rotation(it.x * 2f, it.y * 2f, it.z * 2f, it.w * 2f) }
        // A bare `2` in the rotate formula would return (4, 0, 0) here.
        assertClose(Direction(1f, 0f, 0f), drifted * Direction.Back, 1e-4f)
    }

    @Test
    fun nlerpTakesTheShortWayRound() {
        // 350° about Y is a 10° turn the other way; its quaternion faces the opposite hemisphere
        // from identity, so an unflipped lerp would sweep 175° instead of 5°.
        val a = Rotation.Identity
        val b = Rotation.axisAngle(Direction.Up, 350f)
        assertClose(5f, Rotation.nlerp(a, b, 0.5f).angleTo(a), 1e-2f)
    }

    // ── Direction constants ───────────────────────────────────────────────────

    @Test
    fun directionConstantsNameTheFilamentAxes() {
        assertEquals(Direction(0f, 1f, 0f), Direction.Up)
        assertEquals(Direction(0f, -1f, 0f), Direction.Down)
        assertEquals(Direction(1f, 0f, 0f), Direction.Right)
        assertEquals(Direction(-1f, 0f, 0f), Direction.Left)
        assertEquals(Direction(0f, 0f, 1f), Direction.Back)
        assertEquals(Direction(0f, 0f, 0f), Direction.Zero)
        // Forward is −Z, which is exactly the axis lookTowards aims: aiming at it is a no-op.
        assertEquals(Direction(0f, 0f, -1f), Direction.Forward)
        assertClose(0f, Rotation.lookTowards(Direction.Forward).angleTo(Rotation.Identity), 1e-3f)
    }

    // ── Color ─────────────────────────────────────────────────────────────────

    @Test
    fun colorUniformConstructorMakesGrey() {
        assertEquals(Color(0.5f, 0.5f, 0.5f), Color(0.5f))
    }

    @Test
    fun colorSupportsScalingAndAdding() {
        assertClose(Color(0.2f, 0.4f, 0.6f), Color(0.1f, 0.2f, 0.3f) * 2f)
        assertClose(Color(0.3f, 0.5f, 0.7f), Color(0.1f, 0.2f, 0.3f) + Color(0.2f, 0.3f, 0.4f))
    }

    @Test
    fun colorMapsFloat3ComponentsToRgbInOrder() {
        assertEquals(Color(0.1f, 0.2f, 0.3f), Float3(0.1f, 0.2f, 0.3f).toColor())
        assertEquals(Float3(0.1f, 0.2f, 0.3f), Color(0.1f, 0.2f, 0.3f).toFloat3())
    }

    @Test
    fun composeColorInteropRoundTripsAndDropsAlpha() {
        val scene = Color(ComposeColor(0.25f, 0.5f, 0.75f, alpha = 0.5f))
        assertClose(0.25f, scene.r, composeChannelTolerance)
        assertClose(0.5f, scene.g, composeChannelTolerance)
        assertClose(0.75f, scene.b, composeChannelTolerance)

        val back = scene.toComposeColor()
        assertClose(0.25f, back.red, composeChannelTolerance)
        assertClose(1f, back.alpha, composeChannelTolerance)
    }

    @Test
    fun toComposeColorClampsOutOfRangeChannels() {
        // Emissive/HDR scene colours legitimately exceed 1.0; Compose UI colours cannot.
        val clamped = Color(4f, -0.5f, 0.5f).toComposeColor()
        assertClose(1f, clamped.red, composeChannelTolerance)
        assertClose(0f, clamped.green, composeChannelTolerance)
        assertClose(0.5f, clamped.blue, composeChannelTolerance)
    }

    // ── Cross-type ────────────────────────────────────────────────────────────

    @Test
    fun float3HopsProduceTheMatchingType() {
        val f = Float3(1f, 2f, 3f)
        assertEquals(Position(1f, 2f, 3f), f.toPosition())
        assertEquals(Direction(1f, 2f, 3f), f.toDirection())
        assertEquals(Scale(1f, 2f, 3f), f.toScale())
        assertEquals(f, Position(1f, 2f, 3f).toFloat3())
    }

    @Test
    fun equalityIsByValueSoRecompositionCanSkip() {
        // The reason these are data classes and not typealiases for the mutable Float3/Quaternion:
        // two separately allocated but equal values must compare equal for Compose to skip.
        assertEquals(Position(1f, 2f, 3f), Position(1f, 2f, 3f))
        assertEquals(Scale(1f), Scale(1f, 1f, 1f))
        assertEquals(
            Rotation.axisAngle(Direction(0f, 1f, 0f), 30f),
            Rotation.axisAngle(Direction(0f, 1f, 0f), 30f),
        )
        assertNotEquals(Position(1f, 2f, 3f), Position(1f, 2f, 4f))
        assertNotEquals(
            Rotation.axisAngle(Direction(0f, 1f, 0f), 30f),
            Rotation.axisAngle(Direction(0f, 1f, 0f), 31f),
        )
    }
}
