package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class ColorGrading {
    private long mNativeObject;

    private ColorGrading(long nativeColorGrading) {
        mNativeObject = nativeColorGrading;
    }

    public enum QualityLevel {
        LOW,
        MEDIUM,
        HIGH,
        ULTRA
    }

    public enum LutFormat {
        INTEGER,
        FLOAT
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder quality(QualityLevel qualityLevel) {
            nBuilderQuality(mNativeBuilder, qualityLevel.ordinal());
            return this;
        }

        public Builder format(LutFormat format) {
            nBuilderFormat(mNativeBuilder, format.ordinal());
            return this;
        }

        public Builder dimensions(int dim) {
            nBuilderDimensions(mNativeBuilder, dim);
            return this;
        }

        public Builder toneMapper(ToneMapper toneMapper) {
            nBuilderToneMapper(mNativeBuilder, toneMapper.getNativeObject());
            return this;
        }

        public Builder luminanceScaling(boolean luminanceScaling) {
            nBuilderLuminanceScaling(mNativeBuilder, luminanceScaling);
            return this;
        }

        public Builder gamutMapping(boolean gamutMapping) {
            nBuilderGamutMapping(mNativeBuilder, gamutMapping);
            return this;
        }

        public Builder exposure(float exposure) {
            nBuilderExposure(mNativeBuilder, exposure);
            return this;
        }

        public Builder nightAdaptation(float adaptation) {
            nBuilderNightAdaptation(mNativeBuilder, adaptation);
            return this;
        }

        public Builder whiteBalance(float temperature, float tint) {
            nBuilderWhiteBalance(mNativeBuilder, temperature, tint);
            return this;
        }

        public Builder channelMixer(float[] outRed, float[] outGreen, float[] outBlue) {
            if (outRed.length < 3 || outGreen.length < 3 || outBlue.length < 3) throw new IllegalArgumentException("Mixers must be at least 3 floats");
            nBuilderChannelMixer(mNativeBuilder, outRed, outGreen, outBlue);
            return this;
        }

        public Builder shadowsMidtonesHighlights(float[] shadows, float[] midtones, float[] highlights, float[] ranges) {
            if (shadows.length < 4 || midtones.length < 4 || highlights.length < 4 || ranges.length < 4) throw new IllegalArgumentException("SMH parameters must be at least 4 floats");
            nBuilderShadowsMidtonesHighlights(mNativeBuilder, shadows, midtones, highlights, ranges);
            return this;
        }

        public Builder slopeOffsetPower(float[] slope, float[] offset, float[] power) {
            if (slope.length < 3 || offset.length < 3 || power.length < 3) throw new IllegalArgumentException("SOP parameters must be at least 3 floats");
            nBuilderSlopeOffsetPower(mNativeBuilder, slope, offset, power);
            return this;
        }

        public Builder contrast(float contrast) {
            nBuilderContrast(mNativeBuilder, contrast);
            return this;
        }

        public Builder vibrance(float vibrance) {
            nBuilderVibrance(mNativeBuilder, vibrance);
            return this;
        }

        public Builder saturation(float saturation) {
            nBuilderSaturation(mNativeBuilder, saturation);
            return this;
        }

        public Builder curves(float[] shadowGamma, float[] midPoint, float[] highlightScale) {
            if (shadowGamma.length < 3 || midPoint.length < 3 || highlightScale.length < 3) throw new IllegalArgumentException("Curve parameters must be at least 3 floats");
            nBuilderCurves(mNativeBuilder, shadowGamma, midPoint, highlightScale);
            return this;
        }

        public ColorGrading build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create ColorGrading");
            return new ColorGrading(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed ColorGrading");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);

    private static native void nBuilderQuality(long nativeBuilder, int quality);
    private static native void nBuilderFormat(long nativeBuilder, int format);
    private static native void nBuilderDimensions(long nativeBuilder, int dim);
    private static native void nBuilderToneMapper(long nativeBuilder, long toneMapper);
    private static native void nBuilderLuminanceScaling(long nativeBuilder, boolean luminanceScaling);
    private static native void nBuilderGamutMapping(long nativeBuilder, boolean gamutMapping);
    private static native void nBuilderExposure(long nativeBuilder, float exposure);
    private static native void nBuilderNightAdaptation(long nativeBuilder, float adaptation);
    private static native void nBuilderWhiteBalance(long nativeBuilder, float temperature, float tint);
    private static native void nBuilderChannelMixer(long nativeBuilder, float[] outRed, float[] outGreen, float[] outBlue);
    private static native void nBuilderShadowsMidtonesHighlights(long nativeBuilder, float[] shadows, float[] midtones, float[] highlights, float[] ranges);
    private static native void nBuilderSlopeOffsetPower(long nativeBuilder, float[] slope, float[] offset, float[] power);
    private static native void nBuilderContrast(long nativeBuilder, float contrast);
    private static native void nBuilderVibrance(long nativeBuilder, float vibrance);
    private static native void nBuilderSaturation(long nativeBuilder, float saturation);
    private static native void nBuilderCurves(long nativeBuilder, float[] gamma, float[] midPoint, float[] scale);

    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);
}
