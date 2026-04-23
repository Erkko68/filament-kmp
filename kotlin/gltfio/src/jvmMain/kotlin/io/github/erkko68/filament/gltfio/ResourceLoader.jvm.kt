package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.gltfio.jni.ResourceLoader as JniResourceLoader
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class ResourceLoader actual constructor(engine: Engine, normalizeSkinningWeights: Boolean) {
    private val jni = JniResourceLoader(engine.nativeEngine, normalizeSkinningWeights)
    
    actual fun destroy() : Unit { jni.destroy() }
    
    actual fun addResourceData(url: String, data: ByteArray) : Unit {
        val byteBuffer = ByteBuffer.allocateDirect(data.size).order(ByteOrder.nativeOrder())
        byteBuffer.put(data)
        byteBuffer.rewind()
        jni.addResourceData(url, byteBuffer)
    }
    
    actual fun hasResourceData(url: String): Boolean = jni.hasResourceData(url)
    actual fun loadResources(asset: FilamentAsset): Boolean {
        jni.loadResources(asset.jni)
        return true
    }
    
    actual fun asyncBeginLoad(asset: FilamentAsset): Boolean = jni.asyncBeginLoad(asset.jni)
    actual fun asyncGetLoadProgress(): Float = jni.asyncGetLoadProgress()
    actual fun asyncUpdateLoad() : Unit { jni.asyncUpdateLoad() }
    actual fun asyncCancelLoad() : Unit { jni.asyncCancelLoad() }
    
    actual fun evictResourceData() : Unit { jni.evictResourceData() }
}
