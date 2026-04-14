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

package io.github.erkko68.filament;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransformManager {
    private long mNativeObject;

    TransformManager(long nativeTransformManager) {
        mNativeObject = nativeTransformManager;
    }

    public boolean hasComponent(int entity) {
        return nHasComponent(mNativeObject, entity);
    }

    public int getInstance(int entity) {
        return nGetInstance(mNativeObject, entity);
    }

    public void setAccurateTranslationsEnabled(boolean enable) {
        nSetAccurateTranslationsEnabled(mNativeObject, enable);
    }

    public boolean isAccurateTranslationsEnabled() {
        return nIsAccurateTranslationsEnabled(mNativeObject);
    }

    public int create(int entity) {
        return nCreate(mNativeObject, entity);
    }

    public int create(int entity, int parent, @Nullable float[] localTransform) {
        return nCreateArray(mNativeObject, entity, parent, localTransform);
    }

    public int create(int entity, int parent, @Nullable double[] localTransform) {
        return nCreateArrayFp64(mNativeObject, entity, parent, localTransform);
    }

    public void destroy(int entity) {
        nDestroy(mNativeObject, entity);
    }

    public void setParent(int i, int newParent) {
        nSetParent(mNativeObject, i, newParent);
    }

    public int getParent(int i) {
        return nGetParent(mNativeObject, i);
    }

    public int getChildCount(int i) {
        return nGetChildCount(mNativeObject, i);
    }

    @NotNull
    public int[] getChildren(int i, @Nullable int[] outEntities) {
        if (outEntities == null) {
            outEntities = new int[getChildCount(i)];
        }
        if (outEntities.length > 0) {
            nGetChildren(mNativeObject, i, outEntities, outEntities.length);
        }
        return outEntities;
    }

    public void setTransform(int i, @NotNull float[] localTransform) {
        if (localTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 floats");
        nSetTransform(mNativeObject, i, localTransform);
    }

    public void setTransform(int i, @NotNull double[] localTransform) {
        if (localTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 doubles");
        nSetTransformFp64(mNativeObject, i, localTransform);
    }

    @NotNull
    public float[] getTransform(int i, @Nullable float[] outLocalTransform) {
        if (outLocalTransform == null) outLocalTransform = new float[16];
        else if (outLocalTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 floats");
        nGetTransform(mNativeObject, i, outLocalTransform);
        return outLocalTransform;
    }

    @NotNull
    public double[] getTransform(int i, @Nullable double[] outLocalTransform) {
        if (outLocalTransform == null) outLocalTransform = new double[16];
        else if (outLocalTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 doubles");
        nGetTransformFp64(mNativeObject, i, outLocalTransform);
        return outLocalTransform;
    }

    @NotNull
    public float[] getWorldTransform(int i, @Nullable float[] outWorldTransform) {
        if (outWorldTransform == null) outWorldTransform = new float[16];
        else if (outWorldTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 floats");
        nGetWorldTransform(mNativeObject, i, outWorldTransform);
        return outWorldTransform;
    }

    @NotNull
    public double[] getWorldTransform(int i, @Nullable double[] outWorldTransform) {
        if (outWorldTransform == null) outWorldTransform = new double[16];
        else if (outWorldTransform.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 doubles");
        nGetWorldTransformFp64(mNativeObject, i, outWorldTransform);
        return outWorldTransform;
    }

    public void openLocalTransformTransaction() {
        nOpenLocalTransformTransaction(mNativeObject);
    }

    public void commitLocalTransformTransaction() {
        nCommitLocalTransformTransaction(mNativeObject);
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    private static native boolean nHasComponent(long nativeTransformManager, int entity);
    private static native int nGetInstance(long nativeTransformManager, int entity);
    private static native int nCreate(long nativeTransformManager, int entity);
    private static native int nCreateArray(long nativeTransformManager, int entity, int parent, float[] localTransform);
    private static native int nCreateArrayFp64(long nativeTransformManager, int entity, int parent, double[] localTransform);
    private static native void nDestroy(long nativeTransformManager, int entity);
    private static native void nSetParent(long nativeTransformManager, int i, int newParent);
    private static native int nGetParent(long nativeTransformManager, int i);
    private static native int nGetChildCount(long nativeTransformManager, int i);
    private static native void nGetChildren(long nativeTransformManager, int i, int[] outEntities, int count);
    private static native void nSetTransform(long nativeTransformManager, int i, float[] localTransform);
    private static native void nSetTransformFp64(long nativeTransformManager, int i, double[] localTransform);
    private static native void nGetTransform(long nativeTransformManager, int i, float[] outLocalTransform);
    private static native void nGetTransformFp64(long nativeTransformManager, int i, double[] outLocalTransform);
    private static native void nGetWorldTransform(long nativeTransformManager, int i, float[] outWorldTransform);
    private static native void nGetWorldTransformFp64(long nativeTransformManager, int i, double[] outWorldTransform);
    private static native void nOpenLocalTransformTransaction(long nativeTransformManager);
    private static native void nCommitLocalTransformTransaction(long nativeTransformManager);
    private static native void nSetAccurateTranslationsEnabled(long nativeTransformManager, boolean enable);
    private static native boolean nIsAccurateTranslationsEnabled(long nativeTransformManager);

}
