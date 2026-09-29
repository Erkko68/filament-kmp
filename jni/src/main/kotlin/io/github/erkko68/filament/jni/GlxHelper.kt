package io.github.erkko68.filament.jni

/** Linux-only helpers for sharing GL textures between Filament and skiko's GLX context. */
object GlxHelper {
    // The native side finds JAWT_GetAWT in the already-loaded libjawt instead of linking it.
    init { System.loadLibrary("jawt") }

    /** The `GLXContext` behind skiko's `LinuxOpenGLRedrawer.context`, which points at one. */
    @JvmStatic external fun nSkikoContext(skikoContext: Long): Long
    /**
     * A context in the current context's share group whose FBConfig Filament's PlatformGLX can find
     * (skiko's context, made from a visual, has none); 0 on failure. Only needed while an engine builds.
     */
    @JvmStatic external fun nCreateBridgeContext(): Long
    /** Destroys a context from [nCreateBridgeContext]; the context it bridged must be current. */
    @JvmStatic external fun nDestroyBridgeContext(context: Long)
    /** Locks the AWT [component]'s drawing surface and makes [glxContext] current on it; 0 on failure. */
    @JvmStatic external fun nBegin(component: Any, glxContext: Long): Long
    /** Unlocks the drawing surface locked by [nBegin]. */
    @JvmStatic external fun nEnd(session: Long)
    /** An RGBA8 texture in the current context's share group; 0 on failure. */
    @JvmStatic external fun nCreateTexture(width: Int, height: Int): Int
    /** A framebuffer of the current context with [texture] as its color attachment; 0 on failure. */
    @JvmStatic external fun nCreateFramebuffer(texture: Int): Int
    /** Deletes a texture and framebuffer from [nCreateTexture] / [nCreateFramebuffer] (0 skips either). */
    @JvmStatic external fun nDestroy(texture: Int, framebuffer: Int)
}
