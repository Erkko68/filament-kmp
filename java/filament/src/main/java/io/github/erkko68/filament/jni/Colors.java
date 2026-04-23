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

import org.jetbrains.annotations.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Utilities to manipulate and convert colors.
 */
public class Colors {
    private Colors() {}

    @Retention(SOURCE)
    @Target({PARAMETER, METHOD, LOCAL_VARIABLE, FIELD})
    public @interface LinearColor {}

    public enum RgbType { SRGB, LINEAR }
    public enum RgbaType { SRGB, LINEAR, PREMULTIPLIED_SRGB, PREMULTIPLIED_LINEAR }
    public enum Conversion { ACCURATE, FAST }

    @NotNull
    @LinearColor
    public static float[] toLinear(@NotNull RgbType type, float r, float g, float b) {
        return toLinear(type, new float[] { r, g, b });
    }

    @NotNull
    @LinearColor
    public static float[] toLinear(@NotNull RgbType type, @NotNull float[] rgb) {
        return (type == RgbType.LINEAR) ? rgb : toLinear(Conversion.ACCURATE, rgb);
    }

    @NotNull
    @LinearColor
    public static float[] toLinear(@NotNull RgbaType type, float r, float g, float b, float a) {
        return toLinear(type, new float[] { r, g, b, a });
    }

    @NotNull
    @LinearColor
    public static float[] toLinear(@NotNull RgbaType type, @NotNull float[] rgba) {
        switch (type) {
            case SRGB:
                toLinear(Conversion.ACCURATE, rgba);
                float a = rgba[3];
                rgba[0] *= a; rgba[1] *= a; rgba[2] *= a;
                return rgba;
            case LINEAR:
                float aL = rgba[3];
                rgba[0] *= aL; rgba[1] *= aL; rgba[2] *= aL;
                return rgba;
            case PREMULTIPLIED_SRGB:
                return toLinear(Conversion.ACCURATE, rgba);
            case PREMULTIPLIED_LINEAR:
                return rgba;
        }
        return rgba;
    }

    @NotNull
    @LinearColor
    public static float[] toLinear(@NotNull Conversion conversion, @NotNull float[] rgb) {
        switch (conversion) {
            case ACCURATE:
                for (int i = 0; i < 3; i++) {
                    rgb[i] = (rgb[i] <= 0.04045f) ? rgb[i] / 12.92f : (float) Math.pow((rgb[i] + 0.055f) / 1.055f, 2.4f);
                }
                break;
            case FAST:
                for (int i = 0; i < 3; i++) {
                    rgb[i] = (float) Math.sqrt(rgb[i]);
                }
                break;
        }
        return rgb;
    }

    @NotNull
    @LinearColor
    public static float[] cct(float temperature) {
        float[] color = new float[3];
        nCct(temperature, color);
        return color;
    }

    @NotNull
    @LinearColor
    public static float[] illuminantD(float temperature) {
        float[] color = new float[3];
        nIlluminantD(temperature, color);
        return color;
    }

    private static native void nCct(float temperature, @NotNull float[] color);
    private static native void nIlluminantD(float temperature, @NotNull float[] color);
}
