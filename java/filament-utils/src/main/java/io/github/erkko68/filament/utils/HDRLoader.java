/*
 * Copyright (C) 2021 The Android Open Source Project
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

package io.github.erkko68.filament.utils;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for decoding an HDR file and producing a Filament texture.
 */
public class HDRLoader {
    static {
        Utils.init();
    }
    /**
     * Consumes the content of an HDR file and produces a {@link Texture} object.
     *
     * @param engine Gets passed to the builder.
     * @param buffer The content of the HDR File as a byte array.
     * @param internalFormat The desired internal format of the texture.
     * @return The resulting Filament texture, or null on failure.
     */
    @Nullable
    public static Texture createTexture(@NotNull Engine engine, @NotNull byte[] buffer, @NotNull Texture.InternalFormat internalFormat) {
        long nativeEngine = engine.getNativeObject();
        long nativeTexture = nCreateHDRTexture(nativeEngine, buffer, internalFormat.ordinal());
        if (nativeTexture == 0L) {
            return null;
        }
        return new Texture(nativeTexture);
    }

    private static native long nCreateHDRTexture(long nativeEngine, byte[] buffer, int format);
}
