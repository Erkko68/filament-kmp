package io.github.erkko68.filament;

public class TextureSampler {
    public enum MinFilter {
        NEAREST,
        LINEAR,
        NEAREST_MIPMAP_NEAREST,
        LINEAR_MIPMAP_NEAREST,
        NEAREST_MIPMAP_LINEAR,
        LINEAR_MIPMAP_LINEAR
    }

    public enum MagFilter {
        NEAREST,
        LINEAR
    }

    public enum WrapMode {
        CLAMP_TO_EDGE,
        REPEAT,
        MIRRORED_REPEAT
    }

    public enum CompareMode {
        NONE,
        COMPARE_TO_TEXTURE
    }

    public enum CompareFunc {
        LE,
        GE,
        L,
        G,
        E,
        NE,
        A,
        N
    }

    private int mSampler;

    public TextureSampler() {
        mSampler = nCreateSampler(
            MinFilter.LINEAR_MIPMAP_LINEAR.ordinal(),
            MagFilter.LINEAR.ordinal(),
            WrapMode.REPEAT.ordinal(),
            WrapMode.REPEAT.ordinal(),
            WrapMode.REPEAT.ordinal()
        );
    }

    public TextureSampler(MinFilter min, MagFilter mag, WrapMode s, WrapMode t, WrapMode r) {
        mSampler = nCreateSampler(min.ordinal(), mag.ordinal(), s.ordinal(), t.ordinal(), r.ordinal());
    }

    public int getSampler() {
        return mSampler;
    }

    private static native int nCreateSampler(int min, int mag, int s, int t, int r);
}
