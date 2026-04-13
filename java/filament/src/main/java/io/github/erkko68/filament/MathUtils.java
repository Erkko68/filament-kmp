package io.github.erkko68.filament;

public final class MathUtils {
    private MathUtils() {}

    public static void packTangentFrame(
            float tx, float ty, float tz,
            float bx, float by, float bz,
            float nx, float ny, float nz,
            float[] quaternion) {
        nPackTangentFrame(tx, ty, tz, bx, by, bz, nx, ny, nz, quaternion, 0);
    }

    public static void packTangentFrame(
            float tx, float ty, float tz,
            float bx, float by, float bz,
            float nx, float ny, float nz,
            float[] quaternion, int offset) {
        nPackTangentFrame(tx, ty, tz, bx, by, bz, nx, ny, nz, quaternion, offset);
    }

    private static native void nPackTangentFrame(
        float tx, float ty, float tz,
        float bx, float by, float bz,
        float nx, float ny, float nz,
        float[] quaternion, int offset);
}
