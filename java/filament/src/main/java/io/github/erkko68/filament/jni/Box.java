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

/**
 * An axis-aligned 3D box represented by its center and half-extent.
 */
public class Box {
    private final float[] mCenter = new float[3];
    private final float[] mHalfExtent = new float[3];

    public Box() {}

    public Box(float centerX, float centerY, float centerZ,
               float halfExtentX, float halfExtentY, float halfExtentZ) {
        mCenter[0] = centerX;
        mCenter[1] = centerY;
        mCenter[2] = centerZ;
        mHalfExtent[0] = halfExtentX;
        mHalfExtent[1] = halfExtentY;
        mHalfExtent[2] = halfExtentZ;
    }

    public Box(@NotNull float[] center, @NotNull float[] halfExtent) {
        if (center.length < 3 || halfExtent.length < 3) {
            throw new IllegalArgumentException("Arrays must be at least size 3");
        }
        mCenter[0] = center[0];
        mCenter[1] = center[1];
        mCenter[2] = center[2];
        mHalfExtent[0] = halfExtent[0];
        mHalfExtent[1] = halfExtent[1];
        mHalfExtent[2] = halfExtent[2];
    }

    public void setCenter(float centerX, float centerY, float centerZ) {
        mCenter[0] = centerX;
        mCenter[1] = centerY;
        mCenter[2] = centerZ;
    }

    public void setHalfExtent(float halfExtentX, float halfExtentY, float halfExtentZ) {
        mHalfExtent[0] = halfExtentX;
        mHalfExtent[1] = halfExtentY;
        mHalfExtent[2] = halfExtentZ;
    }

    @NotNull
    public float[] getCenter() { return mCenter; }

    @NotNull
    public float[] getHalfExtent() { return mHalfExtent; }
}
