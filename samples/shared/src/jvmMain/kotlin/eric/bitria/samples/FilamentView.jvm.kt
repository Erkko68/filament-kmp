package eric.bitria.samples

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.isActive
import org.jetbrains.skia.*

fun createMetalRenderTarget(width: Int, height: Int, ptr: Long): BackendRenderTarget? = try {
    val clazz = Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
    val method = clazz.methods.first { it.name == "access\$_nMakeMetal" }
    val handle = method.invoke(null, width, height, ptr) as Long
    BackendRenderTarget::class.java.getDeclaredConstructor(Long::class.java)
        .apply { isAccessible = true }.newInstance(handle) as BackendRenderTarget
} catch (e: Exception) {
    println("createMetalRenderTarget failed: ${e.message}")
    null
}

fun skikoFieldsOf(obj: Any): List<Pair<String, Any?>> {
    val result = mutableListOf<Pair<String, Any?>>()
    var cls: Class<*>? = obj.javaClass
    while (cls != null && cls != Any::class.java) {
        val pkg = cls.packageName
        if (pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("sun.")) break
        for (f in cls.declaredFields) {
            try {
                f.isAccessible = true
                result += f.name to f.get(obj)
            } catch (_: Exception) { }
        }
        cls = cls.superclass
    }
    return result
}

fun extractSkikoMetalContext(): Pair<Long, Long> {
    return try {
        val gpuPriorityClass = Class.forName("org.jetbrains.skiko.GpuPriority")
        val autoPriority = gpuPriorityClass.getMethod("valueOf", String::class.java)
            .invoke(null, "Auto")
        val adapter = Class.forName("org.jetbrains.skiko.MetalApiKt")
            .getDeclaredMethod("chooseMetalAdapter", gpuPriorityClass)
            .invoke(null, autoPriority)
        val devicePtr = adapter.javaClass
            .getDeclaredField("ptr")
            .apply { isAccessible = true }
            .get(adapter) as Long
        println("extractSkikoMetalContext: MTLDevice=0x${devicePtr.toString(16)}")
        Pair(devicePtr, 0L)
    } catch (e: Exception) {
        println("extractSkikoMetalContext failed: ${e.message}")
        Pair(0L, 0L)
    }
}

// Walk Skiko objects (up to `depth` levels deep) looking for a DirectContext field.
fun findDirectContextInObject(obj: Any, depth: Int): DirectContext? {
    if (depth <= 0) return null
    var cls: Class<*>? = obj.javaClass
    while (cls != null) {
        val pkg = cls.packageName
        if (pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("sun.")) break
        for (f in cls.declaredFields) {
            runCatching {
                f.isAccessible = true
                val v = f.get(obj) ?: return@runCatching
                if (v is DirectContext) {
                    println("findDirectContext: found in ${obj.javaClass.simpleName}.${f.name}")
                    return v
                }
                val vPkg = v.javaClass.packageName
                if (depth > 1 && (vPkg.startsWith("org.jetbrains.skiko") ||
                                   vPkg.startsWith("org.jetbrains.skia"))) {
                    findDirectContextInObject(v, depth - 1)?.let { return it }
                }
            }
        }
        cls = cls.superclass
    }
    return null
}

// Walk the AWT component tree to find the DirectContext held by MetalContextHandler.
// Path: Window → (children) → WindowSkiaLayerComponent → redrawerManager → redrawer → contextHandler → context
fun findSkikoDirectContext(): DirectContext? {
    fun searchComponent(c: java.awt.Component): DirectContext? {
        val ctx = findDirectContextInObject(c, depth = 5)
        if (ctx != null) return ctx
        if (c is java.awt.Container) {
            for (child in c.components) {
                searchComponent(child)?.let { return it }
            }
        }
        return null
    }
    return java.awt.Window.getWindows().firstNotNullOfOrNull { searchComponent(it) }
}

// Cached Skiko DirectContext (stable for window lifetime; lazy-found via reflection).
var cachedSkikoDirectContext: DirectContext? = null

fun getSkikoContext(nativeCanvas: Canvas): DirectContext? {
    // Fast path: returned from cache
    cachedSkikoDirectContext?.let { return it }

    // Try Canvas._owner (works when canvas is directly owned by a Surface)
    runCatching {
        val field = Canvas::class.java.getDeclaredField("_owner")
        field.isAccessible = true
        val owner = field.get(nativeCanvas)
        println("getSkikoContext: _owner type=${owner?.javaClass?.name}")
        (owner as? Surface)?.recordingContext
    }.getOrNull()?.let {
        cachedSkikoDirectContext = it
        return it
    }

    // Fall back: walk the AWT/Skiko component tree
    findSkikoDirectContext()?.also { cachedSkikoDirectContext = it }
        ?.let { return it }

    println("getSkikoContext: could not find DirectContext in any path")
    return null
}

@Composable
actual fun FilamentView(modifier: Modifier, renderer: FilamentViewRenderer) {
    val jvmRenderer = renderer as FilamentRenderer
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    var textureHandle by remember { mutableStateOf(0L) }
    var metalReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val (devicePtr, queuePtr) = extractSkikoMetalContext()
        jvmRenderer.setMetalContext(devicePtr, queuePtr)
        println("FilamentView: setMetalContext done device=0x${devicePtr.toString(16)}")
        metalReady = true
    }

    var frameTime by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (isActive) { withFrameNanos { frameTime = it } }
    }

    LaunchedEffect(textureSize, metalReady) {
        if (!metalReady) return@LaunchedEffect
        if (textureSize.width > 0 && textureSize.height > 0) {
            if (textureHandle != 0L) io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            textureHandle = jvmRenderer.createMetalTexture(textureSize.width, textureSize.height)
            jvmRenderer.initializeOffscreen(textureSize.width, textureSize.height, textureHandle)
            println("FilamentView: texture created handle=0x${textureHandle.toString(16)} size=${textureSize.width}x${textureSize.height}")
        }
    }

    // Cached read-surface in Skiko's context — must use same context as Compose canvas.
    val cachedReadSurface = remember { mutableStateOf<Pair<Surface, Long>?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            if (textureHandle != 0L) io.github.erkko68.filament.jni.Texture.nReleaseMetalTexture(textureHandle)
            cachedReadSurface.value?.first?.close()
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { textureSize = it.size }
            .drawBehind {
                val time = frameTime
                if (textureHandle == 0L) return@drawBehind

                jvmRenderer.render(time)
                jvmRenderer.engine!!.flushAndWait()

                drawIntoCanvas { canvas ->
                    val nativeCanvas = canvas.nativeCanvas

                    val skiaCtx = getSkikoContext(nativeCanvas) ?: return@drawIntoCanvas

                    val cached = cachedReadSurface.value
                    val readSurface = if (cached == null || cached.second != textureHandle) {
                        cached?.first?.close()
                        val rt = createMetalRenderTarget(textureSize.width, textureSize.height, textureHandle)
                            ?: return@drawIntoCanvas
                        Surface.makeFromBackendRenderTarget(
                            skiaCtx, rt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.BGRA_8888,
                            ColorSpace.sRGB, SurfaceProps()
                        )?.also { cachedReadSurface.value = it to textureHandle }
                            ?: run {
                                println("drawBehind: makeFromBackendRenderTarget failed")
                                return@drawIntoCanvas
                            }
                    } else {
                        cached.first
                    }

                    val snapshot = readSurface.makeImageSnapshot() ?: run {
                        println("drawBehind: makeImageSnapshot null")
                        return@drawIntoCanvas
                    }
                    nativeCanvas.drawImage(snapshot, 0f, 0f)
                    snapshot.close()

                    // Blue dot — proof of draw path reached
                    nativeCanvas.drawCircle(30f, 30f, 10f, Paint().apply { color = 0xFF0000FF.toInt() })
                }
            }
    )
}
