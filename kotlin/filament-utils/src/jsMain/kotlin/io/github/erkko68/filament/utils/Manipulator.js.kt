package io.github.erkko68.filament.utils

actual class Manipulator {
    actual fun destroy() {
    }

    actual fun getMode(): Mode {
        TODO("Not yet implemented")
    }

    actual fun setViewport(width: Int, height: Int) {
    }

    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) {
    }

    actual fun raycast(x: Int, y: Int, outResult: FloatArray) {
    }

    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) {
    }

    actual fun grabUpdate(x: Int, y: Int) {
    }

    actual fun grabEnd() {
    }

    actual fun keyDown(key: Key) {
    }

    actual fun keyUp(key: Key) {
    }

    actual fun scroll(x: Int, y: Int, delta: Float) {
    }

    actual fun update(deltaTime: Float) {
    }

    actual fun getCurrentBookmark(): Bookmark {
        TODO("Not yet implemented")
    }

    actual fun getHomeBookmark(): Bookmark {
        TODO("Not yet implemented")
    }

    actual fun jumpToBookmark(bookmark: Bookmark) {
    }

    actual enum class Mode { ORBIT, MAP, FLIGHT }
    actual enum class Fov { VERTICAL, HORIZONTAL }
    actual enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }
    actual class Builder {
        actual fun viewport(
            width: Int,
            height: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun targetPosition(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun upVector(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun zoomSpeed(speed: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun orbitHomePosition(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun orbitSpeed(x: Float, y: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun fovDirection(fov: Fov): Builder {
            TODO("Not yet implemented")
        }

        actual fun fovDegrees(degrees: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun farPlane(distance: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun mapExtent(
            width: Float,
            height: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun mapMinDistance(distance: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightStartPosition(
            x: Float,
            y: Float,
            z: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightStartOrientation(
            pitch: Float,
            yaw: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightMaxMoveSpeed(maxSpeed: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightSpeedSteps(steps: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightPanSpeed(
            x: Float,
            y: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun flightMoveDamping(damping: Float): Builder {
            TODO("Not yet implemented")
        }

        actual fun groundPlane(
            a: Float,
            b: Float,
            c: Float,
            d: Float
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun panning(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(mode: Mode): Manipulator {
            TODO("Not yet implemented")
        }
    }

    actual class Bookmark
}