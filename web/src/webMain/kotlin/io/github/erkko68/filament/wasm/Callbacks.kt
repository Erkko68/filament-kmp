package io.github.erkko68.filament.wasm

/**
 * Kotlin lambdas behind C callbacks (design §5). Every C callback takes a `void* userData`: we
 * pass a registry id there and one shared trampoline per C signature, created once with
 * `addFunction`, looks the lambda up. Single-threaded wasm fires them on the main thread during
 * beginFrame/endFrame/flush, so no handler (thread hop) is needed; pass 0 for it.
 */
object Callbacks {
    private class Entry(val once: Boolean, val fn: (Int, Int) -> Unit)

    private val entries = HashMap<Int, Entry>()
    private var nextId = 1

    /** Registers [fn] (receiving the callback's leading pointer args) and returns its userData id. */
    fun register(once: Boolean, fn: (a: Int, b: Int) -> Unit): Int {
        val id = nextId++
        entries[id] = Entry(once, fn)
        return id
    }

    fun release(id: Int) { entries.remove(id) }

    private fun fire(id: Int, a: Int, b: Int) {
        val entry = (if (entries[id]?.once == true) entries.remove(id) else entries[id]) ?: return
        // An exception escaping into wasm would unwind through C++ frames; report and swallow.
        try { entry.fn(a, b) } catch (t: Throwable) { t.printStackTrace() }
    }

    /** `void (*)(void* userData)` — e.g. FilaEngineCompileCallback, frame-scheduled. */
    val userOnly: Int by lazy { fila.addFunction(fn1 { user -> fire(user, 0, 0) }, "vi") }

    /** `void (*)(T* arg, void* userData)` — e.g. picking, frame-completed, material compile. */
    val argUser: Int by lazy { fila.addFunction(fn2 { arg, user -> fire(user, arg, 0) }, "vii") }

    /**
     * FilaBufferCallback `void (*)(void* buffer, size_t size, void* userData)`: frees the heap
     * copy made for the upload, then runs the registered lambda if [userData] isn't 0.
     */
    val freeBuffer: Int by lazy {
        fila.addFunction(fn3 { buffer, size, user -> fila._free(buffer); if (user != 0) fire(user, buffer, size) }, "viii")
    }

    /** FilaBufferCallback that leaves the buffer alone (e.g. readPixels, which reads it in the lambda). */
    val keepBuffer: Int by lazy { fila.addFunction(fn3 { buffer, size, user -> fire(user, buffer, size) }, "viii") }
}

private fun fn1(f: (Int) -> Unit): JsAny = js("f")
private fun fn2(f: (Int, Int) -> Unit): JsAny = js("f")
private fun fn3(f: (Int, Int, Int) -> Unit): JsAny = js("f")
