package io.github.erkko68.filament

import io.github.erkko68.filament.js.SurfaceOrientation as JSSurfaceOrientation
import io.github.erkko68.filament.js.`SurfaceOrientation_Builder` as JSSurfaceOrientationBuilder
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import org.khronos.webgl.Uint32Array
import org.khronos.webgl.Int16Array

actual class SurfaceOrientation(internal val jsSurfaceOrientation: JSSurfaceOrientation) {
    actual class Builder {
        private val jsBuilder = JSSurfaceOrientationBuilder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jsBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: Any, stride: Int): Builder {
            jsBuilder.normals(buffer.unsafeCast<Float32Array>(), stride)
            return this
        }

        actual fun tangents(buffer: Any, stride: Int): Builder {
            // JS Builder doesn't have tangents() directly
            return this
        }

        actual fun uvs(buffer: Any, stride: Int): Builder {
            jsBuilder.uvs(buffer.unsafeCast<Float32Array>(), stride)
            return this
        }

        actual fun positions(buffer: Any, stride: Int): Builder {
            jsBuilder.positions(buffer.unsafeCast<Float32Array>(), stride)
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            jsBuilder.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: Any): Builder {
            jsBuilder.triangles16(buffer.unsafeCast<Uint16Array>())
            return this
        }

        actual fun triangles32(buffer: Any): Builder {
            jsBuilder.triangles32(buffer.unsafeCast<Uint32Array>())
            return this
        }

        actual fun build(): SurfaceOrientation {
            return SurfaceOrientation(jsBuilder.build())
        }
    }

    actual fun getVertexCount(): Int {
        // Not in JS bindings, returning 0
        return 0
    }

    actual fun getQuatsAsFloat(buffer: Any, count: Int) {
        val quats = jsSurfaceOrientation.getQuatsFloat4(count)
        val dst = buffer.unsafeCast<Float32Array>()
        dst.set(quats)
    }

    actual fun getQuatsAsHalf(buffer: Any, count: Int) {
        val quats = jsSurfaceOrientation.getQuatsHalf4(count)
        val dst = buffer.unsafeCast<Uint16Array>()
        dst.set(quats)
    }

    actual fun getQuatsAsShort(buffer: Any, count: Int) {
        val quats = jsSurfaceOrientation.getQuats(count)
        val dst = buffer.unsafeCast<Int16Array>()
        dst.set(quats)
    }

    actual fun destroy() {
        jsSurfaceOrientation.delete()
    }
}