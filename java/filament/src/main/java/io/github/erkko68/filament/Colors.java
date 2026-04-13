package io.github.erkko68.filament;

public class Colors {
    private Colors() {}

    public enum RgbType {
        SRGB,
        LINEAR
    }

    public enum RgbaType {
        SRGB,
        LINEAR,
        PREMULTIPLIED_SRGB,
        PREMULTIPLIED_LINEAR
    }

    public enum Conversion {
        ACCURATE,
        FAST
    }

    public static float[] toLinear(RgbType type, float r, float g, float b) {
        return toLinear(type, new float[] { r, g, b });
    }

    public static float[] toLinear(RgbType type, float[] rgb) {
        if (type == RgbType.LINEAR) return rgb;
        return toLinear(Conversion.ACCURATE, rgb);
    }

    public static float[] toLinear(Conversion conversion, float[] rgb) {
        switch (conversion) {
            case ACCURATE:
                for (int i = 0; i < 3; i++) {
                    rgb[i] = (rgb[i] <= 0.04045f) ?
                        rgb[i] / 12.92f : (float) Math.pow((rgb[i] + 0.055f) / 1.055f, 2.4f);
                }
                break;
            case FAST:
                for (int i = 0; i < 3; i++) {
                    rgb[i] = (float) Math.pow(rgb[i], 2.2f);
                }
                break;
        }
        return rgb;
    }

    public static float[] cct(float temperature) {
        float[] color = new float[3];
        nCct(temperature, color);
        return color;
    }

    public static float[] illuminantD(float temperature) {
        float[] color = new float[3];
        nIlluminantD(temperature, color);
        return color;
    }

    private static native void nCct(float temperature, float[] color);
    private static native void nIlluminantD(float temperature, float[] color);
}
