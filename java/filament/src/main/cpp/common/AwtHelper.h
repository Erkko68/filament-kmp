#pragma once
#include <jni.h>

/**
 * Utility to extract a native window handle from a Java AWT Component on JVM.
 */
class AwtHelper {
public:
    /**
     * Extracts a native window handle (void*) from a jobject that is a Component.
     * Returns nullptr if the object is not a Component or if extraction fails.
     */
    static void* getNativeWindow(JNIEnv* env, jobject component);
};
