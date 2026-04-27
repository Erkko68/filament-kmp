package io.github.erkko68.filament.utils.jni;

import io.github.erkko68.filament.jni.Engine;
import io.github.erkko68.filament.jni.IndirectLight;
import io.github.erkko68.filament.jni.Skybox;
import io.github.erkko68.filament.jni.Texture;
import java.nio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities for consuming KTX1 files and producing Filament textures, IBLs, and sky boxes.
 */
public class KTX1Loader {

    static { Utils.init(); }

    public static class Options {
        public boolean srgb = false;
    }

    public static class IndirectLightBundle {
        public final @Nullable IndirectLight indirectLight;
        public final @Nullable Texture cubemap;
        public IndirectLightBundle(@Nullable IndirectLight il, @Nullable Texture c) {
            indirectLight = il; cubemap = c;
        }
    }

    public static class SkyboxBundle {
        public final @Nullable Skybox skybox;
        public final @Nullable Texture cubemap;
        public SkyboxBundle(@Nullable Skybox s, @Nullable Texture c) {
            skybox = s; cubemap = c;
        }
    }

    @Nullable
    public static Texture createTexture(@NotNull Engine engine, @NotNull Buffer buffer, @NotNull Options options) {
        long nativeTexture = nCreateKTXTexture(engine.getNativeObject(), buffer, buffer.remaining(), options.srgb);
        return nativeTexture != 0 ? new Texture(nativeTexture) : null;
    }

    @Nullable
    public static IndirectLightBundle createIndirectLight(@NotNull Engine engine, @NotNull Buffer buffer, @NotNull Options options) {
        float[] sh = getSphericalHarmonics(buffer);
        if (sh == null) return new IndirectLightBundle(null, null);
        Texture tex = createTexture(engine, buffer, options);
        if (tex == null) return new IndirectLightBundle(null, null);
        long nativeIL = nCreateIndirectLight(engine.getNativeObject(), tex.getNativeObject(), sh);
        return nativeIL != 0 ? new IndirectLightBundle(new IndirectLight(nativeIL), tex) : new IndirectLightBundle(null, tex);
    }

    @Nullable
    public static SkyboxBundle createSkybox(@NotNull Engine engine, @NotNull Buffer buffer, @NotNull Options options) {
        Texture tex = createTexture(engine, buffer, options);
        if (tex == null) return new SkyboxBundle(null, null);
        long nativeSkybox = nCreateSkybox(engine.getNativeObject(), tex.getNativeObject());
        return nativeSkybox != 0 ? new SkyboxBundle(new Skybox(nativeSkybox), tex) : new SkyboxBundle(null, tex);
    }

    @Nullable
    public static float[] getSphericalHarmonics(@NotNull Buffer buffer) {
        float[] sh = new float[9 * 3];
        return nGetSphericalHarmonics(buffer, buffer.remaining(), sh) ? sh : null;
    }

    private static native long nCreateKTXTexture(long nativeEngine, Buffer buffer, int remaining, boolean srgb);
    private static native long nCreateIndirectLight(long nativeEngine, long nativeTexture, float[] sh);
    private static native long nCreateSkybox(long nativeEngine, long nativeTexture);
    private static native boolean nGetSphericalHarmonics(Buffer buffer, int remaining, float[] outSh);
}
