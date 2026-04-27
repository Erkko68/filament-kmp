package io.github.erkko68.filament.utils

actual class Manipulator internal constructor(internal val androidHandle: com.google.android.filament.utils.Manipulator) {

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
        init { com.google.android.filament.utils.Utils.init() }
        private val builder = com.google.android.filament.utils.Manipulator.Builder()

        actual fun viewport(width: Int, height: Int): Builder {
            builder.viewport(width, height)
            return this
        }

        actual fun targetPosition(x: Float, y: Float, z: Float): Builder {
            builder.targetPosition(x, y, z)
            return this
        }

        actual fun upVector(x: Float, y: Float, z: Float): Builder {
            builder.upVector(x, y, z)
            return this
        }

        actual fun zoomSpeed(speed: Float): Builder {
            builder.zoomSpeed(speed)
            return this
        }

        actual fun orbitHomePosition(x: Float, y: Float, z: Float): Builder {
            builder.orbitHomePosition(x, y, z)
            return this
        }

        actual fun orbitSpeed(x: Float, y: Float): Builder {
            builder.orbitSpeed(x, y)
            return this
        }

        actual fun fovDirection(fov: Fov): Builder {
            builder.fovDirection(com.google.android.filament.utils.Manipulator.Fov.values()[fov.ordinal])
            return this
        }

        actual fun fovDegrees(degrees: Float): Builder {
            builder.fovDegrees(degrees)
            return this
        }

        actual fun farPlane(distance: Float): Builder {
            builder.farPlane(distance)
            return this
        }

        actual fun mapExtent(width: Float, height: Float): Builder {
            builder.mapExtent(width, height)
            return this
        }

        actual fun mapMinDistance(distance: Float): Builder {
            builder.mapMinDistance(distance)
            return this
        }

        actual fun flightStartPosition(x: Float, y: Float, z: Float): Builder {
            builder.flightStartPosition(x, y, z)
            return this
        }

        actual fun flightStartOrientation(pitch: Float, yaw: Float): Builder {
            builder.flightStartOrientation(pitch, yaw)
            return this
        }

        actual fun flightMaxMoveSpeed(maxSpeed: Float): Builder {
            builder.flightMaxMoveSpeed(maxSpeed)
            return this
        }

        actual fun flightSpeedSteps(steps: Int): Builder {
            builder.flightSpeedSteps(steps)
            return this
        }

        actual fun flightPanSpeed(x: Float, y: Float): Builder {
            builder.flightPanSpeed(x, y)
            return this
        }

        actual fun flightMoveDamping(damping: Float): Builder {
            builder.flightMoveDamping(damping)
            return this
        }

        actual fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder {
            builder.groundPlane(a, b, c, d)
            return this
        }

        actual fun panning(enabled: Boolean): Builder {
            builder.panning(enabled)
            return this
        }

        actual fun build(mode: Mode): Manipulator {
            return Manipulator(builder.build(com.google.android.filament.utils.Manipulator.Mode.values()[mode.ordinal]))
        }
    }


    actual fun destroy() {
        // Android Manipulator doesn't have a destroy method in the public API (GC handles it)
    }

    actual fun getMode(): Mode = Mode.entries[androidHandle.mode.ordinal]

    actual fun setViewport(width: Int, height: Int) {
        androidHandle.setViewport(width, height)
    }

    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) {
        androidHandle.getLookAt(outEye, outTarget, outUp)
    }

    actual fun raycast(x: Int, y: Int, outResult: FloatArray) {
        val result = androidHandle.raycast(x, y)
        if (result != null) {
            result.copyInto(outResult)
        }
    }

    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        androidHandle.grabBegin(x, y, strafe)
    }

    actual fun grabUpdate(x: Int, y: Int) {
        androidHandle.grabUpdate(x, y)
    }

    actual fun grabEnd() {
        androidHandle.grabEnd()
    }

    actual fun keyDown(key: Key) {
        androidHandle.keyDown(com.google.android.filament.utils.Manipulator.Key.entries[key.ordinal])
    }

    actual fun keyUp(key: Key) {
        androidHandle.keyUp(com.google.android.filament.utils.Manipulator.Key.entries[key.ordinal])
    }

    actual fun scroll(x: Int, y: Int, delta: Float) {
        androidHandle.scroll(x, y, delta)
    }

    actual fun update(deltaTime: Float) {
        androidHandle.update(deltaTime)
    }

    actual fun getCurrentBookmark(): Bookmark = Bookmark(androidHandle.currentBookmark)

    actual fun getHomeBookmark(): Bookmark = Bookmark(androidHandle.homeBookmark)

    actual fun jumpToBookmark(bookmark: Bookmark) {
        androidHandle.jumpToBookmark(bookmark.androidValue as com.google.android.filament.utils.Bookmark)
    }

    actual class Bookmark internal constructor(internal val androidValue: Any)

}
