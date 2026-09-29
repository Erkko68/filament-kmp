package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.utils.capi.*

/** The interaction style of a [Manipulator]. */
enum class Mode {
    /** Rotates and dollies around a target point. */
    ORBIT,
    /** Pans and zooms over a flat ground plane. */
    MAP,
    /** First-person free-flight camera. */
    FREE_FLIGHT,
}

/** The axis held constant when the viewport changes in [Mode.MAP]. */
enum class Fov { VERTICAL, HORIZONTAL }

/**
 * Helper that enables camera interaction similar to sketchfab or Google Maps.
 *
 * Clients notify the manipulator of various mouse or touch events, then periodically call
 * [getLookAt] so that they can adjust their camera. Three modes are supported: [Mode.ORBIT],
 * [Mode.MAP], and [Mode.FREE_FLIGHT]. To construct a manipulator, use [Builder] and pass the
 * desired mode to [Builder.build].
 *
 * Usage example:
 * ```kotlin
 * val manip = Manipulator.Builder()
 *     .viewport(1024, 768)
 *     .build(Mode.ORBIT)
 *
 * // In mouse-down handler:
 * manip.grabBegin(x, y, false)
 *
 * // In mouse-move handler:
 * manip.grabUpdate(x, y)
 *
 * // In mouse-up handler:
 * manip.grabEnd()
 *
 * // Each frame:
 * val eye = FloatArray(3); val target = FloatArray(3); val up = FloatArray(3)
 * manip.getLookAt(eye, target, up)
 * camera.lookAt(eye[0], eye[1], eye[2], target[0], target[1], target[2], up[0], up[1], up[2])
 * ```
 *
 * @see Bookmark
 */
class Manipulator @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) : AutoCloseable {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Keys used to translate the camera in [Mode.FREE_FLIGHT] mode.
     *
     * FORWARD and BACKWARD dolly the camera forwards and backwards.
     * LEFT and RIGHT strafe the camera left and right.
     * UP and DOWN boom the camera upwards and downwards.
     */
    enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    /** Builder for [Manipulator] instances. */
    class Builder() {
        init { Filament.init() } // usable before any Engine exists
        private val nativeBuilder = FilaCamutilsManipulatorBuilder_create()

        /** Width and height of the viewing area. */
        fun viewport(width: Int, height: Int): Builder = apply { FilaCamutilsManipulatorBuilder_viewport(nativeBuilder, width, height) }

        /** World-space position of interest; defaults to (0, 0, 0). */
        fun targetPosition(x: Float, y: Float, z: Float): Builder = apply { FilaCamutilsManipulatorBuilder_targetPosition(nativeBuilder, x, y, z) }

        /** Orientation for the home position; defaults to (0, 1, 0). */
        fun upVector(x: Float, y: Float, z: Float): Builder = apply { FilaCamutilsManipulatorBuilder_upVector(nativeBuilder, x, y, z) }

        /** Multiplied with scroll delta; defaults to 0.01. */
        fun zoomSpeed(value: Float): Builder = apply { FilaCamutilsManipulatorBuilder_zoomSpeed(nativeBuilder, value) }

        /** Initial eye position in world space for [Mode.ORBIT]; defaults to (0, 0, 1). */
        fun orbitHomePosition(x: Float, y: Float, z: Float): Builder = apply { FilaCamutilsManipulatorBuilder_orbitHomePosition(nativeBuilder, x, y, z) }

        /** Multiplied with viewport delta for [Mode.ORBIT]; defaults to 0.01. */
        fun orbitSpeed(x: Float, y: Float): Builder = apply { FilaCamutilsManipulatorBuilder_orbitSpeed(nativeBuilder, x, y) }

        /** The axis held constant when the viewport changes in [Mode.MAP]. */
        fun fovDirection(fov: Fov): Builder = apply { FilaCamutilsManipulatorBuilder_fovDirection(nativeBuilder, fov.ordinal) }

        /** The full field of view in degrees (not half-angle) for [Mode.MAP]. */
        fun fovDegrees(degrees: Float): Builder = apply { FilaCamutilsManipulatorBuilder_fovDegrees(nativeBuilder, degrees) }

        /** The distance to the far plane for [Mode.MAP]. */
        fun farPlane(distance: Float): Builder = apply { FilaCamutilsManipulatorBuilder_farPlane(nativeBuilder, distance) }

        /** The ground size for computing the home position in [Mode.MAP]. */
        fun mapExtent(worldWidth: Float, worldHeight: Float): Builder = apply { FilaCamutilsManipulatorBuilder_mapExtent(nativeBuilder, worldWidth, worldHeight) }

        /** Constrains the zoom-in level in [Mode.MAP]. */
        fun mapMinDistance(mindist: Float): Builder = apply { FilaCamutilsManipulatorBuilder_mapMinDistance(nativeBuilder, mindist) }

        /** Initial eye position in world space for [Mode.FREE_FLIGHT]; defaults to (0, 0, 0). */
        fun flightStartPosition(x: Float, y: Float, z: Float): Builder = apply { FilaCamutilsManipulatorBuilder_flightStartPosition(nativeBuilder, x, y, z) }

        /** Initial pitch and yaw orientation in radians for [Mode.FREE_FLIGHT]; defaults to (0, 0). */
        fun flightStartOrientation(pitch: Float, yaw: Float): Builder = apply { FilaCamutilsManipulatorBuilder_flightStartOrientation(nativeBuilder, pitch, yaw) }

        /** Maximum camera speed in world units per second for [Mode.FREE_FLIGHT]; defaults to 10. */
        fun flightMaxMoveSpeed(maxSpeed: Float): Builder = apply { FilaCamutilsManipulatorBuilder_flightMaxMoveSpeed(nativeBuilder, maxSpeed) }

        /** Number of speed steps adjustable with the scroll wheel in [Mode.FREE_FLIGHT]; defaults to 80. */
        fun flightSpeedSteps(steps: Int): Builder = apply { FilaCamutilsManipulatorBuilder_flightSpeedSteps(nativeBuilder, steps) }

        /** Multiplied with viewport delta for panning in [Mode.FREE_FLIGHT]; defaults to (0.01, 0.01). */
        fun flightPanSpeed(x: Float, y: Float): Builder = apply { FilaCamutilsManipulatorBuilder_flightPanSpeed(nativeBuilder, x, y) }

        /**
         * Applies a deceleration to camera movement in [Mode.FREE_FLIGHT]; defaults to 0 (no damping).
         * Lower values give slower damping times. A good default is 15. Too high a value may lead to instability.
         */
        fun flightMoveDamping(damping: Float): Builder = apply { FilaCamutilsManipulatorBuilder_flightMoveDamping(nativeBuilder, damping) }

        /** Plane equation (ax + by + cz + d = 0) used as a raycast fallback for grab-and-pan. */
        fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder = apply { FilaCamutilsManipulatorBuilder_groundPlane(nativeBuilder, a, b, c, d) }

        /** Sets whether panning is enabled. */
        fun panning(enabled: Boolean): Builder = apply { FilaCamutilsManipulatorBuilder_panning(nativeBuilder, enabled) }

        /** Creates a new camera manipulator in [mode]. The builder can't be used afterwards. */
        fun build(mode: Mode): Manipulator {
            val handle = FilaCamutilsManipulatorBuilder_build(nativeBuilder, mode.ordinal)
            FilaCamutilsManipulatorBuilder_destroy(nativeBuilder)
            return Manipulator(handle)
        }
    }

    /** Destroys the manipulator and releases all resources. */
    fun destroy() = FilaCamutilsManipulator_destroy(nativeHandle)

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /** The immutable mode of the manipulator. */
    val mode: Mode get() = Mode.entries[FilaCamutilsManipulator_getMode(nativeHandle)]

    /** Sets the viewport dimensions. The manipulator uses this to process grab events and raycasts. */
    fun setViewport(width: Int, height: Int) = FilaCamutilsManipulator_setViewport(nativeHandle, width, height)

    /**
     * Gets the current orthonormal basis; this is usually called once per frame. Each array
     * (size ≥ 3) receives a world-space position or direction.
     */
    fun getLookAt(eyePosition: FloatArray, targetPosition: FloatArray, upward: FloatArray) {
        eyePosition.usePinned { e -> targetPosition.usePinned { t -> upward.usePinned { u -> FilaCamutilsManipulator_getLookAt(nativeHandle, e, t, u) } } }
    }

    /**
     * Given a viewport coordinate, picks a point in the ground plane into [result] (size ≥ 3).
     *
     * @return whether the ray hit the ground plane
     */
    fun raycast(x: Int, y: Int, result: FloatArray): Boolean = result.usePinned { FilaCamutilsManipulator_raycast(nativeHandle, x, y, it) }

    /** Given a viewport coordinate, computes a picking ray: its [origin] and direction [dir] (each size ≥ 3). */
    fun getRay(x: Int, y: Int, origin: FloatArray, dir: FloatArray) {
        origin.usePinned { o -> dir.usePinned { d -> FilaCamutilsManipulator_getRay(nativeHandle, x, y, o, d) } }
    }

    /**
     * Starts a grabbing session (i.e. the user begins dragging in the viewport).
     *
     * In [Mode.MAP] mode, this starts a panning session.
     * In [Mode.ORBIT] mode, this starts either rotating or strafing.
     * In [Mode.FREE_FLIGHT] mode, this starts a nodal panning session.
     *
     * @param strafe [Mode.ORBIT] only: if true, starts a translation rather than a rotation
     */
    fun grabBegin(x: Int, y: Int, strafe: Boolean) = FilaCamutilsManipulator_grabBegin(nativeHandle, x, y, strafe)

    /** Updates a grabbing session. Must be called at least once between [grabBegin] and [grabEnd] to dirty the camera. */
    fun grabUpdate(x: Int, y: Int) = FilaCamutilsManipulator_grabUpdate(nativeHandle, x, y)

    /** Ends a grabbing session. */
    fun grabEnd() = FilaCamutilsManipulator_grabEnd(nativeHandle)

    /**
     * Signals that a key is now in the down state.
     *
     * In [Mode.FREE_FLIGHT] mode, the camera is translated forward/backward and strafed
     * left/right depending on the depressed keys, enabling WASD-style movement.
     */
    fun keyDown(key: Key) = FilaCamutilsManipulator_keyDown(nativeHandle, key.ordinal)

    /** Signals that a key is now in the up state. */
    fun keyUp(key: Key) = FilaCamutilsManipulator_keyUp(nativeHandle, key.ordinal)

    /**
     * In [Mode.MAP] and [Mode.ORBIT] modes, dollys the camera along the viewing direction.
     * In [Mode.FREE_FLIGHT] mode, adjusts the move speed of the camera.
     *
     * @param x X-coordinate for the point of interest in viewport space; ignored in [Mode.FREE_FLIGHT]
     * @param y Y-coordinate for the point of interest in viewport space; ignored in [Mode.FREE_FLIGHT]
     * @param scrolldelta in [Mode.MAP] and [Mode.ORBIT]: negative means "zoom in", positive means "zoom out";
     *                    in [Mode.FREE_FLIGHT]: negative means "slower", positive means "faster"
     */
    fun scroll(x: Int, y: Int, scrolldelta: Float) = FilaCamutilsManipulator_scroll(nativeHandle, x, y, scrolldelta)

    /**
     * Processes input and updates internal state. Must be called once every frame before
     * [getLookAt] is valid.
     *
     * @param deltaTime the amount of time in seconds passed since the previous call to update
     */
    fun update(deltaTime: Float) = FilaCamutilsManipulator_update(nativeHandle, deltaTime)

    /** A new [Bookmark] of the current position, for [jumpToBookmark]. */
    val currentBookmark: Bookmark get() = Bookmark().also { FilaCamutilsManipulator_getCurrentBookmark(nativeHandle, it.nativeHandle) }

    /** A new [Bookmark] of the home position, for [jumpToBookmark]. */
    val homeBookmark: Bookmark get() = Bookmark().also { FilaCamutilsManipulator_getHomeBookmark(nativeHandle, it.nativeHandle) }

    /** Sets the manipulator position and orientation back to a stashed state. */
    fun jumpToBookmark(bookmark: Bookmark) = FilaCamutilsManipulator_jumpToBookmark(nativeHandle, bookmark.nativeHandle)
}

/**
 * Opaque memento to a viewing position and orientation (e.g. the "home" camera position), used
 * to track camera animation between waypoints. In map mode this implements Van Wijk interpolation.
 *
 * @see Manipulator.currentBookmark
 * @see Manipulator.jumpToBookmark
 */
class Bookmark @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) : AutoCloseable {
    internal constructor() : this(FilaCamutilsBookmark_create())

    /** The native bookmark, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Frees the bookmark. */
    fun destroy() = FilaCamutilsBookmark_destroy(nativeHandle)

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    companion object {
        /**
         * Interpolates between two bookmarks. [t] must be between 0 and 1 (inclusive), and the
         * two endpoints must have the same mode (ORBIT or MAP).
         */
        fun interpolate(a: Bookmark, b: Bookmark, t: Double): Bookmark =
            Bookmark().also { FilaCamutilsBookmark_interpolate(a.nativeHandle, b.nativeHandle, t, it.nativeHandle) }

        /** Recommends a duration for animation between two MAP endpoints, as a unitless multiplier. */
        fun duration(a: Bookmark, b: Bookmark): Double = FilaCamutilsBookmark_duration(a.nativeHandle, b.nativeHandle)
    }
}
