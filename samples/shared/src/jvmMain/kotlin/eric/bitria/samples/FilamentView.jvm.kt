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
        val rtKtClass = try {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
        } catch (e: ClassNotFoundException) {
            Class.forName("org.jetbrains.skia.BackendRenderTarget")
        }
        
        // Find the native method: _nMakeMetal(int, int, long)
        val makeMetalMethod: Method = rtKtClass.getDeclaredMethods().find { it.name == "_nMakeMetal" } 
            ?: throw NoSuchMethodException("_nMakeMetal not found in ${rtKtClass.name}")
            
        makeMetalMethod.isAccessible = true
        
        // Invoke the native method to get the Skia C++ pointer
        val skiaPtr = makeMetalMethod.invoke(null, width, height, textureHandle) as Long
        if (skiaPtr == 0L) throw IllegalStateException("_nMakeMetal returned 0")

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

    LaunchedEffect(textureSize.width, textureSize.height) {
        val w = textureSize.width
        val h = textureSize.height
        if (w > 0 && h > 0) {
            println("FilamentView: Recreating texture for size $w x $h")
            // Clean up old texture
            if (textureHandle != 0L) {
                io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            }
            
            // 1. Create a native Metal texture
            textureHandle = jvmRenderer.createMetalTexture(w, h)
            
            // 2. Give this handle to Filament for offscreen rendering
            jvmRenderer.initializeOffscreen(w, h, textureHandle)
        }

    }

    // Reuse Skia context to avoid per-frame allocation overhead
    val directContext = remember(jvmRenderer, jvmRenderer.engine) {
        val engine = jvmRenderer.engine
        if (engine != null) {
            val device = jvmRenderer.getMetalDevice()
            val queue = jvmRenderer.getMetalQueue()
            println("Creating DirectContext with device=$device queue=$queue")
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
            println("Creating Skia Surface (BGRA) for texture $textureHandle")
            Surface.makeFromBackendRenderTarget(
                directContext,
                renderTarget,
                SurfaceOrigin.TOP_LEFT,
                SurfaceColorFormat.BGRA_8888,
                null // ColorSpace
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
                    directContext?.flushAndSubmit(skiaSurface)
                    val skiaImage = skiaSurface.makeImageSnapshot()

                    
                    drawIntoCanvas { canvas ->
                        // Background fallback (Dark Gray)
                        canvas.nativeCanvas.drawRect(org.jetbrains.skia.Rect.makeXYWH(0f, 0f, 4000f, 4000f), org.jetbrains.skia.Paint().apply { color = 0xFF333333.toInt() })


                        if (skiaImage != null) {
                            canvas.nativeCanvas.drawImage(skiaImage, 0f, 0f)
                            
                            // Debug overlay: Small green circle
                            val paint = org.jetbrains.skia.Paint().apply { color = 0xFF00FF00.toInt() }
                            canvas.nativeCanvas.drawCircle(30f, 30f, 20f, paint)
                        }
                    }
                    
                    // Flush our surface
                    skiaSurface.flushAndSubmit(true)
                } else {
                    drawIntoCanvas { canvas ->
                        // Draw red background if surface is missing
                        canvas.nativeCanvas.drawRect(org.jetbrains.skia.Rect.makeXYWH(0f, 0f, 4000f, 4000f), org.jetbrains.skia.Paint().apply { color = 0xFFFF0000.toInt() })
                        if (frameCount % 60L == 0L) {
                            println("FilamentView: Surface is NULL. Target=$renderTarget Context=$directContext")
                        }
                    }
                }
            }

    )

    // Use the common render loop
    FilamentRenderLoop(renderer)
}

