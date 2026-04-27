package io.github.erkko68.filament.utils

import kotlin.math.*

// ── Internal math ─────────────────────────────────────────────────────────────

internal data class V3(val x: Float, val y: Float, val z: Float) {
    operator fun plus(o: V3)  = V3(x + o.x, y + o.y, z + o.z)
    operator fun minus(o: V3) = V3(x - o.x, y - o.y, z - o.z)
    operator fun times(s: Float) = V3(x * s, y * s, z * s)
    companion object { val ZERO = V3(0f, 0f, 0f) }
}
internal data class V2(val x: Float, val y: Float) {
    operator fun minus(o: V2) = V2(x - o.x, y - o.y)
    companion object { val ZERO = V2(0f, 0f) }
}
internal data class V4(val x: Float, val y: Float, val z: Float, val w: Float)

private fun dot(a: V3, b: V3) = a.x*b.x + a.y*b.y + a.z*b.z
private fun cross(a: V3, b: V3) = V3(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x)
private fun length(v: V3) = sqrt(dot(v, v))
private fun normalize(v: V3): V3 { val l = length(v); return if (l > 0f) v * (1f / l) else v }
private fun dist(a: V3, b: V3) = length(a - b)
private fun clampf(v: Float, lo: Float, hi: Float) = v.coerceIn(lo, hi)
private fun saturatef(v: Float) = v.coerceIn(0f, 1f)

// eulerZYX(0, yaw, pitch) * (0,0,-1) — the camera forward direction
private fun eulerDir(pitch: Float, yaw: Float) = V3(
    -sin(yaw),
    sin(pitch) * cos(yaw),
    -cos(pitch) * cos(yaw)
)

// Rotate localDir by camera orientation (camera-to-world)
private fun camToWorld(eye: V3, target: V3, up: V3, local: V3): V3 {
    val fwd   = normalize(target - eye)
    val right = normalize(cross(fwd, up))
    val camUp = cross(right, fwd)
    return right * local.x + camUp * local.y + (fwd * -1f) * local.z
}

// ── Config ────────────────────────────────────────────────────────────────────

internal class Cfg {
    var vpW = 800; var vpH = 600
    var targetPosition  = V3.ZERO
    var upVector        = V3(0f, 1f, 0f)
    var zoomSpeed       = 0.01f
    var orbitHomePos    = V3(0f, 0f, 1f)
    var orbitSpeed      = V2(0.01f, 0.01f)
    var fovVertical     = true
    var fovDegrees      = 33f
    var farPlane        = 5000f
    var mapExtent       = V2(512f, 512f)
    var mapMinDist      = 0f
    var flightStartPos  = V3.ZERO
    var flightStartPitch= 0f
    var flightStartYaw  = 0f
    var flightMaxSpeed  = 10f
    var flightSpeedSteps= 80
    var flightPanSpeed  = V2(0.01f, 0.01f)
    var flightDamping   = 0f
    var groundPlane     = V4(0f, 1f, 0f, 0f)
    var panning         = true

    fun resolveDefaults() {
        if (zoomSpeed  == 0f) zoomSpeed  = 0.01f
        if (upVector   == V3.ZERO) upVector = V3(0f, 1f, 0f)
        if (fovDegrees == 0f) fovDegrees = 33f
        if (farPlane   == 0f) farPlane   = 5000f
        if (mapExtent  == V2.ZERO) mapExtent = V2(512f, 512f)
    }
}

// ── Bookmark data ─────────────────────────────────────────────────────────────

// Keep BmData internal so Manipulator.Bookmark (also internal ctor) can hold it
internal class BmData {
    var orbitPhi = 0f; var orbitTheta = 0f; var orbitDist = 0f; var orbitPivot = V3.ZERO
    var mapExtent = 0f; var mapCX = 0f; var mapCY = 0f
    var flightPos = V3.ZERO; var flightPitch = 0f; var flightYaw = 0f
    fun copy() = BmData().also {
        it.orbitPhi = orbitPhi; it.orbitTheta = orbitTheta; it.orbitDist = orbitDist
        it.orbitPivot = orbitPivot; it.mapExtent = mapExtent; it.mapCX = mapCX; it.mapCY = mapCY
        it.flightPos = flightPos; it.flightPitch = flightPitch; it.flightYaw = flightYaw
    }
}

// ── Base ──────────────────────────────────────────────────────────────────────

internal abstract class Base(protected val cfg: Cfg) {
    protected var eye    = V3.ZERO
    protected var target = V3.ZERO

    fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) {
        val gaze  = normalize(target - eye)
        val right = cross(gaze, cfg.upVector)
        val up    = cross(right, gaze)
        outEye[0] = eye.x; outEye[1] = eye.y; outEye[2] = eye.z
        outTarget[0] = target.x; outTarget[1] = target.y; outTarget[2] = target.z
        outUp[0] = up.x; outUp[1] = up.y; outUp[2] = up.z
    }

    fun setViewport(w: Int, h: Int) { cfg.vpW = w; cfg.vpH = h }

    protected fun getRay(x: Int, y: Int): Pair<V3, V3> {
        val gaze   = normalize(target - eye)
        val right  = normalize(cross(gaze, cfg.upVector))
        val upward = cross(right, gaze)
        val w = cfg.vpW.toFloat(); val h = cfg.vpH.toFloat()
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        val u = 2f * (0.5f + x) / w - 1f
        val v = 2f * (0.5f + y) / h - 1f
        val tan2 = tan(fov / 2f); val aspect = w / h
        val dir = if (cfg.fovVertical)
            gaze + right * (tan2 * u * aspect) + upward * (tan2 * v)
        else
            gaze + right * (tan2 * u) + upward * (tan2 * v / aspect)
        return Pair(eye, normalize(dir))
    }

    protected fun groundPlaneT(origin: V3, dir: V3): Float? {
        val n  = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val p0 = n * cfg.groundPlane.w
        val denom = -dot(n, dir)
        if (denom > 1e-6f) {
            val t = dot(p0 - origin, n) / -denom
            if (t >= 0f) return t
        }
        return null
    }

    fun raycast(x: Int, y: Int): V3? {
        val (o, d) = getRay(x, y)
        val t = groundPlaneT(o, d) ?: return null
        return o + d * t
    }

    fun raycastFarPlane(x: Int, y: Int): V3 {
        val gaze   = normalize(target - eye)
        val right  = cross(gaze, cfg.upVector)
        val upward = cross(right, gaze)
        val w = cfg.vpW.toFloat(); val h = cfg.vpH.toFloat()
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        val u = 2f * (0.5f + x) / w - 1f
        val v = 2f * (0.5f + y) / h - 1f
        val tan2 = tan(fov / 2f); val aspect = w / h
        val dir = if (cfg.fovVertical)
            gaze + right * (tan2 * u * aspect) + upward * (tan2 * v)
        else
            gaze + right * (tan2 * u) + upward * (tan2 * v / aspect)
        return eye + dir * cfg.farPlane
    }

    abstract fun grabBegin(x: Int, y: Int, strafe: Boolean)
    abstract fun grabUpdate(x: Int, y: Int)
    abstract fun grabEnd()
    open fun keyDown(k: Int) {}
    open fun keyUp(k: Int) {}
    abstract fun scroll(x: Int, y: Int, delta: Float)
    open fun update(dt: Float) {}
    abstract fun getCurrentBookmark(): BmData
    abstract fun getHomeBookmark(): BmData
    abstract fun jumpToBookmark(b: BmData)
}

// ── Orbit ─────────────────────────────────────────────────────────────────────

private class Orbit(cfg: Cfg) : Base(cfg) {
    private enum class Grab { NONE, ORBIT, PAN }
    private var grab = Grab.NONE
    private var flipped = false
    private var pivot = V3.ZERO
    private var gPivot = V3.ZERO; private var gScene = V3.ZERO; private var gFar = V3.ZERO
    private var gEye = V3.ZERO; private var gTarget = V3.ZERO
    private var gBm = BmData(); private var gX = 0; private var gY = 0

    init {
        if (cfg.orbitHomePos == V3.ZERO) cfg.orbitHomePos = V3(0f, 0f, 1f)
        if (cfg.orbitSpeed == V2.ZERO)   cfg.orbitSpeed   = V2(0.01f, 0.01f)
        if (cfg.groundPlane == V4(0f, 0f, 0f, 0f)) {
            val d = length(cfg.targetPosition)
            val n = normalize(cfg.orbitHomePos - cfg.targetPosition)
            cfg.groundPlane = V4(n.x, n.y, n.z, -d)
        }
        cfg.resolveDefaults()
        eye   = cfg.orbitHomePos
        pivot = cfg.targetPosition
        target = cfg.targetPosition
    }

    override fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        grab = if (strafe && cfg.panning) Grab.PAN else Grab.ORBIT
        gPivot = pivot; gEye = eye; gTarget = target
        gBm = getCurrentBookmark(); gX = x; gY = y
        gFar = raycastFarPlane(x, y); gScene = raycast(x, y) ?: gScene
    }

    override fun grabUpdate(x: Int, y: Int) {
        val dx = gX - x; val dy = gY - y
        if (grab == Grab.ORBIT) {
            val b = getCurrentBookmark()
            val maxPhi = (PI / 2.0 - 0.001).toFloat()
            b.orbitPhi   = clampf(gBm.orbitPhi   + dy * cfg.orbitSpeed.y, -maxPhi, maxPhi)
            b.orbitTheta = gBm.orbitTheta + dx * cfg.orbitSpeed.x
            jumpToBookmark(b)
        }
        if (grab == Grab.PAN) {
            val ulen = dist(gScene, gEye); val vlen = dist(gFar, gScene)
            val t = (gFar - raycastFarPlane(x, y)) * (ulen / vlen)
            pivot = gPivot + t; eye = gEye + t; target = gTarget + t
        }
    }

    override fun grabEnd() { grab = Grab.NONE }

    override fun scroll(x: Int, y: Int, delta: Float) {
        val gaze = normalize(target - eye)
        val move = gaze * (cfg.zoomSpeed * -delta)
        val v0 = pivot - eye
        eye = eye + move; target = target + move
        if (dot(v0, pivot - eye) < 0f) flipped = !flipped
    }

    override fun getCurrentBookmark(): BmData {
        val b = BmData()
        val pe = eye - pivot; val d = length(pe)
        b.orbitPhi   = asin((pe.y / d).coerceIn(-1f, 1f))
        b.orbitTheta = atan2(pe.x / d, pe.z / d)
        b.orbitDist  = if (flipped) -d else d
        b.orbitPivot = pivot
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        val he  = d * tan(fov / 2f)
        val nte = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val u   = cross(cfg.upVector, nte); val v = cross(nte, u)
        val ct  = pivot - cfg.targetPosition
        b.mapExtent = he * 2f; b.mapCX = dot(u, ct); b.mapCY = dot(v, ct)
        return b
    }

    override fun getHomeBookmark(): BmData {
        val b = BmData()
        b.orbitPivot = cfg.targetPosition
        b.orbitDist  = dist(cfg.targetPosition, cfg.orbitHomePos)
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        b.mapExtent  = b.orbitDist * tan(fov / 2f) * 2f
        return b
    }

    override fun jumpToBookmark(b: BmData) {
        pivot = b.orbitPivot
        val x = sin(b.orbitTheta) * cos(b.orbitPhi)
        val y = sin(b.orbitPhi)
        val z = cos(b.orbitTheta) * cos(b.orbitPhi)
        eye = pivot + V3(x, y, z) * abs(b.orbitDist)
        flipped = b.orbitDist < 0f
        target = eye + V3(x, y, z) * (if (flipped) 1f else -1f)
    }
}

// ── Map ───────────────────────────────────────────────────────────────────────

private class MapManip(cfg: Cfg) : Base(cfg) {
    private var grabbing = false
    private var gScene = V3.ZERO; private var gFar = V3.ZERO
    private var gEye = V3.ZERO; private var gTarget = V3.ZERO

    init {
        cfg.resolveDefaults()
        val horiz = !cfg.fovVertical
        val nte = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val he  = ((if (horiz) cfg.mapExtent.x else cfg.mapExtent.y)) / 2f
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        target = cfg.targetPosition
        eye    = target + nte * (he / tan(fov / 2f))
    }

    override fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        if (strafe) return
        gScene = raycast(x, y) ?: return
        gFar = raycastFarPlane(x, y); gEye = eye; gTarget = target; grabbing = true
    }

    override fun grabUpdate(x: Int, y: Int) {
        if (!grabbing) return
        val ulen = dist(gScene, gEye); val vlen = dist(gFar, gScene)
        val t = (gFar - raycastFarPlane(x, y)) * (ulen / vlen)
        eye = gEye + t; target = gTarget + t
    }

    override fun grabEnd() { grabbing = false }

    override fun scroll(x: Int, y: Int, delta: Float) {
        val gs = raycast(x, y) ?: return
        var u = gs - eye
        if (delta < 0f && length(u) < cfg.zoomSpeed) return
        u = u * (-delta * cfg.zoomSpeed)
        eye = eye + u; target = target + u
    }

    private fun localGroundT(origin: V3, dir: V3): Float? {
        val n = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val p0 = n * cfg.groundPlane.w; val denom = -dot(n, dir)
        if (denom > 1e-6f) { val t = dot(p0 - origin, n) / -denom; if (t >= 0f) return t }
        return null
    }

    override fun getCurrentBookmark(): BmData {
        val b = BmData()
        val dir = normalize(target - eye)
        val t = localGroundT(eye, dir) ?: 0f
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        val he = t * tan(fov / 2f)
        val tp = eye + dir * t
        val nte = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val u = cross(cfg.upVector, nte); val v = cross(nte, u)
        val ct = tp - cfg.targetPosition
        b.mapExtent = he * 2f; b.mapCX = dot(u, ct); b.mapCY = dot(v, ct)
        b.orbitPivot = cfg.targetPosition + u * b.mapCX + v * b.mapCY
        b.orbitDist  = he / tan(fov / 2f)
        return b
    }

    override fun getHomeBookmark(): BmData {
        val b = BmData()
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        b.mapExtent = if (!cfg.fovVertical) cfg.mapExtent.x else cfg.mapExtent.y
        b.orbitPivot = target
        b.orbitDist  = 0.5f * b.mapExtent / tan(fov / 2f)
        return b
    }

    override fun jumpToBookmark(b: BmData) {
        val nte = V3(cfg.groundPlane.x, cfg.groundPlane.y, cfg.groundPlane.z)
        val fov = cfg.fovDegrees * PI.toFloat() / 180f
        val dist2 = (b.mapExtent / 2f) / tan(fov / 2f)
        val u = normalize(cross(cfg.upVector, nte)) * b.mapCX
        val v = normalize(cross(nte, cross(cfg.upVector, nte))) * b.mapCY
        target = cfg.targetPosition + u + v
        eye    = target + nte * dist2
    }
}

// ── FreeFlight ────────────────────────────────────────────────────────────────

private class Flight(cfg: Cfg) : Base(cfg) {
    private var grabWin = V2.ZERO
    private var pitchYaw = V2(cfg.flightStartPitch, cfg.flightStartYaw)
    private var grabEuler = V2.ZERO
    private val keys = BooleanArray(6)
    private var grabbing = false
    private var scrollWheel = 0f; private var moveSpeed = 1f
    private var vel = V3.ZERO

    init {
        if (cfg.flightPanSpeed  == V2.ZERO) cfg.flightPanSpeed   = V2(0.01f, 0.01f)
        if (cfg.flightMaxSpeed  == 0f)      cfg.flightMaxSpeed   = 10f
        if (cfg.flightSpeedSteps == 0)      cfg.flightSpeedSteps = 80
        cfg.resolveDefaults()
        eye = cfg.flightStartPos
        updateTarget(pitchYaw.x, pitchYaw.y)
    }

    private fun updateTarget(pitch: Float, yaw: Float) { target = eye + eulerDir(pitch, yaw) }

    override fun grabBegin(x: Int, y: Int, strafe: Boolean) {
        grabWin = V2(x.toFloat(), y.toFloat()); grabbing = true; grabEuler = pitchYaw
    }

    override fun grabUpdate(x: Int, y: Int) {
        if (!grabbing) return
        val del = grabWin - V2(x.toFloat(), y.toFloat())
        val minP = (-PI / 2.0 + 0.001).toFloat(); val maxP = (PI / 2.0 - 0.001).toFloat()
        pitchYaw = V2(
            clampf(grabEuler.x + del.y * -cfg.flightPanSpeed.y, minP, maxP),
            (grabEuler.y + del.x * cfg.flightPanSpeed.x).rem((2 * PI).toFloat())
        )
        updateTarget(pitchYaw.x, pitchYaw.y)
    }

    override fun grabEnd() { grabbing = false }
    override fun keyDown(k: Int) { if (k in 0..5) keys[k] = true }
    override fun keyUp(k: Int)   { if (k in 0..5) keys[k] = false }

    override fun scroll(x: Int, y: Int, delta: Float) {
        val half = cfg.flightSpeedSteps / 2f
        scrollWheel = clampf(scrollWheel + delta, -half, half)
        moveSpeed = cfg.flightMaxSpeed.pow((scrollWheel + half) / half - 1f)
    }

    override fun update(dt: Float) {
        var local = V3.ZERO
        if (keys[0]) local = local + V3(0f,  0f, -1f)
        if (keys[1]) local = local + V3(-1f, 0f,  0f)
        if (keys[2]) local = local + V3(0f,  0f,  1f)
        if (keys[3]) local = local + V3(1f,  0f,  0f)
        var world = camToWorld(eye, target, cfg.upVector, local)
        if (keys[4]) world = world + V3(0f,  1f, 0f)
        if (keys[5]) world = world + V3(0f, -1f, 0f)

        val damp = cfg.flightDamping
        if (damp == 0f) {
            vel = world * moveSpeed
            val d = vel * dt; eye = eye + d; target = target + d
        } else {
            val step = dt / 16f
            repeat(16) {
                vel = vel * saturatef(1f - damp * step)
                val acc = world * (damp * maxOf(moveSpeed - length(vel), 0f))
                vel = vel + acc * step
                val d = vel * step; eye = eye + d; target = target + d
            }
        }
    }

    override fun getCurrentBookmark() = BmData().also {
        it.flightPos = eye; it.flightPitch = pitchYaw.x; it.flightYaw = pitchYaw.y
    }
    override fun getHomeBookmark() = BmData().also {
        it.flightPos = cfg.flightStartPos
        it.flightPitch = cfg.flightStartPitch; it.flightYaw = cfg.flightStartYaw
    }
    override fun jumpToBookmark(b: BmData) {
        eye = b.flightPos; pitchYaw = V2(b.flightPitch, b.flightYaw)
        updateTarget(b.flightPitch, b.flightYaw)
    }
}

// ── Public actual class ───────────────────────────────────────────────────────

actual class Manipulator internal constructor(private val impl: Base) {

    actual enum class Mode { ORBIT, MAP, FLIGHT }
    actual enum class Fov  { VERTICAL, HORIZONTAL }
    actual enum class Key  { FORWARD, LEFT, BACKWARD, RIGHT, UP, DOWN }

    actual class Bookmark internal constructor(internal val data: BmData)

    actual class Builder actual constructor() {
        private val cfg = Cfg()

        actual fun viewport(width: Int, height: Int)                    = apply { cfg.vpW = width; cfg.vpH = height }
        actual fun targetPosition(x: Float, y: Float, z: Float)        = apply { cfg.targetPosition  = V3(x, y, z) }
        actual fun upVector(x: Float, y: Float, z: Float)              = apply { cfg.upVector         = V3(x, y, z) }
        actual fun zoomSpeed(speed: Float)                              = apply { cfg.zoomSpeed        = speed }
        actual fun orbitHomePosition(x: Float, y: Float, z: Float)     = apply { cfg.orbitHomePos     = V3(x, y, z) }
        actual fun orbitSpeed(x: Float, y: Float)                      = apply { cfg.orbitSpeed        = V2(x, y) }
        actual fun fovDirection(fov: Fov)                              = apply { cfg.fovVertical       = fov == Fov.VERTICAL }
        actual fun fovDegrees(degrees: Float)                          = apply { cfg.fovDegrees        = degrees }
        actual fun farPlane(distance: Float)                           = apply { cfg.farPlane          = distance }
        actual fun mapExtent(width: Float, height: Float)              = apply { cfg.mapExtent         = V2(width, height) }
        actual fun mapMinDistance(distance: Float)                     = apply { cfg.mapMinDist        = distance }
        actual fun flightStartPosition(x: Float, y: Float, z: Float)  = apply { cfg.flightStartPos    = V3(x, y, z) }
        actual fun flightStartOrientation(pitch: Float, yaw: Float)   = apply { cfg.flightStartPitch  = pitch; cfg.flightStartYaw = yaw }
        actual fun flightMaxMoveSpeed(maxSpeed: Float)                 = apply { cfg.flightMaxSpeed    = maxSpeed }
        actual fun flightSpeedSteps(steps: Int)                        = apply { cfg.flightSpeedSteps  = steps }
        actual fun flightPanSpeed(x: Float, y: Float)                  = apply { cfg.flightPanSpeed    = V2(x, y) }
        actual fun flightMoveDamping(damping: Float)                   = apply { cfg.flightDamping     = damping }
        actual fun groundPlane(a: Float, b: Float, c: Float, d: Float) = apply { cfg.groundPlane       = V4(a, b, c, d) }
        actual fun panning(enabled: Boolean)                           = apply { cfg.panning           = enabled }

        actual fun build(mode: Mode): Manipulator = Manipulator(when (mode) {
            Mode.ORBIT  -> Orbit(cfg)
            Mode.MAP    -> MapManip(cfg)
            Mode.FLIGHT -> Flight(cfg)
        })
    }

    actual fun destroy() {}
    actual fun getMode(): Mode = when (impl) {
        is Orbit   -> Mode.ORBIT
        is MapManip -> Mode.MAP
        else        -> Mode.FLIGHT
    }
    actual fun setViewport(width: Int, height: Int)                             = impl.setViewport(width, height)
    actual fun getLookAt(outEye: FloatArray, outTarget: FloatArray, outUp: FloatArray) = impl.getLookAt(outEye, outTarget, outUp)
    actual fun raycast(x: Int, y: Int, outResult: FloatArray) {
        val r = impl.raycast(x, y) ?: return
        outResult[0] = r.x; outResult[1] = r.y; outResult[2] = r.z
    }
    actual fun grabBegin(x: Int, y: Int, strafe: Boolean) = impl.grabBegin(x, y, strafe)
    actual fun grabUpdate(x: Int, y: Int)                 = impl.grabUpdate(x, y)
    actual fun grabEnd()                                  = impl.grabEnd()
    actual fun keyDown(key: Key)                          = impl.keyDown(key.ordinal)
    actual fun keyUp(key: Key)                            = impl.keyUp(key.ordinal)
    actual fun scroll(x: Int, y: Int, delta: Float)      = impl.scroll(x, y, delta)
    actual fun update(deltaTime: Float)                   = impl.update(deltaTime)
    actual fun getCurrentBookmark()                       = Bookmark(impl.getCurrentBookmark())
    actual fun getHomeBookmark()                          = Bookmark(impl.getHomeBookmark())
    actual fun jumpToBookmark(bookmark: Bookmark)         = impl.jumpToBookmark(bookmark.data)
}
