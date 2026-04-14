package io.github.erkko68.filament.utils;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.Texture;
import java.nio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * High-level utility for loading textures from common image formats (PNG, JPG, etc.).
 */
public class TextureLoader {
    public enum TextureType { COLOR, NORMAL, DATA }

    @Nullable
    public static Texture loadTexture(@NotNull Engine engine, @NotNull Buffer buffer, @NotNull TextureType type) {
        long nativeTexture = nLoadTexture(engine.getNativeObject(), buffer, buffer.remaining(), type.ordinal());
        return nativeTexture != 0 ? new Texture(nativeTexture) : null;
    }

    private static native long nLoadTexture(long nativeEngine, Buffer buffer, int remaining, int type);
}
