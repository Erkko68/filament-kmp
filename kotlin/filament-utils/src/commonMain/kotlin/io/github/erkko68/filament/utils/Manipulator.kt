package io.github.erkko68.filament.utils

import io.github.erkko68.filament.Camera
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.interop.*
import io.github.erkko68.filament.InternalFilamentApi

/** The axis held constant when the viewport changes in MAP mode. */
typealias Fov = Camera.Fov

/**
 * Helper that enables camera interaction similar to sketchfab or Google Maps.
 *
 * Clients notify the manipulator of various mouse or touch events, then periodically call
 * [getLookAt] so that they can adjust their camera. Three modes are supported: [Mode.ORBIT],
 * [Mode.MAP], and [Mode.FLIGHT]. To construct a manipulator, use [Builder] and pass the desired
 * mode to [Builder.build].
 *
 * Usage example:
 * ```kotlin
 * val manip = Manipulator.Builder()
 *     .viewport(1024, 768)
 *     .build(Manipulator.Mode.ORBIT)
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

    /** Camera manipulation mode. */
    enum class Mode {
        /** Rotates and dollies around a target point. */
        ORBIT,
        /** Pans and zooms over a flat ground plane. */
        MAP,
        /** First-person free-flight camera. */
        FLIGHT
    }

    /**
     * Keys used to translate the camera in [Mode.FLIGHT] mode.
     *
     * FORWARD and BACKWARD dolly the camera forwards and backwards.
     * LEFT and RIGHT strafe the camera left and right.
     * UP and DOWN boom the camera upwards and downwards.
     */
    enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    /**
     * Builder for [Manipulator] instances.
     */
    class Builder() {
        init { Filament.init() } // usable before any Engine exists
        private val nativeBuilder = FilaManipulatorBuilder_create()

        /** Width and height of the viewing area. */
        fun viewport(width: Int, height: Int): Builder {
            FilaManipulatorBuilder_viewport(nativeBuilder, width, height)
            return this
        }

        /** World-space position of interest; defaults to (0, 0, 0). */
        fun targetPosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_targetPosition(nativeBuilder, x, y, z)
            return this
        }

        /** Orientation for the home position; defaults to (0, 1, 0). */
        fun upVector(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_upVector(nativeBuilder, x, y, z)
            return this
        }

        /** Multiplied with scroll delta; defaults to 0.01. */
        fun zoomSpeed(speed: Float): Builder {
            FilaManipulatorBuilder_zoomSpeed(nativeBuilder, speed)
            return this
        }

        /** Initial eye position in world space for [Mode.ORBIT]; defaults to (0, 0, 1). */
        fun orbitHomePosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_orbitHomePosition(nativeBuilder, x, y, z)
            return this
        }

        /** Multiplied with viewport delta for [Mode.ORBIT]; defaults to 0.01. */
        fun orbitSpeed(x: Float, y: Float): Builder {
            FilaManipulatorBuilder_orbitSpeed(nativeBuilder, x, y)
            return this
        }

        /** The axis held constant when viewport changes in [Mode.MAP]. */
        fun fovDirection(fov: Fov): Builder {
            FilaManipulatorBuilder_fovDirection(nativeBuilder, fov.ordinal)
            return this
        }

        /** The full field of view in degrees (not half-angle) for [Mode.MAP]. */
        fun fovDegrees(degrees: Float): Builder {
            FilaManipulatorBuilder_fovDegrees(nativeBuilder, degrees)
            return this
        }

        /** The distance to the far plane for [Mode.MAP]. */
        fun farPlane(distance: Float): Builder {
            FilaManipulatorBuilder_farPlane(nativeBuilder, distance)
            return this
        }

        /** The ground size for computing home position in [Mode.MAP]. */
        fun mapExtent(width: Float, height: Float): Builder {
            FilaManipulatorBuilder_mapExtent(nativeBuilder, width, height)
            return this
        }

        /** Constrains the zoom-in level in [Mode.MAP]. */
        fun mapMinDistance(distance: Float): Builder {
            FilaManipulatorBuilder_mapMinDistance(nativeBuilder, distance)
            return this
        }

        /** Initial eye position in world space for [Mode.FLIGHT]; defaults to (0, 0, 0). */
        fun flightStartPosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_flightStartPosition(nativeBuilder, x, y, z)
            return this
        }

        /** Initial pitch and yaw orientation in radians for [Mode.FLIGHT]; defaults to (0, 0). */
        fun flightStartOrientation(pitch: Float, yaw: Float): Builder {
            FilaManipulatorBuilder_flightStartOrientation(nativeBuilder, pitch, yaw)
            return this
        }

        /** Maximum camera speed in world units per second for [Mode.FLIGHT]; defaults to 10. */
        fun flightMaxMoveSpeed(maxSpeed: Float): Builder {
            FilaManipulatorBuilder_flightMaxMoveSpeed(nativeBuilder, maxSpeed)
            return this
        }

        /** Number of speed steps adjustable with the scroll wheel in [Mode.FLIGHT]; defaults to 80. */
        fun flightSpeedSteps(steps: Int): Builder {
            FilaManipulatorBuilder_flightSpeedSteps(nativeBuilder, steps)
            return this
        }

        /** Multiplied with viewport delta for panning in [Mode.FLIGHT]; defaults to (0.01, 0.01). */
        fun flightPanSpeed(x: Float, y: Float): Builder {
            FilaManipulatorBuilder_flightPanSpeed(nativeBuilder, x, y)
            return this
        }

        /**
         * Applies a deceleration to camera movement in [Mode.FLIGHT]; defaults to 0 (no damping).
         *
         * Lower values give slower damping times. A good default is 15. Too high a value may
         * lead to instability.
         */
        fun flightMoveDamping(damping: Float): Builder {
            FilaManipulatorBuilder_flightMoveDamping(nativeBuilder, damping)
            return this
        }

        /** Plane equation (ax + by + cz + d = 0) used as a raycast fallback for grab-and-pan. */
        fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder {
            FilaManipulatorBuilder_groundPlane(nativeBuilder, a, b, c, d)
            return this
        }

        /** Sets whether panning is enabled. */
        fun panning(enabled: Boolean): Builder {
            FilaManipulatorBuilder_panning(nativeBuilder, enabled)
            return this
        }

        /**
         * Creates a new camera manipulator in the specified mode.
         *
         * @param mode the interaction mode: [Mode.ORBIT], [Mode.MAP], or [Mode.FLIGHT]
         * @return a new [Manipulator] instance
         */
        fun build(mode: Mode): Manipulator {
            val handle = FilaManipulatorBuilder_build(nativeBuilder, mode.ordinal)
            FilaManipulatorBuilder_destroy(nativeBuilder)
            return Manipulator(handle)
        }
    }

    /** Destroys the manipulator and releases all resources. */
    fun destroy() {
        FilaManipulator_destroy(nativeHandle)
    }

    /** Same as [destroy]; lets this be used with `use { }` and try-with-resources. */
    override fun close() = destroy()

    /**
     * Gets the immutable mode of the manipulator.
     *
     * @return the [Mode] this manipulator was created with
     */
    val mode: Mode get() = Mode.entries[FilaManipulator_getMode(nativeHandle)]

    /**
     * Sets the viewport dimensions. The manipulator uses this to process grab events and raycasts.
     *
     * @param width the viewport width in pixels
     * @param height the viewport height in pixels
     */
    fun setViewport(width: Int, height: Int) {
        FilaManipulator_setViewport(nativeHandle, width, height)
    }

    /**
     * Gets the current orthonormal basis; this is usually called once per frame.
     *
     * @param outEye float array of size ≥ 3 filled with the eye position in world space
     * @param outTarget float array of size ≥ 3 filled with the target position in world space
     * @param outUp float array of size ≥ 3 filled with the up vector in world space
     */
    fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) = interopScope {
        val eye = toInterop(outEye); val target = toInterop(outTarget); val up = toInterop(outUp)
        FilaManipulator_getLookAt(nativeHandle, eye, target, up)
        eye.fromInterop(outEye); target.fromInterop(outTarget); up.fromInterop(outUp)
    }

    /**
     * Given a viewport coordinate, picks a point in the ground plane, or in the actual scene if
     * a raycast callback was configured.
     *
     * @param x X-coordinate in viewport space
     * @param y Y-coordinate in viewport space
     * @param outResult float array of size ≥ 3 filled with the world-space intersection point
     */
    fun raycast(x: Int, y: Int, outResult: FloatArray) {
        outResult.usePinned { FilaManipulator_raycast(nativeHandle, x, y, it) }
    }

    /**
     * Starts a grabbing session (i.e. the user begins dragging in the viewport).
     *
     * In [Mode.MAP] mode, this starts a panning session.
     * In [Mode.ORBIT] mode, this starts either rotating or strafing.
     * In [Mode.FLIGHT] mode, this starts a nodal panning session.
     *
     * @param x X-coordinate for the point of interest in viewport space
     * @param y Y-coordinate for the point of interest in viewport space
     * @param strafe [Mode.ORBIT] only: if true, starts a translation rather than a rotation
     */
    fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        FilaManipulator_grabBegin(nativeHandle, x, y, strafe)
    }

    /**
     * Updates a grabbing session.
     *
     * Must be called at least once between [grabBegin] and [grabEnd] to dirty the camera.
     *
     * @param x current X-coordinate in viewport space
     * @param y current Y-coordinate in viewport space
     */
    fun grabUpdate(x: Int, y: Int) {
        FilaManipulator_grabUpdate(nativeHandle, x, y)
    }

    /**
     * Ends a grabbing session.
     */
    fun grabEnd() {
        FilaManipulator_grabEnd(nativeHandle)
    }

    /**
     * Signals that a key is now in the down state.
     *
     * In [Mode.FLIGHT] mode, the camera is translated forward/backward and strafed left/right
     * depending on the depressed keys, enabling WASD-style movement.
     *
     * @param key the key that was pressed
     */
    fun keyDown(key: Key) {
        FilaManipulator_keyDown(nativeHandle, key.ordinal)
    }

    /**
     * Signals that a key is now in the up state.
     *
     * @param key the key that was released
     * @see keyDown
     */
    fun keyUp(key: Key) {
        FilaManipulator_keyUp(nativeHandle, key.ordinal)
    }

    /**
     * In [Mode.MAP] and [Mode.ORBIT] modes, dollys the camera along the viewing direction.
     * In [Mode.FLIGHT] mode, adjusts the move speed of the camera.
     *
     * @param x X-coordinate for the point of interest in viewport space; ignored in [Mode.FLIGHT]
     * @param y Y-coordinate for the point of interest in viewport space; ignored in [Mode.FLIGHT]
     * @param delta in [Mode.MAP] and [Mode.ORBIT]: negative means "zoom in", positive means "zoom out";
     *              in [Mode.FLIGHT]: negative means "slower", positive means "faster"
     */
    fun scroll(x: Int, y: Int, delta: Float) {
        FilaManipulator_scroll(nativeHandle, x, y, delta)
    }

    /**
     * Processes input and updates internal state.
     *
     * Must be called once every frame before [getLookAt] is valid.
     *
     * @param deltaTime the amount of time in seconds passed since the previous call to update
     */
    fun update(deltaTime: Float) {
        FilaManipulator_update(nativeHandle, deltaTime)
    }

    /**
     * Gets a handle that can be used to reset the manipulator back to its current position.
     *
     * @return a [Bookmark] representing the current camera state
     * @see jumpToBookmark
     */
    val currentBookmark: Bookmark get() = Bookmark(FilaManipulator_getCurrentBookmark(nativeHandle))

    /**
     * Gets a handle that can be used to reset the manipulator back to its home position.
     *
     * @return a [Bookmark] representing the home camera state
     * @see jumpToBookmark
     */
    val homeBookmark: Bookmark get() = Bookmark(FilaManipulator_getHomeBookmark(nativeHandle))

    /**
     * Sets the manipulator position and orientation back to a previously stashed state.
     *
     * @param bookmark a [Bookmark] obtained from [getCurrentBookmark] or [getHomeBookmark]
     * @see getCurrentBookmark
     * @see getHomeBookmark
     */
    fun jumpToBookmark(bookmark: Bookmark) {
        FilaManipulator_jumpToBookmark(nativeHandle, bookmark.nativeHandle)
    }

    /** Opaque handle to a viewing position and orientation, used to animate the camera. */
    class Bookmark @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) {
        /** The native bookmark, for interop with code calling the Fila* C API directly. */
        @InternalFilamentApi
        val nativeObject: NativePointer get() = nativeHandle
    }
}

@ExternalSymbolName("FilaManipulatorBuilder_create")
private external fun FilaManipulatorBuilder_create(): NativePointer

@ExternalSymbolName("FilaManipulatorBuilder_viewport")
private external fun FilaManipulatorBuilder_viewport(builder: NativePointer, width: Int, height: Int)

@ExternalSymbolName("FilaManipulatorBuilder_targetPosition")
private external fun FilaManipulatorBuilder_targetPosition(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaManipulatorBuilder_upVector")
private external fun FilaManipulatorBuilder_upVector(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaManipulatorBuilder_zoomSpeed")
private external fun FilaManipulatorBuilder_zoomSpeed(builder: NativePointer, speed: Float)

@ExternalSymbolName("FilaManipulatorBuilder_orbitHomePosition")
private external fun FilaManipulatorBuilder_orbitHomePosition(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaManipulatorBuilder_orbitSpeed")
private external fun FilaManipulatorBuilder_orbitSpeed(builder: NativePointer, x: Float, y: Float)

@ExternalSymbolName("FilaManipulatorBuilder_fovDirection")
private external fun FilaManipulatorBuilder_fovDirection(builder: NativePointer, fov: Int)

@ExternalSymbolName("FilaManipulatorBuilder_fovDegrees")
private external fun FilaManipulatorBuilder_fovDegrees(builder: NativePointer, degrees: Float)

@ExternalSymbolName("FilaManipulatorBuilder_farPlane")
private external fun FilaManipulatorBuilder_farPlane(builder: NativePointer, distance: Float)

@ExternalSymbolName("FilaManipulatorBuilder_mapExtent")
private external fun FilaManipulatorBuilder_mapExtent(builder: NativePointer, width: Float, height: Float)

@ExternalSymbolName("FilaManipulatorBuilder_mapMinDistance")
private external fun FilaManipulatorBuilder_mapMinDistance(builder: NativePointer, distance: Float)

@ExternalSymbolName("FilaManipulatorBuilder_flightStartPosition")
private external fun FilaManipulatorBuilder_flightStartPosition(builder: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaManipulatorBuilder_flightStartOrientation")
private external fun FilaManipulatorBuilder_flightStartOrientation(builder: NativePointer, pitch: Float, yaw: Float)

@ExternalSymbolName("FilaManipulatorBuilder_flightMaxMoveSpeed")
private external fun FilaManipulatorBuilder_flightMaxMoveSpeed(builder: NativePointer, maxSpeed: Float)

@ExternalSymbolName("FilaManipulatorBuilder_flightSpeedSteps")
private external fun FilaManipulatorBuilder_flightSpeedSteps(builder: NativePointer, steps: Int)

@ExternalSymbolName("FilaManipulatorBuilder_flightPanSpeed")
private external fun FilaManipulatorBuilder_flightPanSpeed(builder: NativePointer, x: Float, y: Float)

@ExternalSymbolName("FilaManipulatorBuilder_flightMoveDamping")
private external fun FilaManipulatorBuilder_flightMoveDamping(builder: NativePointer, damping: Float)

@ExternalSymbolName("FilaManipulatorBuilder_groundPlane")
private external fun FilaManipulatorBuilder_groundPlane(builder: NativePointer, a: Float, b: Float, c: Float, d: Float)

@ExternalSymbolName("FilaManipulatorBuilder_panning")
private external fun FilaManipulatorBuilder_panning(builder: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaManipulatorBuilder_build")
private external fun FilaManipulatorBuilder_build(builder: NativePointer, mode: Int): NativePointer

@ExternalSymbolName("FilaManipulatorBuilder_destroy")
private external fun FilaManipulatorBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaManipulator_destroy")
private external fun FilaManipulator_destroy(manip: NativePointer)

@ExternalSymbolName("FilaManipulator_getMode")
private external fun FilaManipulator_getMode(manip: NativePointer): Int

@ExternalSymbolName("FilaManipulator_setViewport")
private external fun FilaManipulator_setViewport(manip: NativePointer, width: Int, height: Int)

@ExternalSymbolName("FilaManipulator_getLookAt")
private external fun FilaManipulator_getLookAt(manip: NativePointer, outEye: NativePointer, outTarget: NativePointer, outUp: NativePointer)

@ExternalSymbolName("FilaManipulator_raycast")
private external fun FilaManipulator_raycast(manip: NativePointer, x: Int, y: Int, outResult: NativePointer)

@ExternalSymbolName("FilaManipulator_grabBegin")
private external fun FilaManipulator_grabBegin(manip: NativePointer, x: Int, y: Int, strafe: Boolean)

@ExternalSymbolName("FilaManipulator_grabUpdate")
private external fun FilaManipulator_grabUpdate(manip: NativePointer, x: Int, y: Int)

@ExternalSymbolName("FilaManipulator_grabEnd")
private external fun FilaManipulator_grabEnd(manip: NativePointer)

@ExternalSymbolName("FilaManipulator_keyDown")
private external fun FilaManipulator_keyDown(manip: NativePointer, key: Int)

@ExternalSymbolName("FilaManipulator_keyUp")
private external fun FilaManipulator_keyUp(manip: NativePointer, key: Int)

@ExternalSymbolName("FilaManipulator_scroll")
private external fun FilaManipulator_scroll(manip: NativePointer, x: Int, y: Int, delta: Float)

@ExternalSymbolName("FilaManipulator_update")
private external fun FilaManipulator_update(manip: NativePointer, deltaTime: Float)

@ExternalSymbolName("FilaManipulator_getCurrentBookmark")
private external fun FilaManipulator_getCurrentBookmark(manip: NativePointer): NativePointer

@ExternalSymbolName("FilaManipulator_getHomeBookmark")
private external fun FilaManipulator_getHomeBookmark(manip: NativePointer): NativePointer

@ExternalSymbolName("FilaManipulator_jumpToBookmark")
private external fun FilaManipulator_jumpToBookmark(manip: NativePointer, bookmark: NativePointer)
