package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class RenderableManager {
    private long mNativeObject;

    RenderableManager(long nativeRenderableManager) {
        mNativeObject = nativeRenderableManager;
    }

    public enum PrimitiveType {
        POINTS,
        LINES,
        TRIANGLES,
        NONE
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder(int count) {
            mNativeBuilder = nCreateBuilder(count);
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder geometry(int index, PrimitiveType type, VertexBuffer vb, IndexBuffer ib) {
            nBuilderGeometry(mNativeBuilder, index, type.ordinal(), vb.getNativeObject(), ib.getNativeObject());
            return this;
        }

        public Builder boundingBox(Box box) {
            nBuilderBoundingBox(mNativeBuilder, box.center[0], box.center[1], box.center[2], box.halfExtent[0], box.halfExtent[1], box.halfExtent[2]);
            return this;
        }

        public Builder material(int index, MaterialInstance materialInstance) {
            nBuilderMaterial(mNativeBuilder, index, materialInstance.getNativeObject());
            return this;
        }

        public Builder castShadows(boolean enabled) {
            nBuilderCastShadows(mNativeBuilder, enabled);
            return this;
        }

        public Builder receiveShadows(boolean enabled) {
            nBuilderReceiveShadows(mNativeBuilder, enabled);
            return this;
        }

        public Builder screenSpaceContactShadows(boolean enabled) {
            nBuilderScreenSpaceContactShadows(mNativeBuilder, enabled);
            return this;
        }

        public Builder culling(boolean enabled) {
            nBuilderCulling(mNativeBuilder, enabled);
            return this;
        }

        public Builder priority(int priority) {
            nBuilderPriority(mNativeBuilder, priority);
            return this;
        }

        public Builder channel(int channel) {
            nBuilderChannel(mNativeBuilder, channel);
            return this;
        }

        public Builder layerMask(int select, int values) {
            nBuilderLayerMask(mNativeBuilder, select, values);
            return this;
        }

        public void build(Engine engine, int entity) {
            nBuilderBuild(mNativeBuilder, engine.getNativeObject(), entity);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public boolean hasComponent(int entity) {
        return nHasComponent(getNativeObject(), entity);
    }

    public void setMorphTargetBuffer(int instance, MorphTargetBuffer buffer) {
        nSetMorphTargetBuffer(getNativeObject(), instance, buffer.getNativeObject());
    }

    public void setSkinningBuffer(int instance, SkinningBuffer buffer, int count, int offset) {
        nSetSkinningBuffer(getNativeObject(), instance, buffer.getNativeObject(), count, offset);
    }

    public void setCastShadows(int instance, boolean enabled) {
        nSetCastShadows(getNativeObject(), instance, enabled);
    }

    public void setReceiveShadows(int instance, boolean enabled) {
        nSetReceiveShadows(getNativeObject(), instance, enabled);
    }

    public void setScreenSpaceContactShadows(int instance, boolean enabled) {
        nSetScreenSpaceContactShadows(getNativeObject(), instance, enabled);
    }

    public void setPriority(int instance, int priority) {
        nSetPriority(getNativeObject(), instance, priority);
    }

    public void setLayerMask(int instance, int select, int values) {
        nSetLayerMask(getNativeObject(), instance, select, values);
    }

    public void destroy(int entity) {
        nDestroy(getNativeObject(), entity);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed RenderableManager");
        }
        return mNativeObject;
    }

    private static native long nCreateBuilder(int count);
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderGeometry(long nativeBuilder, int index, int type, long nativeVb, long nativeIb);
    private static native void nBuilderBoundingBox(long nativeBuilder, float cx, float cy, float cz, float ex, float ey, float ez);
    private static native void nBuilderMaterial(long nativeBuilder, int index, long nativeMi);
    private static native void nBuilderCastShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderReceiveShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderScreenSpaceContactShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderCulling(long nativeBuilder, boolean enabled);
    private static native void nBuilderPriority(long nativeBuilder, int priority);
    private static native void nBuilderChannel(long nativeBuilder, int channel);
    private static native void nBuilderLayerMask(long nativeBuilder, int select, int values);
    private static native void nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);

    private static native boolean nHasComponent(long nativeRenderableManager, int entity);
    private static native void nSetLayerMask(long nativeManager, int instance, int select, int values);
    private static native void nSetCastShadows(long nativeManager, int instance, boolean enabled);
    private static native void nSetReceiveShadows(long nativeManager, int instance, boolean enabled);
    private static native void nSetScreenSpaceContactShadows(long nativeManager, int instance, boolean enabled);
    private static native void nSetPriority(long nativeManager, int instance, int priority);
    private static native void nSetMorphTargetBuffer(long nativeManager, int instance, long nativeBuffer);
    private static native void nSetSkinningBuffer(long nativeManager, int instance, long nativeBuffer, int count, int offset);
    private static native void nDestroy(long nativeRenderableManager, int entity);
}
