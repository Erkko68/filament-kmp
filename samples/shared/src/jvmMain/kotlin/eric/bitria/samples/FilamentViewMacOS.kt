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
import java.lang.reflect.Method

@Composable
internal fun FilamentViewMacOS(modifier: Modifier, controller: FilamentController) {
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var textureHandle by remember { mutableStateOf(0L) }
    val prevSnapshot = remember { mutableStateOf<Image?>(null) }

    LaunchedEffect(textureSize) {
        if (textureSize.width <= 0 || textureSize.height <= 0) return@LaunchedEffect

        val engine = controller.engine ?: return@LaunchedEffect
        
        prevSnapshot.value?.close()
        prevSnapshot.value = null
        TextureUtils.releaseSharedTexture(textureHandle)
        
        val devicePtr = SkikoMacOSInterop.getDevicePtr()
        textureHandle = TextureUtils.createSharedTexture(
            devicePtr, 0L, textureSize.width, textureSize.height
        )
        
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
                    val ctx = SkikoMacOSInterop.getDirectContext() ?: return@drawIntoCanvas
                    val rt = SkikoMacOSInterop.makeBackendRenderTarget(textureSize.width, textureSize.height, textureHandle)
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

internal object SkikoMacOSInterop {
    private fun reflectField(obj: Any, name: String): Any? = runCatching {
        var cls: Class<*>? = obj.javaClass
        while (cls != null) {
            cls.declaredFields.firstOrNull { it.name == name }
                ?.apply { isAccessible = true }
                ?.let { return@runCatching it.get(obj) }
            cls = cls.superclass
        }
        null
    }.getOrNull()

    private val nMakeMetalMethod: Method? by lazy {
        runCatching {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
                .methods.first { it.name == "access\$_nMakeMetal" }
        }.getOrNull()
    }

    private val backendRTConstructor by lazy {
        runCatching {
            BackendRenderTarget::class.java
                .getDeclaredConstructor(Long::class.java)
                .apply { isAccessible = true }
        }.getOrNull()
    }

    fun makeBackendRenderTarget(width: Int, height: Int, mtlTexture: Long): BackendRenderTarget? =
        runCatching {
            val handle = nMakeMetalMethod!!.invoke(null, width, height, mtlTexture) as Long
            backendRTConstructor!!.newInstance(handle) as BackendRenderTarget
        }.getOrNull()

    fun getDirectContext(): DirectContext? = runCatching {
        val window = java.awt.Window.getWindows().firstOrNull() ?: return null
        val composePanel = reflectField(window, "composePanel") as? java.awt.Container ?: return null
        for (child in composePanel.components) {
            val mgr = reflectField(child, "redrawerManager") ?: continue
            val redrawer = reflectField(mgr, "redrawer") ?: continue
            val ctxHandler = reflectField(redrawer, "contextHandler") ?: continue
            val ctx = reflectField(ctxHandler, "context")
            if (ctx is DirectContext) return ctx
        }
        null
    }.getOrNull()

    fun getDevicePtr(): Long = runCatching {
        val gpuPriority = Class.forName("org.jetbrains.skiko.GpuPriority")
        val auto = gpuPriority.getMethod("valueOf", String::class.java).invoke(null, "Auto")
        val adapter = Class.forName("org.jetbrains.skiko.MetalApiKt")
            .getDeclaredMethod("chooseMetalAdapter", gpuPriority)
            .invoke(null, auto)
        adapter.javaClass.getDeclaredField("ptr").apply { isAccessible = true }.get(adapter) as Long
    }.getOrElse { 0L }
}
