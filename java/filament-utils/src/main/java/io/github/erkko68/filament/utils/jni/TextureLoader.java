package io.github.erkko68.filament.utils.jni;

import io.github.erkko68.filament.jni.Engine;
import io.github.erkko68.filament.jni.Texture;
import java.nio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * High-level utility for loading textures from common image formats (PNG, JPG,
 * etc.).
 */
public class TextureLoader {
    public enum TextureType {
        COLOR, NORMAL, DATA
    }

    public static long loadTexture(long nativeEngine, @NotNull Buffer buffer, int typeOrdinal) {
        return nLoadTexture(nativeEngine, buffer, buffer.remaining(), typeOrdinal);
    }

    private static native long nLoadTexture(long nativeEngine, Buffer buffer, int remaining, int type);
}
