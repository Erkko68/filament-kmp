package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Texture
import org.jetbrains.skia.*

@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var textureHandle by remember { mutableStateOf(0L) }
    val prevSnapshot = remember { mutableStateOf<Image?>(null) }

    LaunchedEffect(textureSize) {
        if (textureSize.width <= 0 || textureSize.height <= 0) return@LaunchedEffect
        
        val engine = controller.engine ?: return@LaunchedEffect
        
        // Clean up previous texture/snapshot
        prevSnapshot.value?.close()
        prevSnapshot.value = null
        TextureUtils.releaseSharedTexture(textureHandle)
        
        // Create new shared texture
        val (devicePtr, physDevicePtr) = SkikoInterop.skikoDevicePtrs()
        textureHandle = TextureUtils.createSharedTexture(
            devicePtr, physDevicePtr, textureSize.width, textureSize.height
        )
        
        // Configure Filament for offscreen rendering to this texture
        val colorTexture = Texture.Builder()
            .width(textureSize.width)
            .height(textureSize.height)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
            .format(Texture.InternalFormat.RGBA8)
            .importTexture(textureHandle)
            .build(engine)
        
        val depthTexture = Texture.Builder()
            .width(textureSize.width)
            .height(textureSize.height)
            .usage(Texture.Usage.DEPTH_ATTACHMENT)
            .format(Texture.InternalFormat.DEPTH32F)
            .build(engine)
        
        val renderTarget = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture)
            .build(engine)
            
        val swapChain = engine.createSwapChain(textureSize.width, textureSize.height, 0)
        
        controller.setOffscreenSurface(textureSize.width, textureSize.height, renderTarget, swapChain)
    }

    var frameTime by remember { mutableStateOf(0L) }
    FilamentRenderLoop { frameTime = it }

    DisposableEffect(Unit) {
        onDispose {
            prevSnapshot.value?.close()
            prevSnapshot.value = null
            TextureUtils.releaseSharedTexture(textureHandle)
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                if (textureHandle == 0L) return@drawBehind

                controller.render(frameTime)

                drawIntoCanvas { canvas ->
                    val ctx = SkikoInterop.skikoContext() ?: return@drawIntoCanvas
                    val rt = SkikoInterop.makeBackendRT(textureSize.width, textureSize.height, textureHandle)
                        ?: return@drawIntoCanvas

                    val surface = Surface.makeFromBackendRenderTarget(
                        ctx, rt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.BGRA_8888,
                        ColorSpace.sRGB, SurfaceProps()
                    )
                    rt.close()
                    surface ?: return@drawIntoCanvas

                    val snapshot = surface.makeImageSnapshot()
                    surface.close()
                    snapshot ?: return@drawIntoCanvas

                    canvas.nativeCanvas.drawImage(snapshot, 0f, 0f)
                    
                    prevSnapshot.value?.close()
                    prevSnapshot.value = snapshot
                }
            }
    )
}
