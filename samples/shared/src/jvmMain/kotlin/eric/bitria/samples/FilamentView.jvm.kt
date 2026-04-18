package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.jni.Texture as FilamentTexture
import kotlinx.coroutines.isActive
import org.jetbrains.skia.*

// BackendRenderTargetKt._nMakeMetal is package-private in Skiko — access via reflection.
private fun makeMetalBackendRT(width: Int, height: Int, ptr: Long): BackendRenderTarget? = runCatching {
    val handle = Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
        .methods.first { it.name == "access\$_nMakeMetal" }
        .invoke(null, width, height, ptr) as Long
    BackendRenderTarget::class.java.getDeclaredConstructor(Long::class.java)
        .apply { isAccessible = true }.newInstance(handle) as BackendRenderTarget
}.getOrNull()

// Returns the MTLDevice* that Skiko selected, for creating the shared texture on the same GPU.
private fun skikoMetalDevicePtr(): Long = runCatching {
    val gpuPriority = Class.forName("org.jetbrains.skiko.GpuPriority")
    val auto = gpuPriority.getMethod("valueOf", String::class.java).invoke(null, "Auto")
    val adapter = Class.forName("org.jetbrains.skiko.MetalApiKt")
        .getDeclaredMethod("chooseMetalAdapter", gpuPriority).invoke(null, auto)
    adapter.javaClass.getDeclaredField("ptr").apply { isAccessible = true }.get(adapter) as Long
}.getOrElse { 0L }

// Walks the AWT component tree following the known Skiko field path to DirectContext:
// WindowSkiaLayerComponent → redrawerManager → redrawer → contextHandler → context
private fun findSkikoContext(): DirectContext? {
    fun field(obj: Any, name: String) = runCatching {
        var cls: Class<*>? = obj.javaClass
        while (cls != null) {
            cls.declaredFields.firstOrNull { it.name == name }
                ?.apply { isAccessible = true }
                ?.let { return@runCatching it.get(obj) }
            cls = cls.superclass
        }
        null
    }.getOrNull()

    fun search(c: java.awt.Component): DirectContext? {
        runCatching {
            val ctx = field(field(field(field(c, "redrawerManager")!!, "redrawer")!!, "contextHandler")!!, "context")
            if (ctx is DirectContext) return ctx
        }
        return if (c is java.awt.Container) c.components.firstNotNullOfOrNull { search(it) } else null
    }
    return java.awt.Window.getWindows().firstNotNullOfOrNull { search(it) }
}

private var cachedSkikoCtx: DirectContext? = null
private fun skikoContext(): DirectContext? {
    if (cachedSkikoCtx != null) return cachedSkikoCtx
    return findSkikoContext().also { cachedSkikoCtx = it }
}

@Composable
actual fun FilamentView(modifier: Modifier, renderer: FilamentViewRenderer) {
    val jvmRenderer = renderer as FilamentRenderer
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var textureHandle by remember { mutableStateOf(0L) }

    LaunchedEffect(textureSize) {
        if (textureSize.width <= 0 || textureSize.height <= 0) return@LaunchedEffect
        if (textureHandle != 0L) FilamentTexture.nReleaseMetalTexture(textureHandle)
        val devicePtr = skikoMetalDevicePtr()
        textureHandle = jvmRenderer.createMetalTexture(devicePtr, textureSize.width, textureSize.height)
        jvmRenderer.initializeOffscreen(textureSize.width, textureSize.height, textureHandle)
    }

    var frameTime by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (isActive) { withFrameNanos { frameTime = it } }
    }

    // Hold the previous frame's snapshot until a new one is recorded so Skiko can always replay it.
    val prevSnapshot = remember { mutableStateOf<Image?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            if (textureHandle != 0L) FilamentTexture.nReleaseMetalTexture(textureHandle)
            prevSnapshot.value?.close()
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                if (textureHandle == 0L) return@drawBehind

                jvmRenderer.render(frameTime)

                drawIntoCanvas { canvas ->
                    val ctx = skikoContext() ?: return@drawIntoCanvas
                    val rt = makeMetalBackendRT(textureSize.width, textureSize.height, textureHandle)
                        ?: return@drawIntoCanvas
                    // Fresh surface each frame: forces Skia to re-adopt texture content written by Filament.
                    val surface = Surface.makeFromBackendRenderTarget(
                        ctx, rt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.BGRA_8888,
                        ColorSpace.sRGB, SurfaceProps()
                    ) ?: return@drawIntoCanvas

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
