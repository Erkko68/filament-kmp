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
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo


@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var snapshotImage by remember { mutableStateOf<Image?>(null) }
    var readPixelsPending by remember { mutableStateOf(false) }

    // Buffer for offscreen readPixels
    val pixelBuffer = remember(textureSize) {
        if (textureSize.width > 0 && textureSize.height > 0) {
            ByteArray(textureSize.width * textureSize.height * 4)
        } else null
    }

    val pixelBufferDescriptor = remember(pixelBuffer) {
        if (pixelBuffer != null) {
            Texture.PixelBufferDescriptor(
                storage = pixelBuffer,
                sizeInBytes = pixelBuffer.size,
                format = Texture.Format.RGBA,
                type = Texture.Type.UBYTE,
                alignment = 1,
                left = 0,
                top = 0,
                stride = textureSize.width,
                callback = {
                    val imageInfo = ImageInfo(
                        textureSize.width, 
                        textureSize.height, 
                        ColorType.RGBA_8888, 
                        ColorAlphaType.PREMUL
                    )
                    // Create a Skia image from the copied bytes
                    val newImage = Image.makeRaster(imageInfo, pixelBuffer, textureSize.width * 4)
                    
                    val oldImage = snapshotImage
                    snapshotImage = newImage
                    oldImage?.close()
                    
                    readPixelsPending = false
                }
            )
        } else null
    }

    LaunchedEffect(textureSize) {
        if (textureSize.width <= 0 || textureSize.height <= 0) return@LaunchedEffect

        val engine = controller.engine ?: return@LaunchedEffect
        
        snapshotImage?.close()
        snapshotImage = null
        readPixelsPending = false
        
        // Configure Filament for offscreen rendering to a texture
        val colorTexture = Texture.Builder()
            .width(textureSize.width)
            .height(textureSize.height)
            .levels(1)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC or Texture.Usage.SAMPLEABLE)
            .format(Texture.InternalFormat.RGBA8)
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

    FilamentRenderLoop { frameTime ->
        controller.render(frameTime)
        
        // After rendering, if we aren't currently reading, schedule a pixel read
        if (!readPixelsPending && pixelBufferDescriptor != null && textureSize.width > 0 && textureSize.height > 0) {
            val target = controller.renderTarget
            // Ensure the GPU target size matches our CPU buffer size
            if (target != null && controller.targetWidth == textureSize.width && controller.targetHeight == textureSize.height) {
                readPixelsPending = true
                controller.renderer?.readPixels(
                    renderTarget = target,
                    xoffset = 0,
                    yoffset = 0,
                    width = textureSize.width,
                    height = textureSize.height,
                    buffer = pixelBufferDescriptor
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            snapshotImage?.close()
            snapshotImage = null
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                val image = snapshotImage
                if (image != null) {
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawImage(image, 0f, 0f)
                    }
                }
            }
    )
}
