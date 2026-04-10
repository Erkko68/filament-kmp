@file:OptIn(ExperimentalForeignApi::class)
package eric.bitria.samples

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import dev.filament.kmp.NativeSurface
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.useContents
import objcnames.protocols.MTLDeviceProtocol
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLPixelFormatBGRA8Unorm
import platform.QuartzCore.CAMetalLayer
import platform.UIKit.UIScreen
import platform.UIKit.UIView

@Composable
actual fun FilamentView(
    modifier: Modifier,
    renderer: FilamentViewRenderer
) {
    UIKitView(
        factory = {
            object : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
                private var surfaceAttached = false

                private val metalLayer = CAMetalLayer().apply {
                    opaque = true
                    pixelFormat = MTLPixelFormatBGRA8Unorm
                    device = MTLCreateSystemDefaultDevice() as MTLDeviceProtocol?
                }

                init {
                    layer.addSublayer(metalLayer)
                }

                override fun layoutSubviews() {
                    super.layoutSubviews()

                    metalLayer.frame = bounds

                    val scale = UIScreen.mainScreen.scale
                    metalLayer.contentsScale = scale

                    val width = (bounds.useContents { size.width } * scale).toInt()
                    val height = (bounds.useContents { size.height } * scale).toInt()

                    if (width > 0 && height > 0) {
                        if (!surfaceAttached) {
                            val surface = NativeSurface(interpretCPointer(metalLayer.objcPtr()))
                            renderer.onSurfaceAvailable(surface, width, height)
                            surfaceAttached = true
                        }

                        metalLayer.drawableSize = CGSizeMake(width.toDouble(), height.toDouble())
                        renderer.onSurfaceResized(width, height)
                    }
                }
            }
        },
        modifier = modifier,
        update = {},
        onRelease = {
            renderer.onSurfaceDetached()
        }
    )

    FilamentRenderLoop(renderer)
}