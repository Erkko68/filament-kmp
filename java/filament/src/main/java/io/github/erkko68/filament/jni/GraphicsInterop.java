package io.github.erkko68.filament.jni;

/**
 * Native helpers for creating GPU textures that can be shared between Filament
 * and the host renderer (Skia/Compose). Platform-specific:
 * <ul>
 *     <li>macOS: Metal — {@link #nCreateMetalTexture(long, int, int)} returns a
 *     CFRetained {@code id&lt;MTLTexture&gt;}, released by
 *     {@link #nReleaseMetalTexture(long)}.</li>
 *     <li>Linux / Windows: OpenGL — {@link #nCreateGLTexture(int, int)} returns
 *     a {@code GLuint} (an RGBA8 GL_TEXTURE_2D). The caller's GL context must be
 *     current on the calling thread.</li>
 * </ul>
 */
public final class GraphicsInterop {
    static {
        Filament.init();
    }

    private GraphicsInterop() {}

    public static native long nCreateMetalTexture(long devicePtr, int width, int height);
    public static native void nReleaseMetalTexture(long handle);

    public static native long nCreateGLTexture(int width, int height);
    public static native void nReleaseGLTexture(long handle);
}
