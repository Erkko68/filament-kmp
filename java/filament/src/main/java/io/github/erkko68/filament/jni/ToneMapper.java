package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class ToneMapper {
    private long mNativeObject;
    private final Cleaner.Cleanable mCleanable;

    private ToneMapper(long nativeObject) {
        mNativeObject = nativeObject;
        mCleanable = NativeRegistry.registerForCleanup(this, new ToneMapperCleanup(mNativeObject));
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed ToneMapper");
        }
        return mNativeObject;
    }

    public static class Linear extends ToneMapper {
        public Linear() { super(nCreateLinearToneMapper()); }
    }

    public static class ACES extends ToneMapper {
        public ACES() { super(nCreateACESToneMapper()); }
    }

    public static class ACESLegacy extends ToneMapper {
        public ACESLegacy() { super(nCreateACESLegacyToneMapper()); }
    }

    public static class Filmic extends ToneMapper {
        public Filmic() { super(nCreateFilmicToneMapper()); }
    }

    public static class PBRNeutral extends ToneMapper {
        public PBRNeutral() { super(nCreatePBRNeutralToneMapper()); }
    }

    public static class GT7 extends ToneMapper {
        public GT7() { super(nCreateGT7ToneMapper()); }
    }

    public static class Agx extends ToneMapper {
        public enum Look {
            NONE,
            PUNCHY,
            GOLDEN
        }

        public Agx() { this(Look.NONE); }
        public Agx(Look look) { super(nCreateAgxToneMapper(look.ordinal())); }
    }

    public static class Generic extends ToneMapper {
        public Generic() { this(1.55f, 0.18f, 0.215f, 10.0f); }
        public Generic(float contrast, float midGrayIn, float midGrayOut, float hdrMax) {
            super(nCreateGenericToneMapper(contrast, midGrayIn, midGrayOut, hdrMax));
        }

        public float getContrast() { return nGenericGetContrast(getNativeObject()); }
        public void setContrast(float contrast) { nGenericSetContrast(getNativeObject(), contrast); }
        public float getMidGrayIn() { return nGenericGetMidGrayIn(getNativeObject()); }
        public void setMidGrayIn(float midGrayIn) { nGenericSetMidGrayIn(getNativeObject(), midGrayIn); }
        public float getMidGrayOut() { return nGenericGetMidGrayOut(getNativeObject()); }
        public void setMidGrayOut(float midGrayOut) { nGenericSetMidGrayOut(getNativeObject(), midGrayOut); }
        public float getHdrMax() { return nGenericGetHdrMax(getNativeObject()); }
        public void setHdrMax(float hdrMax) { nGenericSetHdrMax(getNativeObject(), hdrMax); }
    }

    public static class DisplayRange extends ToneMapper {
        public DisplayRange() { super(nCreateDisplayRangeToneMapper()); }
    }

    private static class ToneMapperCleanup implements Runnable {
        private final long mNativeObject;
        ToneMapperCleanup(long nativeObject) { mNativeObject = nativeObject; }
        @Override public void run() { nDestroyToneMapper(mNativeObject); }
    }

    private static native void nDestroyToneMapper(long nativeObject);
    private static native long nCreateLinearToneMapper();
    private static native long nCreateACESToneMapper();
    private static native long nCreateACESLegacyToneMapper();
    private static native long nCreateFilmicToneMapper();
    private static native long nCreatePBRNeutralToneMapper();
    private static native long nCreateGT7ToneMapper();
    private static native long nCreateAgxToneMapper(int look);
    private static native long nCreateGenericToneMapper(float contrast, float midGrayIn, float midGrayOut, float hdrMax);
    private static native long nCreateDisplayRangeToneMapper();

    private static native float nGenericGetContrast(long nativeObject);
    private static native float nGenericGetMidGrayIn(long nativeObject);
    private static native float nGenericGetMidGrayOut(long nativeObject);
    private static native float nGenericGetHdrMax(long nativeObject);
    private static native void nGenericSetContrast(long nativeObject, float contrast);
    private static native void nGenericSetMidGrayIn(long nativeObject, float midGrayIn);
    private static native void nGenericSetMidGrayOut(long nativeObject, float midGrayOut);
    private static native void nGenericSetHdrMax(long nativeObject, float hdrMax);
}
