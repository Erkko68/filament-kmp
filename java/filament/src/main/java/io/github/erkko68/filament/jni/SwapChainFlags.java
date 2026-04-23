/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erkko68.filament.jni;

/**
 * Flags that a {@link SwapChain} can be created with to control behavior.
 */
public final class SwapChainFlags {
    public static final long CONFIG_DEFAULT = 0x0;

    /**
     * This flag indicates that the SwapChain must be allocated with an alpha-channel.
     */
    public static final long CONFIG_TRANSPARENT = 0x1;

    /**
     * This flag indicates that the SwapChain may be used as a source surface for reading back
     * render results.
     */
    public static final long CONFIG_READABLE = 0x2;

    /**
     * Indicates that the native X11 window is an XCB window rather than an XLIB window.
     */
    public static final long CONFIG_ENABLE_XCB = 0x4;

    /**
     * Indicates that the SwapChain must automatically perform linear to sRGB encoding.
     */
    public static final long CONFIG_SRGB_COLORSPACE = 0x10;

    /**
     * Indicates that this SwapChain should allocate a stencil buffer in addition to a depth buffer.
     */
    public static final long CONFIG_HAS_STENCIL_BUFFER = 0x20;

    /**
     * The SwapChain contains protected content.
     */
    public static final long CONFIG_PROTECTED_CONTENT = 0x40;

    /**
     * Indicates that the SwapChain is configured to use Multi-Sample Anti-Aliasing (MSAA) with 4 samples.
     */
    public static final long CONFIG_MSAA_4_SAMPLES = 0x80;

    private SwapChainFlags() {}
}
