package io.github.erkko68.filament.utils

// Manipulator is NOT available in JS runtime bindings. This is a stub implementation.
// Web applications should use JavaScript camera controls or a compatible third-party library.
// This implementation provides basic state tracking but no actual manipulation functionality.
actual class Manipulator(private var mode: Mode = Mode.ORBIT) {
    private var eye = floatArrayOf(0f, 0f, 1f)
    private var target = floatArrayOf(0f, 0f, 0f)
    private var up = floatArrayOf(0f, 1f, 0f)
    private var viewport = intArrayOf(800, 600)

    actual enum class Mode { ORBIT, MAP, FLIGHT }
    actual enum class Fov { VERTICAL, HORIZONTAL }
    actual enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    actual class Bookmark {
        var eye = floatArrayOf(0f, 0f, 1f)
        var target = floatArrayOf(0f, 0f, 0f)
        var up = floatArrayOf(0f, 1f, 0f)
    }

    actual class Builder {
        private var viewportWidth = 800
        private var viewportHeight = 600
        private var targetPos = floatArrayOf(0f, 0f, 0f)
        private var upVec = floatArrayOf(0f, 1f, 0f)
        private var zoomSpd = 0.1f
        private var orbitHomePos = floatArrayOf(0f, 0f, 1f)
        private var orbitSpd = floatArrayOf(0.1f, 0.1f)
        private var fovDir = Fov.VERTICAL
        private var fovDeg = 45f
        private var far = 1000f
        private var mapWdt = 100f
        private var mapHgt = 100f
        private var mapMinDist = 1f
        private var flightStartPos = floatArrayOf(0f, 0f, 0f)
        private var flightStartOrient = floatArrayOf(0f, 0f)
        private var flightMaxSpeed = 100f
        private var flightSpeedStps = 10
        private var flightPanSpd = floatArrayOf(1f, 1f)
        private var flightMoveDmp = 0.9f
        private var groundPlnEq = floatArrayOf(0f, 1f, 0f, 0f)
        private var panEnabled = true

        actual fun viewport(width: Int, height: Int): Builder {
            viewportWidth = width
            viewportHeight = height
            return this
        }

        actual fun targetPosition(x: Float, y: Float, z: Float): Builder {
            targetPos[0] = x
            targetPos[1] = y
            targetPos[2] = z
            return this
        }

        actual fun upVector(x: Float, y: Float, z: Float): Builder {
            upVec[0] = x
            upVec[1] = y
            upVec[2] = z
            return this
        }

        actual fun zoomSpeed(speed: Float): Builder {
            zoomSpd = speed
            return this
        }

        actual fun orbitHomePosition(x: Float, y: Float, z: Float): Builder {
            orbitHomePos[0] = x
            orbitHomePos[1] = y
            orbitHomePos[2] = z
            return this
        }

        actual fun orbitSpeed(x: Float, y: Float): Builder {
            orbitSpd[0] = x
            orbitSpd[1] = y
            return this
        }

        actual fun fovDirection(fov: Fov): Builder {
            fovDir = fov
            return this
        }

        actual fun fovDegrees(degrees: Float): Builder {
            fovDeg = degrees
            return this
        }

        actual fun farPlane(distance: Float): Builder {
            far = distance
            return this
        }

        actual fun mapExtent(width: Float, height: Float): Builder {
            mapWdt = width
            mapHgt = height
            return this
        }

        actual fun mapMinDistance(distance: Float): Builder {
            mapMinDist = distance
            return this
        }

        actual fun flightStartPosition(x: Float, y: Float, z: Float): Builder {
            flightStartPos[0] = x
            flightStartPos[1] = y
            flightStartPos[2] = z
            return this
        }

        actual fun flightStartOrientation(pitch: Float, yaw: Float): Builder {
            flightStartOrient[0] = pitch
            flightStartOrient[1] = yaw
            return this
        }

        actual fun flightMaxMoveSpeed(maxSpeed: Float): Builder {
            flightMaxSpeed = maxSpeed
            return this
        }

        actual fun flightSpeedSteps(steps: Int): Builder {
            flightSpeedStps = steps
            return this
        }

        actual fun flightPanSpeed(x: Float, y: Float): Builder {
            flightPanSpd[0] = x
            flightPanSpd[1] = y
            return this
        }

        actual fun flightMoveDamping(damping: Float): Builder {
            flightMoveDmp = damping
            return this
        }

        actual fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder {
            groundPlnEq[0] = a
            groundPlnEq[1] = b
            groundPlnEq[2] = c
            groundPlnEq[3] = d
            return this
        }

        actual fun panning(enabled: Boolean): Builder {
            panEnabled = enabled
            return this
        }

        actual fun build(mode: Mode): Manipulator {
            val manipulator = Manipulator(mode)
            manipulator.viewport = intArrayOf(viewportWidth, viewportHeight)
            manipulator.target = targetPos.copyOf()
            manipulator.up = upVec.copyOf()
            manipulator.eye = orbitHomePos.copyOf()
            return manipulator
        }
    }

    actual fun destroy() {
    }

    actual fun getMode(): Mode {
        return mode
    }

    actual fun setViewport(width: Int, height: Int) {
        viewport[0] = width
        viewport[1] = height
    }

    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) {
        eye.copyInto(outEye)
        target.copyInto(outTarget)
        up.copyInto(outUp)
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
        val bookmark = Bookmark()
        bookmark.eye = eye.copyOf()
        bookmark.target = target.copyOf()
        bookmark.up = up.copyOf()
        return bookmark
    }

    actual fun getHomeBookmark(): Bookmark {
        val bookmark = Bookmark()
        bookmark.eye = eye.copyOf()
        bookmark.target = target.copyOf()
        bookmark.up = up.copyOf()
        return bookmark
    }

    actual fun jumpToBookmark(bookmark: Bookmark) {
        bookmark.eye.copyInto(eye)
        bookmark.target.copyInto(target)
        bookmark.up.copyInto(up)
    }
}
