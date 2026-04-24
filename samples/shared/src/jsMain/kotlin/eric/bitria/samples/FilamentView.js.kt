package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.NativeSurface
import kotlinx.browser.document
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    var textureSize by remember { mutableStateOf(IntSize.Zero) }

    // A 2D scratch canvas used to copy pixels out of Filament's WebGL canvas.
    val readbackCanvas = remember {
        (document.createElement("canvas") as HTMLCanvasElement).also {
            it.style.display = "none"
        }
    }

    LaunchedEffect(textureSize) {
        val w = textureSize.width
        val h = textureSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        val engine = controller.engine ?: return@LaunchedEffect
        val filamentCanvas = engine.jsCanvas ?: return@LaunchedEffect

        filamentCanvas.width = w
        filamentCanvas.height = h
        readbackCanvas.width = w
        readbackCanvas.height = h

        controller.setSurface(NativeSurface(filamentCanvas), w, h)
    }

    var frameTime by remember { mutableStateOf(0L) }
    FilamentRenderLoop { frameTime = it }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                val engine = controller.engine ?: return@drawBehind
                val filamentCanvas = engine.jsCanvas ?: return@drawBehind
                val w = textureSize.width
                val h = textureSize.height
                if (w <= 0 || h <= 0) return@drawBehind

                controller.render(frameTime)

                // Blit Filament's WebGL canvas into a 2D canvas so we can pull pixels.
                val ctx2d = readbackCanvas.getContext("2d") as? CanvasRenderingContext2D
                    ?: return@drawBehind
                ctx2d.clearRect(0.0, 0.0, w.toDouble(), h.toDouble())
                ctx2d.asDynamic().drawImage(filamentCanvas, 0, 0)
                val imageData = ctx2d.getImageData(0.0, 0.0, w.toDouble(), h.toDouble())
                val u8 = imageData.data.asDynamic() // Uint8ClampedArray
                val len: Int = u8.length as Int
                val bytes = ByteArray(len)
                for (i in 0 until len) {
                    bytes[i] = (u8[i] as Int).toByte()
                }

                val info = ImageInfo(
                    ColorInfo(ColorType.RGBA_8888, ColorAlphaType.UNPREMUL, ColorSpace.sRGB),
                    w, h
                )
                val bitmap = Bitmap()
                bitmap.allocPixels(info)
                bitmap.installPixels(bytes)
                val image = Image.makeFromBitmap(bitmap)

                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawImage(image, 0f, 0f)
                }

                image.close()
                bitmap.close()
            }
    )
}
