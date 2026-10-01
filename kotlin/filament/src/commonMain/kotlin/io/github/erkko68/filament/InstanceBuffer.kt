package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
import io.github.erkko68.filament.interop.*

/**
 * Per-instance local transforms for a renderable drawn with [RenderableManager.Builder.instances]. Each transform
 * is relative to the renderable's, so moving the renderable moves all its instances. Transforms are column-major
 * `mat4f`s, 16 floats each. Destroy it with [Engine.destroy], after the renderables that use it.
 */
class InstanceBuffer @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /** Builds an InstanceBuffer of [instanceCount] instances, 1 to [Engine.maxAutomaticInstances]. */
    class Builder(instanceCount: Int) {
        private val nativeBuilder = FilaInstanceBufferBuilder_create(instanceCount)
        private var localTransforms: FloatArray? = null

        /** Initial local transforms, one `mat4f` per instance (default: identity). */
        fun localTransforms(localTransforms: FloatArray?): Builder = apply { this.localTransforms = localTransforms }

        /** A name for debugging, shown in error messages. */
        fun name(name: String): Builder = apply { interopScope { FilaInstanceBufferBuilder_name(nativeBuilder, toInterop(name)) } }

        /** Creates the InstanceBuffer. */
        fun build(engine: Engine): InstanceBuffer {
            // C++ reads the transforms in build(), so they're only handed over here.
            val handle = interopScope {
                FilaInstanceBufferBuilder_localTransforms(nativeBuilder, localTransforms?.let { toInterop(it) } ?: NullPointer)
                FilaInstanceBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            }
            FilaInstanceBufferBuilder_destroy(nativeBuilder)
            return InstanceBuffer(handle)
        }
    }

    /** The instance count given to the [Builder]. */
    val instanceCount: Int get() = FilaInstanceBuffer_getInstanceCount(nativeHandle)

    /** Sets [count] local transforms (`mat4f`s in [localTransforms]), starting at instance [offset]. */
    fun setLocalTransforms(localTransforms: FloatArray, count: Int = localTransforms.size / 16, offset: Int = 0) {
        require(localTransforms.size >= count * 16) { "$count transforms need ${count * 16} floats, not ${localTransforms.size}" }
        localTransforms.usePinned { FilaInstanceBuffer_setLocalTransforms(nativeHandle, it, count, offset) }
    }

    /** The local transform of instance [index], a `mat4f`. */
    fun getLocalTransform(index: Int): FloatArray = floats(16) { FilaInstanceBuffer_getLocalTransform(nativeHandle, index, it) }
}
