package io.github.erkko68.filament.interop

import io.github.erkko68.filament.InternalFilamentApi

// Common half of the skiko-style interop: API classes live in commonMain and call the Fila* C API
// through `external fun`s declared next to them, named like the C symbol. JVM/Android bind them
// through generated JNI glue, Kotlin/Native through [ExternalSymbolName], web by global name.

/** Address of a native object: `Long` on JVM, Android and Native; a wasm32 address (`Int`) on web. */
@InternalFilamentApi
expect class NativePointer

@InternalFilamentApi
expect val NullPointer: NativePointer

/** The C symbol a common `external fun` binds to (`@SymbolName` on Kotlin/Native, absent elsewhere). */
@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
expect annotation class ExternalSymbolName(val name: String)

/**
 * Hands Kotlin arrays to C for the duration of one call: pinned on Native, copied into native memory
 * (and back with [fromInterop]) on JVM, Android and web. Use through [interopScope].
 */
@InternalFilamentApi
expect class InteropScope() {
    fun toInterop(array: IntArray?): NativePointer

    /** Copies what C wrote at this pointer back into [result] (a no-op where the array was pinned). */
    fun NativePointer.fromInterop(result: IntArray)

    fun release()
}

@InternalFilamentApi
inline fun <T> interopScope(block: InteropScope.() -> T): T {
    val scope = InteropScope()
    try {
        return scope.block()
    } finally {
        scope.release()
    }
}
