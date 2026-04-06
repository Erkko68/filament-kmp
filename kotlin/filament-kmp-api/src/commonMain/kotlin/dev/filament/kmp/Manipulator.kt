package dev.filament.kmp

/**
 * Helper that enables camera interaction similar to sketchfab or Google Maps.
 */
expect class Manipulator {
    enum class Mode { ORBIT, MAP, FREE_FLIGHT }
    enum class Fov { VERTICAL, HORIZONTAL }
    enum class Key { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    class Builder() {
        fun viewport(width: Int, height: Int): Builder
        fun targetPosition(x: Float, y: Float, z: Float): Builder
        fun upVector(x: Float, y: Float, z: Float): Builder
        fun zoomSpeed(speed: Float): Builder
        fun orbitHomePosition(x: Float, y: Float, z: Float): Builder
        fun orbitSpeed(x: Float, y: Float): Builder
        fun fovDirection(fov: Fov): Builder
        fun fovDegrees(degrees: Float): Builder
        fun farPlane(distance: Float): Builder
        fun mapExtent(width: Float, height: Float): Builder
        fun mapMinDistance(distance: Float): Builder
        fun flightStartPosition(x: Float, y: Float, z: Float): Builder
        fun flightStartOrientation(pitch: Float, yaw: Float): Builder
        fun flightMaxMoveSpeed(maxSpeed: Float): Builder
        fun flightSpeedSteps(steps: Int): Builder
        fun flightPanSpeed(x: Float, y: Float): Builder
        fun flightMoveDamping(damping: Float): Builder
        fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder
        fun panning(enabled: Boolean): Builder
        fun build(mode: Mode): Manipulator
    }

    class Bookmark

    fun getMode(): Mode
    fun setViewport(width: Int, height: Int)
    fun getLookAt(eyePosition: FloatArray, targetPosition: FloatArray, upward: FloatArray)
    fun getLookAt(eyePosition: DoubleArray, targetPosition: DoubleArray, upward: DoubleArray)
    fun raycast(x: Int, y: Int): FloatArray?
    fun grabBegin(x: Int, y: Int, strafe: Boolean)
    fun grabUpdate(x: Int, y: Int)
    fun grabEnd()
    fun keyDown(key: Key)
    fun keyUp(key: Key)
    fun scroll(x: Int, y: Int, scrollDelta: Float)
    fun update(deltaTime: Float)
    fun getCurrentBookmark(): Bookmark
    fun getHomeBookmark(): Bookmark
    fun jumpToBookmark(bookmark: Bookmark)
}
