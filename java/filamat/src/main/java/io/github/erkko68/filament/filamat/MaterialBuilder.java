package io.github.erkko68.filament.filamat;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MaterialBuilder {
    private final long mNativeBuilder;
    private final Cleaner.Cleanable mCleanable;

    static {
        System.loadLibrary("filamat-jni");
    }

    public enum Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    public enum BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    public enum MaterialDomain { SURFACE, POST_PROCESS }
    public enum TargetApi { OPENGL(1), VULKAN(2), METAL(4), WEBGPU(8), ALL(15);
        final int bit;
        TargetApi(int bit) { this.bit = bit; }
    }

    public static void init() { nMaterialBuilderInit(); }
    public static void shutdown() { nMaterialBuilderShutdown(); }

    public MaterialBuilder() {
        mNativeBuilder = nCreateMaterialBuilder();
        mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
    }

    public MaterialBuilder name(String name) { nMaterialBuilderName(mNativeBuilder, name); return this; }
    public MaterialBuilder shading(Shading shading) { nMaterialBuilderShading(mNativeBuilder, shading.ordinal()); return this; }
    public MaterialBuilder blending(BlendingMode mode) { nMaterialBuilderBlending(mNativeBuilder, mode.ordinal()); return this; }
    public MaterialBuilder materialDomain(MaterialDomain domain) { nMaterialBuilderMaterialDomain(mNativeBuilder, domain.ordinal()); return this; }
    public MaterialBuilder material(String code) { nMaterialBuilderMaterial(mNativeBuilder, code); return this; }
    public MaterialBuilder targetApi(TargetApi api) { nMaterialBuilderTargetApi(mNativeBuilder, api.bit); return this; }

    public MaterialPackage build() {
        long nativePackage = nBuilderBuild(mNativeBuilder);
        if (nativePackage == 0) return new MaterialPackage(null, false);
        
        byte[] data = nGetPackageBytes(nativePackage);
        boolean valid = nGetPackageIsValid(nativePackage);
        nDestroyPackage(nativePackage);
        
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(data);
        buffer.flip();
        
        return new MaterialPackage(buffer, valid);
    }

    private static class BuilderCleanup implements Runnable {
        private final long mNativeObject;
        BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
        @Override public void run() { nDestroyMaterialBuilder(mNativeObject); }
    }

    private static native void nMaterialBuilderInit();
    private static native void nMaterialBuilderShutdown();
    private static native long nCreateMaterialBuilder();
    private static native void nDestroyMaterialBuilder(long nativeBuilder);
    private static native long nBuilderBuild(long nativeBuilder);
    private static native byte[] nGetPackageBytes(long nativePackage);
    private static native boolean nGetPackageIsValid(long nativePackage);
    private static native void nDestroyPackage(long nativePackage);

    private static native void nMaterialBuilderName(long nativeBuilder, String name);
    private static native void nMaterialBuilderShading(long nativeBuilder, int shading);
    private static native void nMaterialBuilderBlending(long nativeBuilder, int mode);
    private static native void nMaterialBuilderMaterialDomain(long nativeBuilder, int domain);
    private static native void nMaterialBuilderMaterial(long nativeBuilder, String code);
    private static native void nMaterialBuilderTargetApi(long nativeBuilder, int api);
}
