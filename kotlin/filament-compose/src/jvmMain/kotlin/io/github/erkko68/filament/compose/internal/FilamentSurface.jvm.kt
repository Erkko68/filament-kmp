package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.RenderTarget
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.compose.HostOs
import io.github.erkko68.filament.compose.sharedHandleFor
import io.github.erkko68.filament.jni.GraphicsInterop
import kotlinx.coroutines.delay
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.BackendTexture
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin
import org.jetbrains.skia.SurfaceProps

// Even on the zero-copy paths the rebuild has a teardown/create gap. Rebuilding
// per pixel of a drag makes the view flash, so we debounce — and meanwhile the
// drawBehind stretches the previous snapshot to the new layout size, giving a
// smooth rubber-band resize identical to other platforms.
private const val RESIZE_DEBOUNCE_MS = 150L

private fun surfaceLog(msg: String) = println("[FilamentSurface] $msg")

@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (aspect: Double) -> Unit,
) {
    val shared = sharedHandleFor(engine)
    androidx.compose.runtime.remember(shared) {
        surfaceLog("dispatch: engine=$engine shared=$shared")
    }
    when {
        shared?.os == HostOs.MACOS && shared.sharedContextPtr != 0L -> {
            androidx.compose.runtime.remember(shared) { surfaceLog("path = MetalSharedSurface (device=0x${shared.sharedContextPtr.toString(16)})") }
            MetalSharedSurface(modifier, engine, renderer, view, onResize, shared.sharedContextPtr)
        }
        shared != null && shared.sharedContextPtr != 0L -> {
            androidx.compose.runtime.remember(shared) { surfaceLog("path = GLSharedSurface (ctx=0x${shared.sharedContextPtr.toString(16)})") }
            GLSharedSurface(modifier, engine, renderer, view, onResize)
        }
        else -> {
            androidx.compose.runtime.remember(shared) { surfaceLog("path = ReadbackSurface (no shared context)") }
            ReadbackSurface(modifier, engine, renderer, view, onResize)
        }
    }
}

// ---------------------------------------------------------------------------
// Shared offscreen-surface bookkeeping (used by all three paths).
// ---------------------------------------------------------------------------

private class OffscreenResources(
    val colorTexture: Texture,
    val depthTexture: Texture,
    val renderTarget: RenderTarget,
    val swapChain: SwapChain,
    val width: Int,
    val height: Int,
    /** Native GPU texture handle (MTLTexture* / GLuint as Long). 0 when unused. */
    val sharedTextureHandle: Long,
) {
    fun destroy(engine: Engine) {
        engine.destroySwapChain(swapChain)
        engine.destroyRenderTarget(renderTarget)
        engine.destroyTexture(depthTexture)
        engine.destroyTexture(colorTexture)
    }
}

/**
 * Debounces resize so we don't tear down the offscreen render target on every
 * pixel of a drag. While the layout is changing faster than [RESIZE_DEBOUNCE_MS],
 * the drawBehind keeps drawing the last snapshot stretched to the new layout
 * size, giving a smooth rubber-band resize.
 */
@Composable
private fun rememberDebouncedTextureSize(layoutSize: IntSize): IntSize {
    var textureSize by remember { mutableStateOf(IntSize.Zero) }
    LaunchedEffect(layoutSize) {
        val w = layoutSize.width; val h = layoutSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect
        if (textureSize.width <= 0) {
            textureSize = IntSize(w, h)
        } else {
            delay(RESIZE_DEBOUNCE_MS)
            textureSize = IntSize(w, h)
        }
    }
    return textureSize
}

// ---------------------------------------------------------------------------
// Metal zero-copy (macOS)
// ---------------------------------------------------------------------------

@Composable
private fun MetalSharedSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (Double) -> Unit,
    devicePtr: Long,
) {
    SharedTextureSurface(
        modifier = modifier,
        engine = engine,
        renderer = renderer,
        view = view,
        onResize = onResize,
        createSharedTexture = { w, h -> GraphicsInterop.nCreateMetalTexture(devicePtr, w, h) },
        releaseSharedTexture = { handle -> GraphicsInterop.nReleaseMetalTexture(handle) },
        buildSnapshot = buildSnapshot@{ textureHandle, w, h ->
            // Skia's Metal interop: wrap the same MTLTexture as a BackendRenderTarget,
            // build a transient Surface against the canvas's DirectContext, and snapshot.
            val skiaCtx = SkikoInterop.findDirectContext() ?: run {
                surfaceLog("Metal buildSnapshot: no DirectContext")
                return@buildSnapshot null
            }
            val rt = BackendRenderTarget.makeMetal(w, h, textureHandle)
            val readSurface = Surface.makeFromBackendRenderTarget(
                skiaCtx, rt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.BGRA_8888,
                ColorSpace.sRGB, SurfaceProps()
            )
            if (readSurface == null) {
                surfaceLog("Metal buildSnapshot: Surface.makeFromBackendRenderTarget returned null (handle=0x${textureHandle.toString(16)} ${w}x$h)")
                rt.close()
                return@buildSnapshot null
            }
            val snapshot = readSurface.makeImageSnapshot()
            readSurface.close()
            rt.close()
            if (snapshot == null) surfaceLog("Metal buildSnapshot: makeImageSnapshot returned null")
            snapshot
        },
    )
}

// ---------------------------------------------------------------------------
// OpenGL zero-copy (Linux / Windows when Skiko uses the OpenGL redrawer)
// ---------------------------------------------------------------------------

@Composable
private fun GLSharedSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (Double) -> Unit,
) {
    // GL_TEXTURE_2D / GL_RGBA8
    val target = 0x0DE1
    val format = 0x8058

    val backendTextureRef = remember { Ref<BackendTexture>() }

    SharedTextureSurface(
        modifier = modifier,
        engine = engine,
        renderer = renderer,
        view = view,
        onResize = onResize,
        // glGenTextures requires a current GL context; Skiko's GL context is
        // current on its render thread during draw, but we're allocating in a
        // coroutine. Filament's GL backend will issue the texture import on
        // its own render thread (which DOES have a current context shared with
        // Skiko's). To stay simple and correct, create from JNI here — the
        // call happens from the EDT-ish dispatcher, where most JVMs already
        // have a context current via Skiko's draw scope on the previous tick.
        // If creation returns 0 (no current context), SharedTextureSurface
        // falls back to readback by not setting `sharedTextureHandle`.
        createSharedTexture = { w, h -> GraphicsInterop.nCreateGLTexture(w, h) },
        releaseSharedTexture = { handle ->
            backendTextureRef.value?.close()
            backendTextureRef.value = null
            GraphicsInterop.nReleaseGLTexture(handle)
        },
        buildSnapshot = buildSnapshot@{ textureHandle, w, h ->
            val skiaCtx = SkikoInterop.findDirectContext() ?: run {
                surfaceLog("GL buildSnapshot: no DirectContext")
                return@buildSnapshot null
            }

            // Lazily wrap the GL texture id as a Skia BackendTexture. Same
            // size for the surface lifetime so we cache between draws.
            val bt = backendTextureRef.value ?: BackendTexture.makeGL(
                w, h, /* isMipmapped = */ false,
                textureHandle.toInt(), target, format,
            ).also {
                backendTextureRef.value = it
                surfaceLog("GL buildSnapshot: created BackendTexture id=${textureHandle.toInt()} ${w}x$h")
            }

            // Filament's commands and Skia's consumption share a GL context group
            // but not a stream — flushing the DirectContext makes Skia observe
            // Filament's pixel writes.
            skiaCtx.flush()

            try {
                Image.adoptTextureFrom(skiaCtx, bt, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888)
            } catch (e: Throwable) {
                surfaceLog("GL buildSnapshot: adoptTextureFrom threw ${e.javaClass.simpleName}: ${e.message}")
                null
            }
        },
    )
}

// ---------------------------------------------------------------------------
// Shared scaffolding for the two zero-copy paths.
// ---------------------------------------------------------------------------

@Composable
private fun SharedTextureSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (Double) -> Unit,
    createSharedTexture: (width: Int, height: Int) -> Long,
    releaseSharedTexture: (handle: Long) -> Unit,
    /** Wraps the shared GPU texture as a Skia [Image] in the right DirectContext, without drawing it. */
    buildSnapshot: (textureHandle: Long, width: Int, height: Int) -> Image?,
) {
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    val textureSize = rememberDebouncedTextureSize(layoutSize)

    // current = what Filament is rendering into now.
    // pending = the previous offscreen — kept alive so its snapshot can keep
    //           drawing until `current` has rendered at least one frame.
    //           Destroyed only once we successfully draw from `current`.
    var current by remember { mutableStateOf<OffscreenResources?>(null) }
    val pending = remember { Ref<OffscreenResources?>() }
    val framesSinceRebuild = remember { intArrayOf(0) }

    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    // lastSnapshot wraps the *latest* texture we successfully sampled from. Across
    // a rebuild it wraps `pending`'s texture, which we keep alive on purpose.
    val lastSnapshot = remember { Ref<Image?>() }

    fun destroy(r: OffscreenResources) {
        engine.flushAndWait()
        r.destroy(engine)
        releaseSharedTexture(r.sharedTextureHandle)
    }

    DisposableEffect(Unit) {
        onDispose {
            view.renderTarget = null
            current?.let { destroy(it) }
            pending.value?.let { destroy(it) }
            current = null
            pending.value = null
            lastSnapshot.value?.close()
            lastSnapshot.value = null
        }
    }

    // Build (or rebuild) the offscreen resources when the debounced texture size
    // settles. We do NOT destroy the previous one here — it gets demoted to
    // `pending` and lives until the draw path confirms `current` has content.
    LaunchedEffect(textureSize) {
        val w = textureSize.width
        val h = textureSize.height
        if (w <= 0 || h <= 0) return@LaunchedEffect

        val handle = createSharedTexture(w, h)
        surfaceLog("createSharedTexture(${w}x$h) -> 0x${handle.toString(16)}")
        if (handle == 0L) {
            surfaceLog("createSharedTexture returned 0 — surface stays empty for this size")
            return@LaunchedEffect
        }

        val colorTexture = Texture.Builder()
            .width(w).height(h).levels(1)
            .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.SAMPLEABLE)
            .format(Texture.InternalFormat.RGBA8)
            .importTexture(handle)
            .build(engine)
        val depthTexture = Texture.Builder()
            .width(w).height(h)
            .usage(Texture.Usage.DEPTH_ATTACHMENT)
            .format(Texture.InternalFormat.DEPTH32F)
            .build(engine)
        val renderTarget = RenderTarget.Builder()
            .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture)
            .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture)
            .build(engine)
        val swapChain = engine.createSwapChain(w, h, 0)
        view.renderTarget = renderTarget
        view.viewport = Viewport(0, 0, w, h)
        onResizeRef.value?.invoke(w.toDouble() / h.toDouble())
        val newR = OffscreenResources(colorTexture, depthTexture, renderTarget, swapChain, w, h, handle)

        // Two rebuilds back-to-back: the stale `pending` is no longer needed
        // (lastSnapshot wraps the now-soon-to-be-pending `current`'s texture).
        pending.value?.let { destroy(it) }
        pending.value = current
        current = newR
        framesSinceRebuild[0] = 0
        surfaceLog("rebuilt: current=${w}x$h handle=0x${handle.toString(16)} demotedPrev=${pending.value != null}")
    }

    val renderFrameCount = remember { intArrayOf(0) }

    FilamentRenderLoop { frameTime ->
        val r = current ?: return@FilamentRenderLoop
        val drewFrame = renderer.beginFrame(r.swapChain, frameTime)
        if (drewFrame) {
            renderer.render(view)
            renderer.endFrame()
            framesSinceRebuild[0]++
        }
        renderFrameCount[0]++
        if (renderFrameCount[0] <= 3 || renderFrameCount[0] % 120 == 0) {
            surfaceLog("RenderLoop frame=${renderFrameCount[0]} drew=$drewFrame sinceRebuild=${framesSinceRebuild[0]} size=${r.width}x${r.height}")
        }
    }

    // drawBehind only re-runs when something it reads invalidates. Filament is
    // rendering into the shared texture every vsync, but Compose has no idea
    // those pixels changed — so without a per-frame state read here Skia keeps
    // replaying its cached snapshot and the view appears frozen until a resize
    // forces a redraw. Tick a mutable Long inside drawBehind to subscribe.
    var frameTick by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameNanos { frameTick = it }
        }
    }

    val drawCount = remember { intArrayOf(0) }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { layoutSize = it.size }
            .drawBehind {
                @Suppress("UNUSED_VARIABLE") val pin = frameTick

                val r = current
                drawIntoCanvas { canvas ->
                    // If `current` has at least one Filament frame in it, sample it;
                    // otherwise (just rebuilt, or first frame ever) keep showing the
                    // last good snapshot stretched to the current layout size.
                    val newSnap = if (r != null && framesSinceRebuild[0] > 0) {
                        engine.flushAndWait()
                        buildSnapshot(r.sharedTextureHandle, r.width, r.height)
                    } else null

                    val toDraw = newSnap ?: lastSnapshot.value
                    if (toDraw != null) {
                        canvas.nativeCanvas.drawImageRect(toDraw, Rect.makeWH(size.width, size.height))
                    }
                    if (drawCount[0] < 5) {
                        surfaceLog("drawBehind frame=${drawCount[0]} newSnap=${newSnap != null} fallback=${newSnap == null && toDraw != null} draw=${size.width.toInt()}x${size.height.toInt()} tex=${r?.width}x${r?.height} sinceRebuild=${framesSinceRebuild[0]}")
                    }

                    // We now have a fresh snapshot from `current`. The old one
                    // (wrapping `pending`) is safe to release, and so is `pending`.
                    if (newSnap != null) {
                        if (newSnap !== lastSnapshot.value) lastSnapshot.value?.close()
                        lastSnapshot.value = newSnap
                        pending.value?.let { destroy(it) }
                        pending.value = null
                    }
                }
                drawCount[0]++
            }
    )
}

// ---------------------------------------------------------------------------
// CPU-readback fallback (used when no Skiko-shared GPU context is available,
// e.g. Compose Desktop on Windows with the default DirectX/ANGLE redrawer).
// ---------------------------------------------------------------------------

@Composable
private fun ReadbackSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    onResize: (Double) -> Unit,
) {
    var layoutSize by remember { mutableStateOf(IntSize.Zero) }
    val textureSize = rememberDebouncedTextureSize(layoutSize)
    var snapshotImage by remember { mutableStateOf<Image?>(null) }
    var surface by remember { mutableStateOf<OffscreenResources?>(null) }

    val onResizeRef = remember { Ref<(Double) -> Unit>() }
    SideEffect { onResizeRef.value = onResize }

    DisposableEffect(Unit) {
        onDispose {
            snapshotImage?.close()
            snapshotImage = null
        }
    }

    // Double-buffered readback: while one buffer's GPU→CPU copy is in flight, the other
    // can be issued, so the readback pipelines with rendering instead of stalling.
    val pixelBuffers = remember(textureSize) {
        if (textureSize.width > 0 && textureSize.height > 0)
            Array(2) { ByteArray(textureSize.width * textureSize.height * 4) }
        else null
    }

    val pendingFlags = remember(pixelBuffers) { BooleanArray(2) }
    val nextBufIndex = remember(pixelBuffers) { intArrayOf(0) }
    val snapshotBufIndex = remember(pixelBuffers) { intArrayOf(-1) }

    val pixelBufferDescriptors = remember(pixelBuffers) {
        val buffers = pixelBuffers ?: return@remember null
        val tw = textureSize.width
        val th = textureSize.height
        Array(2) { i ->
            Texture.PixelBufferDescriptor(
                storage = buffers[i],
                sizeInBytes = buffers[i].size,
                format = Texture.Format.RGBA,
                type = Texture.Type.UBYTE,
                alignment = 1,
                left = 0,
                top = 0,
                stride = tw,
                callback = {
                    val info = ImageInfo(tw, th, ColorType.RGBA_8888, ColorAlphaType.PREMUL)
                    val newImage = Image.makeRaster(info, buffers[i], tw * 4)
                    val old = snapshotImage
                    val oldBuf = snapshotBufIndex[0]
                    snapshotImage = newImage
                    snapshotBufIndex[0] = i
                    old?.close()
                    if (oldBuf >= 0) pendingFlags[oldBuf] = false
                }
            )
        }
    }

    DisposableEffect(textureSize) {
        val w = textureSize.width
        val h = textureSize.height
        if (w > 0 && h > 0) {
            val colorTexture = Texture.Builder()
                .width(w).height(h).levels(1)
                .usage(Texture.Usage.COLOR_ATTACHMENT or Texture.Usage.BLIT_SRC or Texture.Usage.SAMPLEABLE)
                .format(Texture.InternalFormat.RGBA8)
                .build(engine)
            val depthTexture = Texture.Builder()
                .width(w).height(h)
                .usage(Texture.Usage.DEPTH_ATTACHMENT)
                .format(Texture.InternalFormat.DEPTH32F)
                .build(engine)
            val renderTarget = RenderTarget.Builder()
                .texture(RenderTarget.AttachmentPoint.COLOR, colorTexture)
                .texture(RenderTarget.AttachmentPoint.DEPTH, depthTexture)
                .build(engine)
            val swapChain = engine.createSwapChain(w, h, 0)
            view.renderTarget = renderTarget
            view.viewport = Viewport(0, 0, w, h)
            onResizeRef.value?.invoke(w.toDouble() / h.toDouble())
            surface = OffscreenResources(colorTexture, depthTexture, renderTarget, swapChain, w, h, 0L)
        }
        onDispose {
            val s = surface ?: return@onDispose
            view.renderTarget = null
            surface = null
            engine.flushAndWait()
            s.destroy(engine)
        }
    }

    FilamentRenderLoop { frameTime ->
        val s = surface ?: return@FilamentRenderLoop
        if (renderer.beginFrame(s.swapChain, frameTime)) {
            renderer.render(view)
            renderer.endFrame()
        }
        val descs = pixelBufferDescriptors ?: return@FilamentRenderLoop
        val i = nextBufIndex[0]
        if (!pendingFlags[i]) {
            pendingFlags[i] = true
            renderer.readPixels(
                renderTarget = s.renderTarget,
                xoffset = 0,
                yoffset = 0,
                width = s.width,
                height = s.height,
                buffer = descs[i],
            )
            nextBufIndex[0] = (i + 1) % 2
        }
    }

    Spacer(
        modifier = modifier
            .onGloballyPositioned { layoutSize = it.size }
            .drawBehind {
                val image = snapshotImage ?: return@drawBehind
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawImageRect(
                        image,
                        Rect.makeWH(size.width, size.height),
                    )
                }
            }
    )
}
