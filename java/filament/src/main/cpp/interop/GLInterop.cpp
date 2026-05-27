#include <jni.h>

#if defined(_WIN32)
    #include <windows.h>
    #include <GL/gl.h>
#else
    #include <GL/gl.h>
#endif

// Some Linux/Windows GL headers omit GL_RGBA8 from <GL/gl.h>; it's GL 1.1.
#ifndef GL_RGBA8
#define GL_RGBA8 0x8058
#endif

// Caller must have a current GL context on this thread. The texture is created
// in that context's share group, so it must be a context that shares with both
// Filament's and Skia's GL contexts (or be one of them).
extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_GraphicsInterop_nCreateGLTexture(
        JNIEnv*, jclass, jint width, jint height) {
    if (width <= 0 || height <= 0) return 0;

    GLuint id = 0;
    glGenTextures(1, &id);
    if (id == 0) return 0;

    glBindTexture(GL_TEXTURE_2D, id);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);

    return (jlong) id;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_GraphicsInterop_nReleaseGLTexture(JNIEnv*, jclass, jlong handle) {
    if (handle == 0) return;
    GLuint id = (GLuint) handle;
    glDeleteTextures(1, &id);
}
