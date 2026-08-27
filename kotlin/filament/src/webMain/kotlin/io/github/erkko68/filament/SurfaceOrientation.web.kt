package io.github.erkko68.filament

import io.github.erkko68.filament.web.SurfaceOrientation as JSSurfaceOrientation
import io.github.erkko68.filament.web.`SurfaceOrientation_Builder` as JSSurfaceOrientationBuilder
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.get
import org.khronos.webgl.set

private fun FloatArray.toFloat32Array(): Float32Array {
    val fa = Float32Array(size)
    forEachIndexed { i, v -> fa[i] = v }
    return fa
}

private fun ShortArray.toUint16Array(): Uint16Array {
    val ua = Uint16Array(size)
    forEachIndexed { i, v -> ua[i] = v }
    return ua
}

private fun IntArray.toUint32Array(): Uint32Array {
    val ua = Uint32Array(size)
    forEachIndexed { i, v -> ua[i] = v }
    return ua
}

actual class SurfaceOrientation @InternalFilamentApi constructor(internal val jsSurfaceOrientation: JSSurfaceOrientation, actual val vertexCount: Int = 0) {
    actual class Builder {
        private val jsBuilder = JSSurfaceOrientationBuilder()
        private var vertexCount: Int = 0

        actual fun vertexCount(vertexCount: Int): Builder {
            this.vertexCount = vertexCount
            jsBuilder.vertexCount(vertexCount.toDouble())
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            jsBuilder.normals(buffer.toFloat32Array(), stride.toDouble())
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            jsBuilder.tangents(buffer.toFloat32Array(), stride.toDouble())
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            jsBuilder.uvs(buffer.toFloat32Array(), stride.toDouble())
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            jsBuilder.positions(buffer.toFloat32Array(), stride.toDouble())
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            jsBuilder.triangleCount(triangleCount.toDouble())
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            jsBuilder.triangles16(buffer.toUint16Array())
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            jsBuilder.triangles32(buffer.toUint32Array())
            return this
        }

        actual fun build(): SurfaceOrientation {
            return SurfaceOrientation(jsBuilder.build(), vertexCount)
        }
    }


    actual fun getQuatsAsFloat(buffer: FloatArray, count: Int) {
        val quats = jsSurfaceOrientation.getQuatsFloat4(count.toDouble())
        val n = minOf(count * 4, buffer.size)
        for (i in 0 until n) buffer[i] = quats[i]
    }

    actual fun getQuatsAsHalf(buffer: ShortArray, count: Int) {
        val quats = jsSurfaceOrientation.getQuatsHalf4(count.toDouble())
        val n = minOf(count * 4, buffer.size)
        for (i in 0 until n) buffer[i] = quats[i].toShort()
    }

    actual fun getQuatsAsShort(buffer: ShortArray, count: Int) {
        val quats = jsSurfaceOrientation.getQuats(count.toDouble())
        val n = minOf(count * 4, buffer.size)
        for (i in 0 until n) buffer[i] = quats[i]
    }

    actual fun destroy() {
        jsSurfaceOrientation.delete()
    }
}
