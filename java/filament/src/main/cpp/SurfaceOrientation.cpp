/*
 * Copyright (C) 2020 The Android Open Source Project
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

#include <jni.h>
#include <filament/Engine.h>
#include <geometry/SurfaceOrientation.h>

#include "common/CallbackUtils.h"

using namespace filament;
using namespace filament::geometry;
using namespace filament::math;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new SurfaceOrientation::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (SurfaceOrientation::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderVertexCount(JNIEnv*, jclass, jlong nativeBuilder, jint vertexCount) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    builder->vertexCount(vertexCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderNormals(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining, jint stride) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->normals((const float3*) autoBuffer.getData(), stride);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderTangents(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining, jint stride) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->tangents((const float4*) autoBuffer.getData(), stride);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderUVs(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining, jint stride) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->uvs((const float2*) autoBuffer.getData(), stride);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderPositions(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining, jint stride) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->positions((const float3*) autoBuffer.getData(), stride);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderTriangleCount(JNIEnv*, jclass, jlong nativeBuilder, jint triangleCount) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    builder->triangleCount(triangleCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderTriangles16(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->triangles((const ushort3*) autoBuffer.getData());
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderTriangles32(JNIEnv* env, jclass, jlong nativeBuilder, jobject buffer, jint remaining) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    AutoBuffer autoBuffer(env, buffer, remaining);
    builder->triangles((const uint3*) autoBuffer.getData());
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder) {
    SurfaceOrientation::Builder* builder = (SurfaceOrientation::Builder*) nativeBuilder;
    return (jlong) builder->build();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nGetVertexCount(JNIEnv*, jclass, jlong nativeSurfaceOrientation) {
    SurfaceOrientation* orientation = (SurfaceOrientation*) nativeSurfaceOrientation;
    return (jint) orientation->getVertexCount();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nGetQuatsAsFloat(JNIEnv* env, jclass, jlong nativeSurfaceOrientation, jobject buffer, jint remaining) {
    SurfaceOrientation* orientation = (SurfaceOrientation*) nativeSurfaceOrientation;
    AutoBuffer autoBuffer(env, buffer, remaining);
    orientation->getQuats((quatf*) autoBuffer.getData(), remaining / 4);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nGetQuatsAsHalf(JNIEnv* env, jclass, jlong nativeSurfaceOrientation, jobject buffer, jint remaining) {
    SurfaceOrientation* orientation = (SurfaceOrientation*) nativeSurfaceOrientation;
    AutoBuffer autoBuffer(env, buffer, remaining);
    orientation->getQuats((quath*) autoBuffer.getData(), remaining / 4);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nGetQuatsAsShort(JNIEnv* env, jclass, jlong nativeSurfaceOrientation, jobject buffer, jint remaining) {
    SurfaceOrientation* orientation = (SurfaceOrientation*) nativeSurfaceOrientation;
    AutoBuffer autoBuffer(env, buffer, remaining);
    orientation->getQuats((short4*) autoBuffer.getData(), remaining / 4);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SurfaceOrientation_nDestroy(JNIEnv*, jclass, jlong nativeSurfaceOrientation) {
    delete (SurfaceOrientation*) nativeSurfaceOrientation;
}

} // extern "C"
