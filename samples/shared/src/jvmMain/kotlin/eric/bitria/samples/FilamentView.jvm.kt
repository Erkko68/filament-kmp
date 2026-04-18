package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.*
import java.lang.reflect.Method

fun createReflectiveMetalRenderTarget(width: Int, height: Int, textureHandle: Long): BackendRenderTarget? {
    return try {
        // Target BackendRenderTargetKt where _nMakeMetal resides
        val rtKtClass = Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
        
        // Find the native method: _nMakeMetal(int, int, long)
        val makeMetalMethod: Method = rtKtClass.getDeclaredMethod(
            "_nMakeMetal", 
            Int::class.java, // width
            Int::class.java, // height
            Long::class.java // texturePtr
        )
        makeMetalMethod.isAccessible = true
        
        // Invoke the native method to get the Skia C++ pointer
        val skiaPtr = makeMetalMethod.invoke(null, width, height, textureHandle) as Long
        
        // Use the internal constructor of BackendRenderTarget(long nativePtr)
        val constructor = BackendRenderTarget::class.java.getDeclaredConstructor(Long::class.java)
        constructor.isAccessible = true
        constructor.newInstance(skiaPtr) as BackendRenderTarget
        
    } catch (e: Exception) {
        println("Reflective Metal hack failed: ${e.message}")
        e.printStackTrace()
        null
    }
}

@Composable
actual fun FilamentView(
    modifier: Modifier,
    renderer: FilamentViewRenderer
) {
    val jvmRenderer = renderer as FilamentRenderer
    var textureHandle by remember { mutableStateOf(0L) }
    var textureSize by remember { mutableStateOf(IntSize.Zero) }

    // Read frameCount to trigger redraw every frame
    val frameCount by jvmRenderer.frameCount

    LaunchedEffect(textureSize) {
        if (textureSize.width > 0 && textureSize.height > 0) {
            // Clean up old texture
            if (textureHandle != 0L) {
                io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            }
            
            // 1. Create a native Metal texture
            textureHandle = jvmRenderer.createMetalTexture(textureSize.width, textureSize.height)
            
            // 2. Give this handle to Filament for offscreen rendering
            jvmRenderer.initializeOffscreen(textureSize.width, textureSize.height, textureHandle)
        }
    }

    // Reuse Skia context to avoid per-frame allocation overhead
    val directContext = remember(jvmRenderer, jvmRenderer.engine) {
        val engine = jvmRenderer.engine
        if (engine != null) {
            val device = jvmRenderer.getMetalDevice()
            val queue = jvmRenderer.getMetalQueue()
            DirectContext.makeMetal(device, queue)
        } else null
    }

    // Reuse RenderTarget based on the current texture handle
    val renderTarget = remember(textureHandle) {
        if (textureHandle != 0L) {
            createReflectiveMetalRenderTarget(
                width = textureSize.width,
                height = textureSize.height,
                textureHandle = textureHandle
            )
        } else null
    }

    // Reuse Surface based on context/target
    val skiaSurface = remember(directContext, renderTarget) {
        if (directContext != null && renderTarget != null) {
            Surface.makeFromBackendRenderTarget(
                directContext,
                renderTarget,
                SurfaceOrigin.TOP_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.sRGB
            )
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            if (textureHandle != 0L) {
                io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            }
            directContext?.close()
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                // Ensure redraw on every frame
                @Suppress("UNUSED_VARIABLE")
                val _trigger = frameCount
                
                if (skiaSurface != null) {
                    val skiaImage = skiaSurface.makeImageSnapshot()
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawImage(skiaImage, 0f, 0f)
                    }
                }
            }
    )

    // Use the common render loop
    FilamentRenderLoop(renderer)
}
