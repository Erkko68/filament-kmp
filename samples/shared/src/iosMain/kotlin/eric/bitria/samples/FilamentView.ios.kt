@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package eric.bitria.samples

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import dev.filament.kmp.NativeSurface
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import platform.QuartzCore.CAMetalLayer
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import kotlinx.coroutines.isActive
import kotlinx.cinterop.useContents

@Composable
actual fun FilamentView(
    modifier: Modifier,
    renderer: FilamentRenderer
) {
    UIKitView(
        factory = {
            val view = UIView()
            val metalLayer = CAMetalLayer()
            view.layer.addSublayer(metalLayer)
            
            // On iOS, Filament expects the CAMetalLayer as the native window
            val surface = NativeSurface(interpretCPointer(metalLayer.objcPtr()))
            
            renderer.onSurfaceAvailable(surface, 0, 0)
            view
        },
        modifier = modifier,
        update = { view ->
            val metalLayer = view.layer.sublayers?.firstOrNull() as? CAMetalLayer
            metalLayer?.let {
                it.frame = view.bounds
                // Get physical pixels
                val scale = view.contentScaleFactor
                val width = (view.bounds.useContents { size.width } * scale).toInt()
                val height = (view.bounds.useContents { size.height } * scale).toInt()
                renderer.onSurfaceResized(width, height)
            }
        }
    )

    LaunchedEffect(renderer) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                renderer.render(frameTimeNanos)
            }
        }
    }
}
