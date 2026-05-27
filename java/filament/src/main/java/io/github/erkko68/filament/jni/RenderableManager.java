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

import io.github.erkko68.filament.jni.proguard.UsedByNative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.github.erkko68.filament.jni.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.Buffer;
import java.nio.BufferOverflowException;

/**
 * Factory and manager for renderables, which are entities that can be drawn.
 */
public class RenderableManager {
    private long mNativeObject;

    RenderableManager(long nativeRenderableManager) {
        mNativeObject = nativeRenderableManager;
    }

    public boolean hasComponent(@io.github.erkko68.filament.jni.Entity int entity) { return nHasComponent(mNativeObject, entity); }

    @io.github.erkko68.filament.jni.proguard.EntityInstance
    public int getInstance(@io.github.erkko68.filament.jni.Entity int entity) { return nGetInstance(mNativeObject, entity); }

    public void destroy(@io.github.erkko68.filament.jni.Entity int entity) { nDestroy(mNativeObject, entity); }

    public enum PrimitiveType {
        POINTS(0), LINES(1), LINE_STRIP(3), TRIANGLES(4), TRIANGLE_STRIP(5);
        private final int mType;
        PrimitiveType(int value) { mType = value; }
        public int getValue() { return mType; }
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder(int count) {
            mNativeBuilder = nCreateBuilder(count);
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        @NotNull public Builder geometry(int index, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, @NotNull IndexBuffer indices, int offset, int minIndex, int maxIndex, int count) {
            nBuilderGeometry(mNativeBuilder, index, type.getValue(), vertices.getNativeObject(), indices.getNativeObject(), offset, minIndex, maxIndex, count);
            return this;
        }

        @NotNull public Builder geometry(int index, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, @NotNull IndexBuffer indices, int offset, int count) {
            nBuilderGeometryShort(mNativeBuilder, index, type.getValue(), vertices.getNativeObject(), indices.getNativeObject(), offset, count);
            return this;
        }

        @NotNull public Builder geometry(int index, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, @NotNull IndexBuffer indices) {
            nBuilderGeometryNone(mNativeBuilder, index, type.getValue(), vertices.getNativeObject(), indices.getNativeObject());
            return this;
        }

        @NotNull public Builder geometry(int index, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, int offset, int count) {
            nBuilderGeometryNonIndexed(mNativeBuilder, index, type.getValue(), vertices.getNativeObject(), offset, count);
            return this;
        }

        @NotNull public Builder geometry(int index, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices) {
            nBuilderGeometryNonIndexedNone(mNativeBuilder, index, type.getValue(), vertices.getNativeObject());
            return this;
        }

        public enum GeometryType { DYNAMIC, STATIC_BOUNDS, STATIC }

        @NotNull public Builder geometryType(@NotNull GeometryType type) { nBuilderGeometryType(mNativeBuilder, type.ordinal()); return this; }

        @NotNull public Builder material(int index, @NotNull MaterialInstance material) { nBuilderMaterial(mNativeBuilder, index, material.getNativeObject()); return this; }

        @NotNull public Builder blendOrder(int index, int blendOrder) { nBuilderBlendOrder(mNativeBuilder, index, blendOrder); return this; }

        @NotNull public Builder globalBlendOrderEnabled(int index, boolean enabled) { nBuilderGlobalBlendOrderEnabled(mNativeBuilder, index, enabled); return this; }

        @NotNull public Builder boundingBox(@NotNull Box aabb) {
            float[] center = aabb.getCenter();
            float[] halfExtent = aabb.getHalfExtent();
            nBuilderBoundingBox(mNativeBuilder, center[0], center[1], center[2], halfExtent[0], halfExtent[1], halfExtent[2]);
            return this;
        }

        @NotNull public Builder layerMask(int select, int value) { nBuilderLayerMask(mNativeBuilder, select & 0xFF, value & 0xFF); return this; }

        @NotNull public Builder priority(int priority) { nBuilderPriority(mNativeBuilder, priority); return this; }

        @NotNull public Builder channel(int channel) { nBuilderChannel(mNativeBuilder, channel); return this; }

        @NotNull public Builder culling(boolean enabled) { nBuilderCulling(mNativeBuilder, enabled); return this; }

        @NotNull public Builder lightChannel(int channel, boolean enable) { nBuilderLightChannel(mNativeBuilder, channel, enable); return this; }

        @NotNull public Builder instances(int instanceCount) { nBuilderInstances(mNativeBuilder, instanceCount); return this; }

        @NotNull public Builder castShadows(boolean enabled) { nBuilderCastShadows(mNativeBuilder, enabled); return this; }

        @NotNull public Builder receiveShadows(boolean enabled) { nBuilderReceiveShadows(mNativeBuilder, enabled); return this; }

        @NotNull public Builder screenSpaceContactShadows(boolean enabled) { nBuilderScreenSpaceContactShadows(mNativeBuilder, enabled); return this; }

        @NotNull public Builder enableSkinningBuffers(boolean enabled) { nBuilderEnableSkinningBuffers(mNativeBuilder, enabled); return this; }

        @NotNull public Builder fog(boolean enabled) { nBuilderFog(mNativeBuilder, enabled); return this; }

        @NotNull public Builder skinning(@Nullable SkinningBuffer skinningBuffer, int boneCount, int offset) {
            nBuilderSkinningBuffer(mNativeBuilder, skinningBuffer != null ? skinningBuffer.getNativeObject() : 0, boneCount, offset);
            return this;
        }

        @NotNull public Builder skinning(int boneCount) { nBuilderSkinning(mNativeBuilder, boneCount); return this; }

        @NotNull public Builder skinning(int boneCount, @NotNull Buffer bones) {
            int result = nBuilderSkinningBones(mNativeBuilder, boneCount, bones, bones.remaining());
            if (result < 0) throw new BufferOverflowException();
            return this;
        }

        @NotNull public Builder morphing(int targetCount) { nBuilderMorphing(mNativeBuilder, targetCount); return this; }

        @NotNull public Builder morphing(@NotNull MorphTargetBuffer morphTargetBuffer) {
            nBuilderMorphingStandard(mNativeBuilder, morphTargetBuffer.getNativeObject());
            return this;
        }

        @NotNull public Builder morphing(int level, int primitiveIndex, int offset) {
            nBuilderSetMorphTargetBufferOffsetAtAt(mNativeBuilder, level, primitiveIndex, offset);
            return this;
        }

        public void build(@NotNull Engine engine, @io.github.erkko68.filament.jni.Entity int entity) {
            if (!nBuilderBuild(mNativeBuilder, engine.getNativeObject(), entity)) {
                throw new IllegalStateException("Couldn't create Renderable component for entity " + entity);
            }
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public void setSkinningBuffer(int i, @NotNull SkinningBuffer skinningBuffer, int count, int offset) { nSetSkinningBuffer(mNativeObject, i, skinningBuffer.getNativeObject(), count, offset); }

    public void setBonesAsMatrices(int i, @NotNull Buffer matrices, int boneCount, int offset) {
        int result = nSetBonesAsMatrices(mNativeObject, i, matrices, matrices.remaining(), boneCount, offset);
        if (result < 0) throw new BufferOverflowException();
    }

    public void setBonesAsQuaternions(int i, @NotNull Buffer quaternions, int boneCount, int offset) {
        int result = nSetBonesAsQuaternions(mNativeObject, i, quaternions, quaternions.remaining(), boneCount, offset);
        if (result < 0) throw new BufferOverflowException();
    }

    public void setMorphWeights(int i, @NotNull float[] weights, int offset) { nSetMorphWeights(mNativeObject, i, weights, offset); }

    public void setMorphTargetBufferOffsetAt(int i, int level, int primitiveIndex, int offset) { nSetMorphTargetBufferOffsetAt(mNativeObject, i, level, primitiveIndex, offset); }

    public int getMorphTargetCount(int i) { return nGetMorphTargetCount(mNativeObject, i); }

    public void setAxisAlignedBoundingBox(int i, @NotNull Box aabb) {
        float[] center = aabb.getCenter();
        float[] halfExtent = aabb.getHalfExtent();
        nSetAxisAlignedBoundingBox(mNativeObject, i, center[0], center[1], center[2], halfExtent[0], halfExtent[1], halfExtent[2]);
    }

    public void setLayerMask(int i, int select, int value) { nSetLayerMask(mNativeObject, i, select & 0xFF, value & 0xFF); }
    public int getLayerMask(int i) { return nGetLayerMask(mNativeObject, i); }

    public void setPriority(int i, int priority) { nSetPriority(mNativeObject, i, priority); }
    public int getPriority(int i) { return nGetPriority(mNativeObject, i); }

    public void setChannel(int i, int channel) { nSetChannel(mNativeObject, i, channel); }
    public int getChannel(int i) { return nGetChannel(mNativeObject, i); }

    public void setCulling(int i, boolean enabled) { nSetCulling(mNativeObject, i, enabled); }
    public boolean isCullingEnabled(int i) { return nIsCullingEnabled(mNativeObject, i); }

    public void setFogEnabled(int i, boolean enabled) { nSetFogEnabled(mNativeObject, i, enabled); }
    public boolean getFogEnabled(int i) { return nGetFogEnabled(mNativeObject, i); }

    public void setLightChannel(int i, int channel, boolean enable) { nSetLightChannel(mNativeObject, i, channel, enable); }
    public boolean getLightChannel(int i, int channel) { return nGetLightChannel(mNativeObject, i, channel); }

    public void setCastShadows(int i, boolean enabled) { nSetCastShadows(mNativeObject, i, enabled); }
    public boolean isShadowCastingEnabled(int i) { return nIsShadowCastingEnabled(mNativeObject, i); }

    public void setReceiveShadows(int i, boolean enabled) { nSetReceiveShadows(mNativeObject, i, enabled); }
    public boolean isShadowReceivingEnabled(int i) { return nIsShadowReceivingEnabled(mNativeObject, i); }

    public void setScreenSpaceContactShadows(int i, boolean enabled) { nSetScreenSpaceContactShadows(mNativeObject, i, enabled); }


    public void setMaterialInstanceAt(int i, int primitiveIndex, @NotNull MaterialInstance materialInstance) { nSetMaterialInstanceAt(mNativeObject, i, primitiveIndex, materialInstance.getNativeObject()); }

    @NotNull
    public MaterialInstance getMaterialInstanceAt(int i, int primitiveIndex) {
        long nativeMi = nGetMaterialInstanceAt(mNativeObject, i, primitiveIndex);
        return new MaterialInstance(nativeMi);
    }

    public void setBlendOrderAt(int i, int primitiveIndex, int blendOrder) { nSetBlendOrderAt(mNativeObject, i, primitiveIndex, blendOrder); }
    public int getBlendOrderAt(int i, int primitiveIndex) { return nGetBlendOrderAt(mNativeObject, i, primitiveIndex); }

    public int getEnabledAttributesAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, int primitiveIndex) { return nGetEnabledAttributesAt(mNativeObject, i, primitiveIndex); }

    public int getPrimitiveCount(@io.github.erkko68.filament.jni.proguard.EntityInstance int i) { return nGetPrimitiveCount(mNativeObject, i); }

    @io.github.erkko68.filament.jni.proguard.EntityInstance
    public int getInstanceCount(@io.github.erkko68.filament.jni.proguard.EntityInstance int i) { return nGetInstanceCount(mNativeObject, i); }

    public void clearMaterialInstanceAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, int primitiveIndex) { nClearMaterialInstanceAt(mNativeObject, i, primitiveIndex); }

    public void setGeometryAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, int primitiveIndex, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, @NotNull IndexBuffer indices, int offset, int count) {
        nSetGeometryAt(mNativeObject, i, primitiveIndex, type.getValue(), vertices.getNativeObject(), indices.getNativeObject(), offset, count);
    }

    public void setGeometryAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, int primitiveIndex, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, @NotNull IndexBuffer indices) {
        nSetGeometryAt(mNativeObject, i, primitiveIndex, type.getValue(), vertices.getNativeObject(), indices.getNativeObject(), 0, (int) indices.getIndexCount());
    }

    public void setGeometryAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, int primitiveIndex, @NotNull PrimitiveType type, @NotNull VertexBuffer vertices, int offset, int count) {
        nSetGeometryAtNonIndexed(mNativeObject, i, primitiveIndex, type.getValue(), vertices.getNativeObject(), offset, count);
    }

    public void setGlobalBlendOrderEnabledAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int instance, int primitiveIndex, boolean enabled) {
        nSetGlobalBlendOrderEnabledAt(mNativeObject, instance, primitiveIndex, enabled);
    }

    public boolean isGlobalBlendOrderEnabledAt(@io.github.erkko68.filament.jni.proguard.EntityInstance int instance, int primitiveIndex) {
        return nIsGlobalBlendOrderEnabledAt(mNativeObject, instance, primitiveIndex);
    }

    public boolean isShadowCaster(@io.github.erkko68.filament.jni.proguard.EntityInstance int i) { return nIsShadowCaster(mNativeObject, i); }

    public boolean isShadowReceiver(@io.github.erkko68.filament.jni.proguard.EntityInstance int i) { return nIsShadowReceiver(mNativeObject, i); }

    public boolean isScreenSpaceContactShadowsEnabled(@io.github.erkko68.filament.jni.proguard.EntityInstance int i) { return nIsScreenSpaceContactShadowsEnabled(mNativeObject, i); }

    @NotNull
    public Box getAxisAlignedBoundingBox(@io.github.erkko68.filament.jni.proguard.EntityInstance int i, @Nullable Box out) {
        if (out == null) out = new Box();
        nGetAxisAlignedBoundingBox(mNativeObject, i, out.getCenter(), out.getHalfExtent());
        return out;
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed RenderableManager");
        return mNativeObject;
    }

    private static native boolean nHasComponent(long nativeRenderableManager, int entity);
    private static native int nGetInstance(long nativeRenderableManager, int entity);
    private static native void nDestroy(long nativeRenderableManager, int entity);

    private static native long nCreateBuilder(int count);
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderGeometry(long nativeBuilder, int index, int type, long nativeVb, long nativeIb, int offset, int minIndex, int maxIndex, int count);
    private static native void nBuilderGeometryShort(long nativeBuilder, int index, int type, long nativeVb, long nativeIb, int offset, int count);
    private static native void nBuilderGeometryNone(long nativeBuilder, int index, int type, long nativeVb, long nativeIb);
    private static native void nBuilderGeometryNonIndexed(long nativeBuilder, int index, int type, long nativeVb, int offset, int count);
    private static native void nBuilderGeometryNonIndexedNone(long nativeBuilder, int index, int type, long nativeVb);
    private static native void nBuilderGeometryType(long nativeBuilder, int type);
    private static native void nBuilderMaterial(long nativeBuilder, int index, long nativeMi);
    private static native void nBuilderBlendOrder(long nativeBuilder, int index, int blendOrder);
    private static native void nBuilderGlobalBlendOrderEnabled(long nativeBuilder, int index, boolean enabled);
    private static native void nBuilderBoundingBox(long nativeBuilder, float cx, float cy, float cz, float ex, float ey, float ez);
    private static native void nBuilderLayerMask(long nativeBuilder, int select, int value);
    private static native void nBuilderPriority(long nativeBuilder, int priority);
    private static native void nBuilderChannel(long nativeBuilder, int channel);
    private static native void nBuilderCulling(long nativeBuilder, boolean enabled);
    private static native void nBuilderLightChannel(long nativeBuilder, int channel, boolean enable);
    private static native void nBuilderInstances(long nativeBuilder, int instanceCount);
    private static native void nBuilderCastShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderReceiveShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderScreenSpaceContactShadows(long nativeBuilder, boolean enabled);
    private static native void nBuilderEnableSkinningBuffers(long nativeBuilder, boolean enabled);
    private static native void nBuilderFog(long nativeBuilder, boolean enabled);
    private static native void nBuilderSkinningBuffer(long nativeBuilder, long nativeSkinningBuffer, int boneCount, int offset);
    private static native void nBuilderSkinning(long nativeBuilder, int boneCount);
    private static native int nBuilderSkinningBones(long nativeBuilder, int boneCount, Buffer bones, int remaining);
    private static native void nBuilderMorphing(long nativeBuilder, int targetCount);
    private static native void nBuilderMorphingStandard(long nativeBuilder, long nativeMorphTargetBuffer);
    private static native void nBuilderSetMorphTargetBufferOffsetAtAt(long nativeBuilder, int level, int primitiveIndex, int offset);
    private static native boolean nBuilderBuild(long nativeBuilder, long nativeEngine, int entity);

    private static native void nSetLayerMask(long nativeManager, int i, int select, int value);
    private static native int nGetLayerMask(long nativeManager, int i);
    private static native void nSetPriority(long nativeManager, int i, int priority);
    private static native int nGetPriority(long nativeManager, int i);
    private static native void nSetChannel(long nativeManager, int i, int channel);
    private static native int nGetChannel(long nativeManager, int i);
    private static native void nSetCulling(long nativeManager, int i, boolean enabled);
    private static native boolean nIsCullingEnabled(long nativeManager, int i);
    private static native void nSetFogEnabled(long nativeManager, int i, boolean enabled);
    private static native boolean nGetFogEnabled(long nativeManager, int i);
    private static native void nSetLightChannel(long nativeManager, int i, int channel, boolean enable);
    private static native boolean nGetLightChannel(long nativeManager, int i, int channel);
    private static native void nSetCastShadows(long nativeManager, int i, boolean enabled);
    private static native boolean nIsShadowCastingEnabled(long nativeManager, int i);
    private static native void nSetReceiveShadows(long nativeManager, int i, boolean enabled);
    private static native boolean nIsShadowReceivingEnabled(long nativeManager, int i);
    private static native void nSetScreenSpaceContactShadows(long nativeManager, int i, boolean enabled);
    private static native void nSetMaterialInstanceAt(long nativeManager, int i, int primitiveIndex, long nativeMi);
    private static native long nGetMaterialInstanceAt(long nativeManager, int i, int primitiveIndex);
    private static native void nSetBlendOrderAt(long nativeManager, int i, int primitiveIndex, int blendOrder);
    private static native int nGetBlendOrderAt(long nativeManager, int i, int primitiveIndex);
    private static native int nGetEnabledAttributesAt(long nativeManager, int i, int primitiveIndex);
    private static native int nGetPrimitiveCount(long nativeManager, int i);
    private static native int nGetInstanceCount(long nativeManager, int i);
    private static native void nClearMaterialInstanceAt(long nativeManager, int i, int primitiveIndex);
    private static native void nSetGeometryAt(long nativeManager, int i, int primitiveIndex, int primitiveType, long nativeVertexBuffer, long nativeIndexBuffer, int offset, int count);
    private static native void nSetGeometryAtNonIndexed(long nativeManager, int i, int primitiveIndex, int primitiveType, long nativeVertexBuffer, int offset, int count);
    private static native boolean nIsShadowCaster(long nativeManager, int i);
    private static native boolean nIsShadowReceiver(long nativeManager, int i);
    private static native boolean nIsScreenSpaceContactShadowsEnabled(long nativeManager, int i);
    private static native void nGetAxisAlignedBoundingBox(long nativeManager, int i, float[] center, float[] halfExtent);
    private static native void nSetGlobalBlendOrderEnabledAt(long nativeManager, int i, int primitiveIndex, boolean enabled);
    private static native boolean nIsGlobalBlendOrderEnabledAt(long nativeManager, int i, int primitiveIndex);
    private static native void nSetMorphWeights(long nativeManager, int i, float[] weights, int offset);
    private static native void nSetMorphTargetBufferOffsetAt(long nativeManager, int i, int level, int primitiveIndex, int offset);
    private static native int nGetMorphTargetCount(long nativeManager, int i);
    private static native void nSetAxisAlignedBoundingBox(long nativeManager, int i, float cx, float cy, float cz, float ex, float ey, float ez);
    private static native void nSetSkinningBuffer(long nativeManager, int i, long nativeBuffer, int count, int offset);
    private static native int nSetBonesAsMatrices(long nativeManager, int i, Buffer matrices, int remaining, int boneCount, int offset);
    private static native int nSetBonesAsQuaternions(long nativeManager, int i, Buffer quaternions, int remaining, int boneCount, int offset);
}
