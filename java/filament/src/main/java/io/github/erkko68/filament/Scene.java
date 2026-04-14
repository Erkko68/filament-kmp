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

public class Scene {
    private long mNativeObject;
    private @Nullable Skybox mSkybox;
    private @Nullable IndirectLight mIndirectLight;

    Scene(long nativeScene) {
        mNativeObject = nativeScene;
    }

    @Nullable public Skybox getSkybox() { return mSkybox; }
    public void setSkybox(@Nullable Skybox skybox) {
        mSkybox = skybox;
        nSetSkybox(getNativeObject(), skybox != null ? skybox.getNativeObject() : 0);
    }

    @Nullable public IndirectLight getIndirectLight() { return mIndirectLight; }
    public void setIndirectLight(@Nullable IndirectLight indirectLight) {
        mIndirectLight = indirectLight;
        nSetIndirectLight(getNativeObject(), indirectLight != null ? indirectLight.getNativeObject() : 0);
    }

    public void addEntity(int entity) { nAddEntity(getNativeObject(), entity); }
    public void addEntities(@NotNull int[] entities) { nAddEntities(getNativeObject(), entities); }

    public void removeEntity(int entity) { nRemove(getNativeObject(), entity); }
    public void removeEntities(@NotNull int[] entities) { nRemoveEntities(getNativeObject(), entities); }

    public int getEntityCount() { return nGetEntityCount(getNativeObject()); }
    public int getRenderableCount() { return nGetRenderableCount(getNativeObject()); }
    public int getLightCount() { return nGetLightCount(getNativeObject()); }
    public boolean hasEntity(int entity) { return nHasEntity(getNativeObject(), entity); }

    @NotNull
    public int[] getEntities(@Nullable int[] outArray) {
        int c = getEntityCount();
        if (outArray == null || outArray.length < c) {
            outArray = new int[c];
        }
        boolean success = nGetEntities(getNativeObject(), outArray, outArray.length);
        if (!success) {
            throw new IllegalStateException("Error retriving Scene's entities");
        }
        return outArray;
    }

    @NotNull
    public int[] getEntities() {
        return getEntities(null);
    }

    public interface EntityProcessor {
        void process(int entity);
    }

    public void forEach(@NotNull EntityProcessor entityProcessor) {
        int[] entities = getEntities(null);
        for (int entity : entities) {
            entityProcessor.process(entity);
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Scene");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetSkybox(long nativeScene, long nativeSkybox);
    private static native void nSetIndirectLight(long nativeScene, long nativeIndirectLight);
    private static native void nAddEntity(long nativeScene, int entity);
    private static native void nAddEntities(long nativeScene, int[] entities);
    private static native void nRemove(long nativeScene, int entity);
    private static native void nRemoveEntities(long nativeScene, int[] entities);
    private static native int nGetEntityCount(long nativeScene);
    private static native int nGetRenderableCount(long nativeScene);
    private static native int nGetLightCount(long nativeScene);
    private static native boolean nHasEntity(long nativeScene, int entity);
    private static native boolean nGetEntities(long nativeScene, int[] outArray, int length);
}
