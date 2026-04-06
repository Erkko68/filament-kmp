package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class Manipulator internal constructor(
    internal val nativeObject: CPointer<FilaManipulator>
) {
    actual enum class Mode { ORBIT, MAP, FREE_FLIGHT }
    actual enum class Fov { VERTICAL, HORIZONTAL }
    actual enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaManipulatorBuilder_create()!!

        actual fun viewport(width: Int, height: Int): Builder {
            FilaManipulatorBuilder_viewport(nativeBuilder, width, height)
            return this
        }

        actual fun targetPosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_targetPosition(nativeBuilder, x, y, z)
            return this
        }

        actual fun upVector(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_upVector(nativeBuilder, x, y, z)
            return this
        }

        actual fun zoomSpeed(speed: Float): Builder {
            FilaManipulatorBuilder_zoomSpeed(nativeBuilder, speed)
            return this
        }

        actual fun orbitHomePosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_orbitHomePosition(nativeBuilder, x, y, z)
            return this
        }

        actual fun orbitSpeed(x: Float, y: Float): Builder {
            FilaManipulatorBuilder_orbitSpeed(nativeBuilder, x, y)
            return this
        }

        actual fun fovDirection(fov: Fov): Builder {
            val nativeFov = when (fov) {
                Fov.VERTICAL -> FILA_MANIPULATOR_FOV_VERTICAL
                Fov.HORIZONTAL -> FILA_MANIPULATOR_FOV_HORIZONTAL
            }
            FilaManipulatorBuilder_fovDirection(nativeBuilder, nativeFov)
            return this
        }

        actual fun fovDegrees(degrees: Float): Builder {
            FilaManipulatorBuilder_fovDegrees(nativeBuilder, degrees)
            return this
        }

        actual fun farPlane(distance: Float): Builder {
            FilaManipulatorBuilder_farPlane(nativeBuilder, distance)
            return this
        }

        actual fun mapExtent(width: Float, height: Float): Builder {
            FilaManipulatorBuilder_mapExtent(nativeBuilder, width, height)
            return this
        }

        actual fun mapMinDistance(distance: Float): Builder {
            FilaManipulatorBuilder_mapMinDistance(nativeBuilder, distance)
            return this
        }

        actual fun flightStartPosition(x: Float, y: Float, z: Float): Builder {
            FilaManipulatorBuilder_flightStartPosition(nativeBuilder, x, y, z)
            return this
        }

        actual fun flightStartOrientation(pitch: Float, yaw: Float): Builder {
            FilaManipulatorBuilder_flightStartOrientation(nativeBuilder, pitch, yaw)
            return this
        }

        actual fun flightMaxMoveSpeed(maxSpeed: Float): Builder {
            FilaManipulatorBuilder_flightMaxMoveSpeed(nativeBuilder, maxSpeed)
            return this
        }

        actual fun flightSpeedSteps(steps: Int): Builder {
            FilaManipulatorBuilder_flightSpeedSteps(nativeBuilder, steps)
            return this
        }

        actual fun flightPanSpeed(x: Float, y: Float): Builder {
            FilaManipulatorBuilder_flightPanSpeed(nativeBuilder, x, y)
            return this
        }

        actual fun flightMoveDamping(damping: Float): Builder {
            FilaManipulatorBuilder_flightMoveDamping(nativeBuilder, damping)
            return this
        }

        actual fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder {
            FilaManipulatorBuilder_groundPlane(nativeBuilder, a, b, c, d)
            return this
        }

        actual fun panning(enabled: Boolean): Builder {
            FilaManipulatorBuilder_panning(nativeBuilder, enabled)
            return this
        }

        actual fun build(mode: Mode): Manipulator {
            val nativeMode = when (mode) {
                Mode.ORBIT -> FILA_MANIPULATOR_MODE_ORBIT
                Mode.MAP -> FILA_MANIPULATOR_MODE_MAP
                Mode.FREE_FLIGHT -> FILA_MANIPULATOR_MODE_FLIGHT
            }
            val nativeManipulator = FilaManipulatorBuilder_build(nativeBuilder, nativeMode)
                ?: throw IllegalStateException("Couldn't create Manipulator")
            FilaManipulatorBuilder_destroy(nativeBuilder)
            return Manipulator(nativeManipulator)
        }
    }

    actual class Bookmark(internal val nativeObject: CPointer<FilaBookmark>)

    actual fun getMode(): Mode {
        val nativeMode = FilaManipulator_getMode(nativeObject)
        return when (nativeMode) {
            FILA_MANIPULATOR_MODE_ORBIT -> Mode.ORBIT
            FILA_MANIPULATOR_MODE_MAP -> Mode.MAP
            FILA_MANIPULATOR_MODE_FLIGHT -> Mode.FREE_FLIGHT
            else -> Mode.ORBIT
        }
    }

    actual fun setViewport(width: Int, height: Int) {
        FilaManipulator_setViewport(nativeObject, width, height)
    }

    actual fun getLookAt(eyePosition: FloatArray, targetPosition: FloatArray, upward: FloatArray) {
        memScoped {
            val eye = allocArray<FloatVar>(3)
            val target = allocArray<FloatVar>(3)
            val up = allocArray<FloatVar>(3)
            FilaManipulator_getLookAt(nativeObject, eye, target, up)
            for (i in 0 until 3) {
                eyePosition[i] = eye[i]
                targetPosition[i] = target[i]
                upward[i] = up[i]
            }
        }
    }

    actual fun getLookAt(eyePosition: DoubleArray, targetPosition: DoubleArray, upward: DoubleArray) {
        // ... handled via float for now as our C-wrapper only does float for look-at
    }

    actual fun raycast(x: Int, y: Int): FloatArray? {
        val result = FloatArray(3)
        memScoped {
            val nativeResult = allocArray<FloatVar>(3)
            FilaManipulator_raycast(nativeObject, x, y, nativeResult)
            for (i in 0 until 3) result[i] = nativeResult[i]
        }
        return result
    }

    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        FilaManipulator_grabBegin(nativeObject, x, y, strafe)
    }

    actual fun grabUpdate(x: Int, y: Int) {
        FilaManipulator_grabUpdate(nativeObject, x, y)
    }

    actual fun grabEnd() {
        FilaManipulator_grabEnd(nativeObject)
    }

    actual fun keyDown(key: Key) {
        val nativeKey = when (key) {
            Key.FORWARD -> FILA_MANIPULATOR_KEY_FORWARD
            Key.LEFT -> FILA_MANIPULATOR_KEY_LEFT
            Key.BACKWARD -> FILA_MANIPULATOR_KEY_BACKWARD
            Key.RIGHT -> FILA_MANIPULATOR_KEY_RIGHT
            Key.UP -> FILA_MANIPULATOR_KEY_UP
            Key.DOWN -> FILA_MANIPULATOR_KEY_DOWN
        }
        FilaManipulator_keyDown(nativeObject, nativeKey)
    }

    actual fun keyUp(key: Key) {
        val nativeKey = when (key) {
            Key.FORWARD -> FILA_MANIPULATOR_KEY_FORWARD
            Key.LEFT -> FILA_MANIPULATOR_KEY_LEFT
            Key.BACKWARD -> FILA_MANIPULATOR_KEY_BACKWARD
            Key.RIGHT -> FILA_MANIPULATOR_KEY_RIGHT
            Key.UP -> FILA_MANIPULATOR_KEY_UP
            Key.DOWN -> FILA_MANIPULATOR_KEY_DOWN
        }
        FilaManipulator_keyUp(nativeObject, nativeKey)
    }

    actual fun scroll(x: Int, y: Int, scrollDelta: Float) {
        FilaManipulator_scroll(nativeObject, x, y, scrollDelta)
    }

    actual fun update(deltaTime: Float) {
        FilaManipulator_update(nativeObject, deltaTime)
    }

    actual fun getCurrentBookmark(): Bookmark {
        val nativeBookmark = FilaManipulator_getCurrentBookmark(nativeObject)
            ?: throw IllegalStateException("Couldn't get current bookmark")
        return Bookmark(nativeBookmark)
    }

    actual fun getHomeBookmark(): Bookmark {
        val nativeBookmark = FilaManipulator_getHomeBookmark(nativeObject)
            ?: throw IllegalStateException("Couldn't get home bookmark")
        return Bookmark(nativeBookmark)
    }

    actual fun jumpToBookmark(bookmark: Bookmark) {
        FilaManipulator_jumpToBookmark(nativeObject, bookmark.nativeObject)
    }

    fun destroy() {
        FilaManipulator_destroy(nativeObject)
    }
}
