package io.github.erkko68.filament.compose.internal

import org.jetbrains.skia.DirectContext
import java.awt.Component
import java.awt.Container
import java.awt.Window

/**
 * Reflective access to Skiko/Compose-Desktop internals so Filament can share GPU
 * resources with the on-screen Skia renderer. Pinned to Skiko 0.9.37.x — field
 * layout (notably [MetalRedrawer.adapter.ptr] and the GL redrawers'
 * [context] field) is what we depend on.
 *
 * Three things to extract from the live window:
 *  - [findDirectContext]: the [DirectContext] owning the on-screen canvas. Skia
 *    images and backend textures must be created against this same context or
 *    they silently fail to draw.
 *  - [findMetalDevicePtr]: the `MTLDevice*` driving Skiko's Metal renderer.
 *    Passed to [io.github.erkko68.filament.jni.Engine.Builder.sharedContext]
 *    so Filament's Metal backend uses the same device — and to
 *    `GraphicsInterop.nCreateMetalTexture` for the shared texture.
 *  - [findGLContextHandle]: the native `GLXContext` / `HGLRC` driving Skiko's
 *    OpenGL renderer. Passed to Filament's Engine so its GL backend creates a
 *    context in the same share group.
 */
internal object SkikoInterop {

    private fun log(msg: String) = println("[SkikoInterop] $msg")

    fun findDirectContext(): DirectContext? {
        val redrawer = findRedrawer() ?: run { log("findDirectContext: no redrawer"); return null }
        val handler = reflectField(redrawer, "contextHandler") ?: run {
            log("findDirectContext: redrawer=${redrawer.javaClass.simpleName} has no contextHandler field")
            return null
        }
        val ctx = reflectField(handler, "context") as? DirectContext
        if (ctx == null) log("findDirectContext: handler=${handler.javaClass.simpleName} context is null/wrong type")
        return ctx
    }

    fun findMetalDevicePtr(): Long {
        val redrawer = findRedrawer() ?: return 0L
        val adapter = reflectField(redrawer, "adapter") ?: run {
            log("findMetalDevicePtr: redrawer=${redrawer.javaClass.simpleName} has no adapter field")
            return 0L
        }
        val ptr = (reflectField(adapter, "ptr") as? Long) ?: 0L
        log("findMetalDevicePtr: redrawer=${redrawer.javaClass.simpleName} adapter=${adapter.javaClass.simpleName} ptr=0x${ptr.toString(16)}")
        return ptr
    }

    fun findGLContextHandle(): Long {
        val redrawer = findRedrawer() ?: return 0L
        val ctx = (reflectField(redrawer, "context") as? Long) ?: 0L
        log("findGLContextHandle: redrawer=${redrawer.javaClass.simpleName} context=0x${ctx.toString(16)}")
        return ctx
    }

    // The redrawer is held by Compose's SkiaLayer wrapper. The Compose layout
    // wraps the SkiaLayer inside a chain of AWT containers; locate by field
    // name rather than class so we don't pin to a specific Compose internal.
    private var loggedNoRedrawer = false

    private fun findRedrawer(): Any? {
        val windows = Window.getWindows()
        for (window in windows) {
            findRedrawerIn(window)?.let { return it }
        }
        if (!loggedNoRedrawer) {
            log("findRedrawer: no redrawer found across ${windows.size} windows")
            loggedNoRedrawer = true
        }
        return null
    }

    private fun findRedrawerIn(component: Component): Any? {
        // SkiaLayer holds `redrawer` directly; some Compose wrappers go through
        // `redrawerManager.redrawer`. Try both.
        reflectField(component, "redrawer")?.let { return it }
        reflectField(component, "redrawerManager")
            ?.let { reflectField(it, "redrawer") }
            ?.let { return it }

        if (component is Container) {
            for (child in component.components) {
                findRedrawerIn(child)?.let { return it }
            }
        }
        return null
    }

    private fun reflectField(obj: Any, name: String): Any? {
        var cls: Class<*>? = obj.javaClass
        while (cls != null && cls != Any::class.java) {
            try {
                val field = cls.getDeclaredField(name)
                field.isAccessible = true
                return field.get(obj)
            } catch (_: NoSuchFieldException) {
                cls = cls.superclass
            } catch (_: Throwable) {
                return null
            }
        }
        return null
    }
}
