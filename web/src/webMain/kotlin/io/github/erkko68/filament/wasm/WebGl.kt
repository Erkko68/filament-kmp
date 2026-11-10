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

/** Registers a WebGL2 context another renderer created (e.g. Compose's) and makes it current. */
fun FilamentModule.adoptGlContext(context: JsAny): Int {
    enableCompressedTextures(context)
    val handle = GL.registerContext(context, glAttributes(alpha = true))
    GL.makeContextCurrent(handle)
    return handle
}

/** Registers [texture] (a WebGLTexture of the current context) with Emscripten: the id `Texture.Builder.import` takes. */
fun FilamentModule.registerGlTexture(texture: JsAny): Int = registerTexture(GL, texture)

fun FilamentModule.unregisterGlTexture(id: Int): Unit = unregisterTexture(GL, id)

private fun registerTexture(gl: EmscriptenGL, texture: JsAny): Int = js("{ const id = gl.getNewId(gl.textures); texture.name = id; gl.textures[id] = texture; return id; }")

private fun unregisterTexture(gl: EmscriptenGL, id: Int): Unit = js("{ gl.textures[id] = null; }")

private fun enableCompressedTextures(gl: JsAny): Unit = js("""{
    ['WEBGL_compressed_texture_s3tc', 'WEBGL_compressed_texture_s3tc_srgb',
     'WEBGL_compressed_texture_astc', 'WEBGL_compressed_texture_etc']
        .forEach(function (e) { gl.getExtension(e); });
}""")

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

/**
 * Unregisters [handle] and loses [canvas]'s WebGL context right away, instead of waiting for GC:
 * browsers cap live contexts (~16), so tests and apps creating many engines exhaust them otherwise.
 */
fun FilamentModule.releaseGlContext(canvas: HTMLCanvasElement, handle: Int) {
    GL.deleteContext(handle)
    loseContext(canvas)
}

private fun loseContext(canvas: HTMLCanvasElement): Unit = js("""{
    const gl = canvas.getContext('webgl2');
    const ext = gl && gl.getExtension('WEBGL_lose_context');
    if (ext) ext.loseContext();
}""")
