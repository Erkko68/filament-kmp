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

package io.github.erkko68.filament;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.ReadOnlyBufferException;

/**
 * A Renderer instance represents an operating system's window.
 */
public class Renderer {
    private final Engine mEngine;
    private long mNativeObject;
    private DisplayInfo mDisplayInfo;
    private FrameRateOptions mFrameRateOptions;
    private ClearOptions mClearOptions;

    public static class DisplayInfo {
        public float refreshRate = 60.0f;
        @Deprecated public long presentationDeadlineNanos = 0;
        @Deprecated public long vsyncOffsetNanos = 0;
    }

    public static class FrameRateOptions {
        public float interval = 1.0f;
        public float headRoomRatio = 0.0f;
        public float scaleRate = 1.0f / 15.0f;
        public int history = 15;
    }

    public static class ClearOptions {
        @NotNull public float[] clearColor = { 0.0f, 0.0f, 0.0f, 0.0f };
        public boolean clear = false;
        public boolean discard = true;
    }

    public static final int MIRROR_FRAME_FLAG_COMMIT = 0x1;
    public static final int MIRROR_FRAME_FLAG_SET_PRESENTATION_TIME = 0x2;
    public static final int MIRROR_FRAME_FLAG_CLEAR = 0x4;

    Renderer(@NotNull Engine engine, long nativeRenderer) {
        mEngine = engine;
        mNativeObject = nativeRenderer;
    }

    public void setDisplayInfo(@NotNull DisplayInfo info) {
        mDisplayInfo = info;
        nSetDisplayInfo(getNativeObject(), info.refreshRate);
    }

    @NotNull
    public DisplayInfo getDisplayInfo() {
        if (mDisplayInfo == null) mDisplayInfo = new DisplayInfo();
        return mDisplayInfo;
    }

    public void setFrameRateOptions(@NotNull FrameRateOptions options) {
        mFrameRateOptions = options;
        nSetFrameRateOptions(getNativeObject(), options.interval, options.headRoomRatio, options.scaleRate, options.history);
    }

    @NotNull
    public FrameRateOptions getFrameRateOptions() {
        if (mFrameRateOptions == null) mFrameRateOptions = new FrameRateOptions();
        return mFrameRateOptions;
    }

    public void setClearOptions(@NotNull ClearOptions options) {
        mClearOptions = options;
        nSetClearOptions(getNativeObject(), options.clearColor[0], options.clearColor[1], options.clearColor[2], options.clearColor[3], options.clear, options.discard);
    }

    @NotNull
    public ClearOptions getClearOptions() {
        if (mClearOptions == null) mClearOptions = new ClearOptions();
        return mClearOptions;
    }

    @NotNull
    public Engine getEngine() { return mEngine; }

    public void setPresentationTime(long monotonicClockNanos) { nSetPresentationTime(getNativeObject(), monotonicClockNanos); }

    public void setVsyncTime(long steadyClockTimeNano) { nSetVsyncTime(getNativeObject(), steadyClockTimeNano); }

    public void skipFrame(long vsyncSteadyClockTimeNano) { nSkipFrame(getNativeObject(), vsyncSteadyClockTimeNano); }

    public boolean shouldRenderFrame() { return nShouldRenderFrame(getNativeObject()); }

    public boolean beginFrame(@NotNull SwapChain swapChain, long frameTimeNanos) {
        return nBeginFrame(getNativeObject(), swapChain.getNativeObject(), frameTimeNanos);
    }

    public void endFrame() { nEndFrame(getNativeObject()); }

    public void render(@NotNull View view) { nRender(getNativeObject(), view.getNativeObject()); }

    public void renderStandaloneView(@NotNull View view) { nRenderStandaloneView(getNativeObject(), view.getNativeObject()); }

    public void copyFrame(@NotNull SwapChain dstSwapChain, @NotNull Viewport dstViewport, @NotNull Viewport srcViewport, int flags) {
        nCopyFrame(getNativeObject(), dstSwapChain.getNativeObject(), dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height, srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height, flags);
    }

    @Deprecated
    public void mirrorFrame(@NotNull SwapChain dstSwapChain, @NotNull Viewport dstViewport, @NotNull Viewport srcViewport, int flags) {
        copyFrame(dstSwapChain, dstViewport, srcViewport, flags);
    }

    public void readPixels(int xoffset, int yoffset, int width, int height, @NotNull Texture.PixelBufferDescriptor buffer) {
        if (buffer.storage.isReadOnly()) throw new ReadOnlyBufferException();
        int result = nReadPixels(getNativeObject(), mEngine.getNativeObject(), xoffset, yoffset, width, height, buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(), buffer.handler, buffer.callback);
        if (result < 0) throw new BufferOverflowException();
    }

    public void readPixels(@NotNull RenderTarget renderTarget, int xoffset, int yoffset, int width, int height, @NotNull Texture.PixelBufferDescriptor buffer) {
        if (buffer.storage.isReadOnly()) throw new ReadOnlyBufferException();
        int result = nReadPixelsEx(getNativeObject(), mEngine.getNativeObject(), renderTarget.getNativeObject(), xoffset, yoffset, width, height, buffer.storage, buffer.storage.remaining(), buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment, buffer.stride, buffer.format.ordinal(), buffer.handler, buffer.callback);
        if (result < 0) throw new BufferOverflowException();
    }

    public double getUserTime() { return nGetUserTime(getNativeObject()); }

    public void resetUserTime() { nResetUserTime(getNativeObject()); }

    public void skipNextFrames(int frameCount) { nSkipNextFrames(getNativeObject(), frameCount); }

    public int getFrameToSkipCount() { return nGetFrameToSkipCount(getNativeObject()); }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed Renderer");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native void nSetPresentationTime(long nativeObject, long monotonicClockNanos);
    private static native void nSetVsyncTime(long nativeObject, long steadyClockTimeNano);
    private static native void nSkipFrame(long nativeObject, long vsyncSteadyClockTimeNano);
    private static native boolean nShouldRenderFrame(long nativeObject);
    private static native boolean nBeginFrame(long nativeRenderer, long nativeSwapChain, long frameTimeNanos);
    private static native void nEndFrame(long nativeRenderer);
    private static native void nRender(long nativeRenderer, long nativeView);
    private static native void nRenderStandaloneView(long nativeRenderer, long nativeView);
    private static native void nCopyFrame(long nativeRenderer, long nativeDstSwapChain, int dstLeft, int dstBottom, int dstWidth, int dstHeight, int srcLeft, int srcBottom, int srcWidth, int srcHeight, int flags);
    private static native int nReadPixels(long nativeRenderer, long nativeEngine, int xoffset, int yoffset, int width, int height, Buffer storage, int remaining, int left, int top, int type, int alignment, int stride, int format, Object handler, Runnable callback);
    private static native int nReadPixelsEx(long nativeRenderer, long nativeEngine, long nativeRenderTarget, int xoffset, int yoffset, int width, int height, Buffer storage, int remaining, int left, int top, int type, int alignment, int stride, int format, Object handler, Runnable callback);
    private static native double nGetUserTime(long nativeRenderer);
    private static native void nResetUserTime(long nativeRenderer);
    private static native void nSetDisplayInfo(long nativeRenderer, float refreshRate);
    private static native void nSetFrameRateOptions(long nativeRenderer, float interval, float headRoomRatio, float scaleRate, int history);
    private static native void nSetClearOptions(long nativeRenderer, float r, float g, float b, float a, boolean clear, boolean discard);
    private static native void nSkipNextFrames(long nativeObject, int frameCount);
    private static native int nGetFrameToSkipCount(long nativeObject);
}
