#include <jni.h>

#if defined(__linux__) && !defined(__ANDROID__)
#include <dlfcn.h>
#include <jawt_md.h>
#include <GL/glx.h>
#include <GL/glext.h>

#define GLX_JNI(ret, name) extern "C" JNIEXPORT ret JNICALL Java_io_github_erkko68_filament_jni_GlxHelper_##name

namespace {

// skiko has already loaded libjawt (GlxHelper.kt loads it too), so look it up rather than link it:
// a DT_NEEDED on libjawt.so wouldn't resolve, the JDK's lib dir isn't on the loader's path.
JAWT* awt(JNIEnv* env) {
    static JAWT instance{};
    static bool ready = false;
    if (!ready) {
        void* lib = dlopen("libjawt.so", RTLD_LAZY | RTLD_NOLOAD);
        auto getAwt = lib ? (jboolean (JNICALL*)(JNIEnv*, JAWT*)) dlsym(lib, "JAWT_GetAWT") : nullptr;
        if (!getAwt) return nullptr;
        instance.version = JAWT_VERSION_1_4;
        ready = getAwt(env, &instance) == JNI_TRUE;
    }
    return ready ? &instance : nullptr;
}

// Locked drawing surface and skiko's framebuffer bindings, kept between nBegin and nEnd.
struct Session {
    JAWT* jawt;
    JAWT_DrawingSurface* ds;
    JAWT_DrawingSurfaceInfo* dsi;
    GLint drawFramebuffer;
    GLint readFramebuffer;
};

template<typename T>
T proc(const char* name) {
    return (T) glXGetProcAddressARB((const GLubyte*) name);
}

} // namespace

// skiko keeps its GLXContext behind a heap pointer (`new GLXContext(glXCreateContext(...))`).
GLX_JNI(jlong, nSkikoContext)(JNIEnv*, jclass, jlong skikoContext) {
    return skikoContext ? (jlong) *(GLXContext*) skikoContext : 0;
}

// Filament's PlatformGLX looks the shared context's GLXFBConfig up by GLX_FBCONFIG_ID, which a context
// made from an XVisualInfo (skiko's glXCreateContext) doesn't have. This context joins the current
// context's share group from a pbuffer-capable FBConfig, so Filament can share through it instead.
GLX_JNI(jlong, nCreateBridgeContext)(JNIEnv*, jclass) {
    static auto createContextAttribs =
            proc<PFNGLXCREATECONTEXTATTRIBSARBPROC>("glXCreateContextAttribsARB");
    Display* display = glXGetCurrentDisplay();
    GLXContext current = glXGetCurrentContext();
    if (!display || !current || !createContextAttribs) return 0;
    const int attribs[] = {
            GLX_DRAWABLE_TYPE, GLX_PBUFFER_BIT,
            GLX_RENDER_TYPE, GLX_RGBA_BIT,
            GLX_RED_SIZE, 8, GLX_GREEN_SIZE, 8, GLX_BLUE_SIZE, 8, GLX_ALPHA_SIZE, 8,
            GLX_DEPTH_SIZE, 24,
            None
    };
    int count = 0;
    GLXFBConfig* configs = glXChooseFBConfig(display, DefaultScreen(display), attribs, &count);
    if (!configs || count == 0) return 0;
    const int contextAttribs[] = {
            GLX_CONTEXT_MAJOR_VERSION_ARB, 4,
            GLX_CONTEXT_MINOR_VERSION_ARB, 1,
            None
    };
    GLXContext bridge = createContextAttribs(display, configs[0], current, True, contextAttribs);
    XFree(configs);
    return (jlong) bridge;
}

GLX_JNI(void, nDestroyBridgeContext)(JNIEnv*, jclass, jlong context) {
    glXDestroyContext(glXGetCurrentDisplay(), (GLXContext) context);
}

// Locks [component]'s AWT drawing surface and makes [context] current on its X window. skiko draws on
// the same thread and re-makes its context current before each frame, so leaving it current is safe.
// It doesn't rebind its framebuffer, though: on a resize it wraps whatever is bound as the window
// surface, so nEnd restores the bindings Skia's work in between moves.
GLX_JNI(jlong, nBegin)(JNIEnv* env, jclass, jobject component, jlong context) {
    JAWT* jawt = awt(env);
    if (!jawt) return 0;
    JAWT_DrawingSurface* ds = jawt->GetDrawingSurface(env, component);
    if (!ds) return 0;
    if (ds->Lock(ds) & JAWT_LOCK_ERROR) {
        jawt->FreeDrawingSurface(ds);
        return 0;
    }
    JAWT_DrawingSurfaceInfo* dsi = ds->GetDrawingSurfaceInfo(ds);
    auto* x11 = dsi ? (JAWT_X11DrawingSurfaceInfo*) dsi->platformInfo : nullptr;
    if (!x11 || !glXMakeCurrent(x11->display, x11->drawable, (GLXContext) context)) {
        if (dsi) ds->FreeDrawingSurfaceInfo(dsi);
        ds->Unlock(ds);
        jawt->FreeDrawingSurface(ds);
        return 0;
    }
    auto* session = new Session{ jawt, ds, dsi, 0, 0 };
    glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &session->drawFramebuffer);
    glGetIntegerv(GL_READ_FRAMEBUFFER_BINDING, &session->readFramebuffer);
    return (jlong) session;
}

GLX_JNI(void, nEnd)(JNIEnv*, jclass, jlong session) {
    static auto bindFramebuffer = proc<PFNGLBINDFRAMEBUFFERPROC>("glBindFramebuffer");
    auto* s = (Session*) session;
    bindFramebuffer(GL_DRAW_FRAMEBUFFER, s->drawFramebuffer);
    bindFramebuffer(GL_READ_FRAMEBUFFER, s->readFramebuffer);
    s->ds->FreeDrawingSurfaceInfo(s->dsi);
    s->ds->Unlock(s->ds);
    s->jawt->FreeDrawingSurface(s->ds);
    delete s;
}

// An immutable RGBA8 texture in the current context's share group, for Filament's importTexture.
GLX_JNI(jint, nCreateTexture)(JNIEnv*, jclass, jint width, jint height) {
    static auto texStorage2D = proc<PFNGLTEXSTORAGE2DPROC>("glTexStorage2D");
    GLint previous = 0;
    glGetIntegerv(GL_TEXTURE_BINDING_2D, &previous);
    GLuint texture = 0;
    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);
    texStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glBindTexture(GL_TEXTURE_2D, previous);
    return glGetError() == GL_NO_ERROR ? (jint) texture : 0;
}

// Framebuffers aren't shared between contexts: this one belongs to the current (skiko's) context.
GLX_JNI(jint, nCreateFramebuffer)(JNIEnv*, jclass, jint texture) {
    static auto genFramebuffers = proc<PFNGLGENFRAMEBUFFERSPROC>("glGenFramebuffers");
    static auto bindFramebuffer = proc<PFNGLBINDFRAMEBUFFERPROC>("glBindFramebuffer");
    static auto framebufferTexture2D = proc<PFNGLFRAMEBUFFERTEXTURE2DPROC>("glFramebufferTexture2D");
    static auto checkFramebufferStatus = proc<PFNGLCHECKFRAMEBUFFERSTATUSPROC>("glCheckFramebufferStatus");
    // skiko wraps whatever framebuffer is bound when it (re)creates its window surface; restore it.
    GLint previous = 0;
    glGetIntegerv(GL_FRAMEBUFFER_BINDING, &previous);
    GLuint framebuffer = 0;
    genFramebuffers(1, &framebuffer);
    bindFramebuffer(GL_FRAMEBUFFER, framebuffer);
    framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, (GLuint) texture, 0);
    const bool complete = checkFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    bindFramebuffer(GL_FRAMEBUFFER, previous);
    return complete ? (jint) framebuffer : 0;
}

GLX_JNI(void, nDestroy)(JNIEnv*, jclass, jint texture, jint framebuffer) {
    static auto deleteFramebuffers = proc<PFNGLDELETEFRAMEBUFFERSPROC>("glDeleteFramebuffers");
    GLuint fb = framebuffer, tex = texture;
    if (fb) deleteFramebuffers(1, &fb);
    if (tex) glDeleteTextures(1, &tex);
}
#endif
