package io.github.erkko68

actual class Aabb {
    internal val box = com.google.android.filament.Box()

    actual constructor()

    actual constructor(minX: Float, minY: Float, minZ: Float, maxX: Float, maxY: Float, maxZ: Float) {
        val cx = (minX + maxX) / 2f
        val cy = (minY + maxY) / 2f
        val cz = (minZ + maxZ) / 2f
        val hx = (maxX - minX) / 2f
        val hy = (maxY - minY) / 2f
        val hz = (maxZ - minZ) / 2f
        box.setCenter(cx, cy, cz)
        box.setHalfExtent(hx, hy, hz)
    }

    actual val min: FloatArray
        get() {
            val c = box.center
            val h = box.halfExtent
            return floatArrayOf(c[0] - h[0], c[1] - h[1], c[2] - h[2])
        }

    actual val max: FloatArray
        get() {
            val c = box.center
            val h = box.halfExtent
            return floatArrayOf(c[0] + h[0], c[1] + h[1], c[2] + h[2])
        }

    actual fun setMin(x: Float, y: Float, z: Float) {
        val currentMax = max
        val cx = (x + currentMax[0]) / 2f
        val cy = (y + currentMax[1]) / 2f
        val cz = (z + currentMax[2]) / 2f
        val hx = (currentMax[0] - x) / 2f
        val hy = (currentMax[1] - y) / 2f
        val hz = (currentMax[2] - z) / 2f
        box.setCenter(cx, cy, cz)
        box.setHalfExtent(hx, hy, hz)
    }

    actual fun setMax(x: Float, y: Float, z: Float) {
        val currentMin = min
        val cx = (currentMin[0] + x) / 2f
        val cy = (currentMin[1] + y) / 2f
        val cz = (currentMin[2] + z) / 2f
        val hx = (x - currentMin[0]) / 2f
        val hy = (y - currentMin[1]) / 2f
        val hz = (z - currentMin[2]) / 2f
        box.setCenter(cx, cy, cz)
        box.setHalfExtent(hx, hy, hz)
    }

    actual fun center(): FloatArray = box.center
    actual fun halfExtent(): FloatArray = box.halfExtent
}
