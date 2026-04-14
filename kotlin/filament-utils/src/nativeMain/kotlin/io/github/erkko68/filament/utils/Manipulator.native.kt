@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.cinterop.*
import kotlinx.cinterop.*
import cnames.structs.FilaManipulator
import cnames.structs.FilaBookmark

actual class Manipulator internal constructor(internal val nativeHandle: CPointer<FilaManipulator>) {

    actual enum class Mode {
        ORBIT, MAP, FLIGHT
    }

    actual enum class Fov {
        VERTICAL, HORIZONTAL
    }

    actual enum class Key {
        FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN
    }

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
            FilaManipulatorBuilder_fovDirection(nativeBuilder, fov.ordinal.toUInt())
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
            val handle = FilaManipulatorBuilder_build(nativeBuilder, mode.ordinal.toUInt())!!
            FilaManipulatorBuilder_destroy(nativeBuilder)
            return Manipulator(handle)
        }
    }

    actual fun destroy() {
        FilaManipulator_destroy(nativeHandle)
    }

    actual fun getMode(): Mode = Mode.entries[FilaManipulator_getMode(nativeHandle).toInt()]

    actual fun setViewport(width: Int, height: Int) {
        FilaManipulator_setViewport(nativeHandle, width, height)
    }

    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) {
        memScoped {
            val eye = allocArray<FloatVar>(3)
            val target = allocArray<FloatVar>(3)
            val up = allocArray<FloatVar>(3)
            FilaManipulator_getLookAt(nativeHandle, eye, target, up)
            for (i in 0 until 3) {
                outEye[i] = eye[i]
                outTarget[i] = target[i]
                outUp[i] = up[i]
            }
        }
    }

    actual fun raycast(x: Int, y: Int, outResult: FloatArray) {
        memScoped {
            val result = allocArray<FloatVar>(3)
            FilaManipulator_raycast(nativeHandle, x, y, result)
            for (i in 0 until 3) {
                outResult[i] = result[i]
            }
        }
    }

    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        FilaManipulator_grabBegin(nativeHandle, x, y, strafe)
    }

    actual fun grabUpdate(x: Int, y: Int) {
        FilaManipulator_grabUpdate(nativeHandle, x, y)
    }

    actual fun grabEnd() {
        FilaManipulator_grabEnd(nativeHandle)
    }

    actual fun keyDown(key: Key) {
        FilaManipulator_keyDown(nativeHandle, key.ordinal.toUInt())
    }

    actual fun keyUp(key: Key) {
        FilaManipulator_keyUp(nativeHandle, key.ordinal.toUInt())
    }

    actual fun scroll(x: Int, y: Int, delta: Float) {
        FilaManipulator_scroll(nativeHandle, x, y, delta)
    }

    actual fun update(deltaTime: Float) {
        FilaManipulator_update(nativeHandle, deltaTime)
    }

    actual fun getCurrentBookmark(): Bookmark = Bookmark(FilaManipulator_getCurrentBookmark(nativeHandle)!!)

    actual fun getHomeBookmark(): Bookmark = Bookmark(FilaManipulator_getHomeBookmark(nativeHandle)!!)

    actual fun jumpToBookmark(bookmark: Bookmark) {
        FilaManipulator_jumpToBookmark(nativeHandle, bookmark.nativeHandle)
    }

    actual class Bookmark internal constructor(internal val nativeHandle: CPointer<FilaBookmark>)
}
