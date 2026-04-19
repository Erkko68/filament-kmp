package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.jni.Manipulator as JniManipulator

actual class Manipulator(val jni: JniManipulator) {
    actual enum class Mode { 
        ORBIT, MAP, FLIGHT;
        internal fun toJni() = JniManipulator.Mode.values()[ordinal]
        companion object {
            internal fun fromJni(mode: JniManipulator.Mode) = values()[mode.ordinal]
        }
    }
    actual enum class Fov { 
        VERTICAL, HORIZONTAL;
        internal fun toJni() = JniManipulator.Fov.values()[ordinal]
    }
    actual enum class Key { 
        FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN;
        internal fun toJni() = JniManipulator.Key.values()[ordinal]
    }

    actual class Builder actual constructor() {
        private val jniBuilder = JniManipulator.Builder()

        actual fun viewport(width: Int, height: Int): Builder { jniBuilder.viewport(width, height); return this }
        actual fun targetPosition(x: Float, y: Float, z: Float): Builder { jniBuilder.targetPosition(x, y, z); return this }
        actual fun upVector(x: Float, y: Float, z: Float): Builder { jniBuilder.upVector(x, y, z); return this }
        actual fun zoomSpeed(speed: Float): Builder { jniBuilder.zoomSpeed(speed); return this }
        actual fun orbitHomePosition(x: Float, y: Float, z: Float): Builder { jniBuilder.orbitHomePosition(x, y, z); return this }
        actual fun orbitSpeed(x: Float, y: Float): Builder { jniBuilder.orbitSpeed(x, y); return this }
        actual fun fovDirection(fov: Fov): Builder { jniBuilder.fovDirection(fov.toJni()); return this }
        actual fun fovDegrees(degrees: Float): Builder { jniBuilder.fovDegrees(degrees); return this }
        actual fun farPlane(distance: Float): Builder { jniBuilder.farPlane(distance); return this }
        actual fun mapExtent(width: Float, height: Float): Builder { jniBuilder.mapExtent(width, height); return this }
        actual fun mapMinDistance(distance: Float): Builder { jniBuilder.mapMinDistance(distance); return this }
        actual fun flightStartPosition(x: Float, y: Float, z: Float): Builder { jniBuilder.flightStartPosition(x, y, z); return this }
        actual fun flightStartOrientation(pitch: Float, yaw: Float): Builder { jniBuilder.flightStartOrientation(pitch, yaw); return this }
        actual fun flightMaxMoveSpeed(maxSpeed: Float): Builder { jniBuilder.flightMaxMoveSpeed(maxSpeed); return this }
        actual fun flightSpeedSteps(steps: Int): Builder { jniBuilder.flightSpeedSteps(steps); return this }
        actual fun flightPanSpeed(x: Float, y: Float): Builder { jniBuilder.flightPanSpeed(x, y); return this }
        actual fun flightMoveDamping(damping: Float): Builder { jniBuilder.flightMoveDamping(damping); return this }
        actual fun groundPlane(a: Float, b: Float, c: Float, d: Float): Builder { jniBuilder.groundPlane(a, b, c, d); return this }
        actual fun panning(enabled: Boolean): Builder { jniBuilder.panning(enabled); return this }
        actual fun build(mode: Mode): Manipulator = Manipulator(jniBuilder.build(mode.toJni()))
    }

    actual fun destroy() : Unit { jni.destroy() }
    actual fun getMode(): Mode = Mode.fromJni(jni.getMode())
    actual fun setViewport(width: Int, height: Int) : Unit { jni.setViewport(width, height) }
    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) : Unit { jni.getLookAt(outEye, outTarget, outUp) }
    actual fun raycast(x: Int, y: Int, outResult: FloatArray) : Unit { jni.raycast(x, y, outResult) }
    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) : Unit { jni.grabBegin(x, y, strafe) }
    actual fun grabUpdate(x: Int, y: Int) : Unit { jni.grabUpdate(x, y) }
    actual fun grabEnd() : Unit { jni.grabEnd() }
    actual fun keyDown(key: Key) : Unit { jni.keyDown(key.toJni()) }
    actual fun keyUp(key: Key) : Unit { jni.keyUp(key.toJni()) }
    actual fun scroll(x: Int, y: Int, delta: Float) : Unit { jni.scroll(x, y, delta) }
    actual fun update(deltaTime: Float) : Unit { jni.update(deltaTime) }
    
    actual fun getCurrentBookmark(): Bookmark = Bookmark(jni.getCurrentBookmark())
    actual fun getHomeBookmark(): Bookmark = Bookmark(jni.getHomeBookmark())
    actual fun jumpToBookmark(bookmark: Bookmark) : Unit { jni.jumpToBookmark(bookmark.jni) }

    actual class Bookmark(val jni: JniManipulator.Bookmark) {
        fun destroy() : Unit { jni.destroy() }
    }
}
