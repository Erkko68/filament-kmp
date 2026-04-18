package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.*
import java.lang.reflect.Method

fun createReflectiveMetalRenderTarget(width: Int, height: Int, textureHandle: Long): BackendRenderTarget? {
    return try {
        val rtClass = BackendRenderTarget::class.java
        val rtKtClass = Class.forName("${rtClass.`package`.name}.BackendRenderTargetKt", true, rtClass.classLoader)
        val makeMetalMethod = rtKtClass.methods.find { it.name == "access\$_nMakeMetal" }
            ?: throw IllegalStateException("Could not find access\$_nMakeMetal in BackendRenderTargetKt")

        val ptr = makeMetalMethod.invoke(null, width, height, textureHandle) as Long
        if (ptr == 0L) throw IllegalStateException("BackendRenderTarget_nMakeMetal returned null ptr")

        return rtClass.getDeclaredConstructor(Long::class.java).also { it.isAccessible = true }.newInstance(ptr) as BackendRenderTarget
    } catch (e: Exception) {
        println("Reflective Metal hack failed: ${e.message}")
        null
    }
}

fun findDirectContext(canvas: Canvas): DirectContext? {
    return try {
        val searchClasses = listOf(canvas.javaClass, canvas.javaClass.superclass)
        for (clazz in searchClasses.filterNotNull()) {
            for (method in clazz.declaredMethods) {
                if (method.parameterCount == 0 && 
                    (method.returnType.name.contains("DirectContext") || 
                     method.returnType.name.contains("RecordingContext"))) {
                    method.isAccessible = true
                    val result = method.invoke(canvas)
                    if (result is DirectContext) return result
                }
            }
        }
        null
    } catch (e: java.lang.Exception) {
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
    val frameCount by jvmRenderer.frameCount

    LaunchedEffect(textureSize.width, textureSize.height) {
        val w = textureSize.width
        val h = textureSize.height
        if (w > 0 && h > 0) {
            if (textureHandle != 0L) io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            textureHandle = jvmRenderer.createMetalTexture(w, h)
            jvmRenderer.initializeOffscreen(w, h, textureHandle)
        }
    }

    val backupContext = remember(jvmRenderer.engine) {
        val engine = jvmRenderer.engine
        if (engine != null) {
            DirectContext.makeMetal(jvmRenderer.getMetalDevice(), jvmRenderer.getMetalQueue())
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            if (textureHandle != 0L) io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            backupContext?.close()
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                @Suppress("UNUSED_VARIABLE")
                val trigger = frameCount 

                if (textureHandle != 0L) {
                    drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        val context = findDirectContext(nativeCanvas) ?: backupContext
                        
                        if (context != null) {
                            try {
                                // 1. Filament Render (Cyan)
                                jvmRenderer.render(System.nanoTime())
                                jvmRenderer.engine!!.flushAndWait()
                                
                                val rt = createReflectiveMetalRenderTarget(textureSize.width, textureSize.height, textureHandle)
                                if (rt != null) {
                                    val surface = Surface.makeFromBackendRenderTarget(
                                        context, rt, 
                                        SurfaceOrigin.TOP_LEFT, 
                                        SurfaceColorFormat.BGRA_8888, 
                                        ColorSpace.sRGB, 
                                        SurfaceProps()
                                    )
                                    
                                    if (surface != null) {
                                        // 2. DIAGNOSTIC MAGENTA PUNCH
                                        // We clear the shared texture with Magenta from Skia's side.
                                        // If we don't see Magenta on the screen, the texture sharing is broken.
                                        surface.canvas.clear(0xFFFF00FF.toInt()) 
                                        
                                        val snapshot = surface.makeImageSnapshot()
                                        if (snapshot != null) {
                                            nativeCanvas.drawImage(snapshot, 0f, 0f)
                                            // BLUE DOT = Pipeline Success
                                            nativeCanvas.drawCircle(30f, 30f, 10f, org.jetbrains.skia.Paint().apply { color = 0xFF0000FF.toInt() })
                                            snapshot.close()
                                        }
                                        surface.close()
                                    } else {
                                        // ORANGE DOT = Surface Fail
                                        nativeCanvas.drawCircle(30f, 30f, 10f, org.jetbrains.skia.Paint().apply { color = 0xFFFFA500.toInt() })
                                    }
                                }
                                
                                context.resetAll()
                                context.flush()
                                context.submit(true)
                                
                            } catch (t: Throwable) {
                                println("Interop error: ${t.message}")
                            }
                        } else {
                            // RED DOT = Context Fail
                            nativeCanvas.drawCircle(30f, 30f, 10f, org.jetbrains.skia.Paint().apply { color = 0xFFFF0000.toInt() })
                        }
                    }
                }
            }
    )

    FilamentRenderLoop(renderer)
}
