package io.github.erkko68.filament.wasm

import org.w3c.dom.HTMLCanvasElement

/**
 * Creates the WebGL2 context Filament renders into, registers it with Emscripten and makes it
 * current: what upstream's `Engine.create` does in extensions.js + `EM_ASM`. Call before
 * building an OpenGL engine; returns the handle to make current again when switching engines.
 */
fun FilamentModule.createGlContext(canvas: HTMLCanvasElement, alpha: Boolean = false): Int {
    val attributes = glAttributes(alpha)
    val context = webGl2Context(canvas, attributes) ?: error("WebGL2 is not available")
    val handle = GL.registerContext(context, attributes)
    GL.makeContextCurrent(handle)
    return handle
}

private fun glAttributes(alpha: Boolean): JsAny =
    js("({ majorVersion: 2, minorVersion: 0, antialias: false, depth: true, alpha: alpha })")

// Compressed-texture extensions must be enabled up front; Filament only queries them.
private fun webGl2Context(canvas: HTMLCanvasElement, attributes: JsAny): JsAny? = js("""{
    const gl = canvas.getContext('webgl2', attributes);
    if (gl) ['WEBGL_compressed_texture_s3tc', 'WEBGL_compressed_texture_s3tc_srgb',
             'WEBGL_compressed_texture_astc', 'WEBGL_compressed_texture_etc']
        .forEach(function (e) { gl.getExtension(e); });
    return gl;
}""")
