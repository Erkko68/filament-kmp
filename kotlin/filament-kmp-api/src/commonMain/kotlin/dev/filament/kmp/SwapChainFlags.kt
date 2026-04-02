package dev.filament.kmp

/**
 * Flags that a <code>SwapChain</code> can be created with to control behavior.
 *
 * @see Engine#createSwapChain
 * @see Engine#createSwapChainFromNativeSurface
 */
expect object SwapChainFlags {
    val CONFIG_DEFAULT: Long

    /**
     * This flag indicates that the <code>SwapChain</code> must be allocated with an
     * alpha-channel.
     */
    val CONFIG_TRANSPARENT: Long

    /**
     * This flag indicates that the <code>SwapChain</code> may be used as a source surface
     * for reading back render results. This config must be set when creating
     * any <code>SwapChain</code> that will be used as the source for a blit operation.
     *
     * @see Renderer#copyFrame
     */
    val CONFIG_READABLE: Long

    /**
     * Indicates that the native X11 window is an XCB window rather than an XLIB window.
     * This is ignored on non-Linux platforms and in builds that support only one X11 API.
     */
    val CONFIG_ENABLE_XCB: Long

    /**
     * Indicates that the SwapChain must automatically perform linear to sRGB encoding.
     *
     * This flag is ignored if isSRGBSwapChainSupported() is false.
     *
     * When using this flag, post-processing should be disabled.
     *
     * @see SwapChain#isSRGBSwapChainSupported
     * @see View#setPostProcessingEnabled
     */
    val CONFIG_SRGB_COLORSPACE: Long

    /**
     * Indicates that this SwapChain should allocate a stencil buffer in addition to a depth buffer.
     *
     * This flag is necessary when using View::setStencilBufferEnabled and rendering directly into
     * the SwapChain (when post-processing is disabled).
     *
     * The specific format of the stencil buffer depends on platform support. The following pixel
     * formats are tried, in order of preference:
     *
     * Depth only (without CONFIG_HAS_STENCIL_BUFFER):
     * - DEPTH32F
     * - DEPTH24
     *
     * Depth + stencil (with CONFIG_HAS_STENCIL_BUFFER):
     * - DEPTH32F_STENCIL8
     * - DEPTH24F_STENCIL8
     *
     * Note that enabling the stencil buffer may hinder depth precision and should only be used if
     * necessary.
     *
     * @see View#setStencilBufferEnabled
     * @see View#setPostProcessingEnabled
     */
    val CONFIG_HAS_STENCIL_BUFFER: Long

    /**
     * The SwapChain contains protected content. Only supported when isProtectedContentSupported()
     * is true.
     */
    val CONFIG_PROTECTED_CONTENT: Long

    /**
     * Indicates that the SwapChain is configured to use Multi-Sample Anti-Aliasing (MSAA) with the
     * given sample points within each pixel. Only supported when isMSAASwapChainSupported(4) is
     * true.
     *
     * This is only supported by EGL(Android). Other GL platforms (GLX, WGL, etc) don't support it
     * because the swapchain MSAA settings must be configured before window creation.
     */
    val CONFIG_MSAA_4_SAMPLES: Long
}

