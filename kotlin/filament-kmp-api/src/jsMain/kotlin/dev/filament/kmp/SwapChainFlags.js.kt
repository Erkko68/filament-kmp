package dev.filament.kmp

actual object SwapChainFlags {
    actual val CONFIG_DEFAULT: Long = 0x0
    actual val CONFIG_TRANSPARENT: Long = 0x1
    actual val CONFIG_READABLE: Long = 0x2
    actual val CONFIG_ENABLE_XCB: Long = 0x4
    actual val CONFIG_SRGB_COLORSPACE: Long = 0x10
    actual val CONFIG_HAS_STENCIL_BUFFER: Long = 0x20
    actual val CONFIG_PROTECTED_CONTENT: Long = 0x40
    actual val CONFIG_MSAA_4_SAMPLES: Long = 0x80
}

