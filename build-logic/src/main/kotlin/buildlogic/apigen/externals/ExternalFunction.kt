package buildlogic.apigen.externals

import java.io.File

/**
 * One common `@ExternalSymbolName("…") external fun`: the C [symbol] it binds, its Kotlin [name],
 * parameters and [returnType] as written in Kotlin (`NativePointer`, `Int`, `Long`, `Float`, `Double`,
 * `Boolean`, `Unit`).
 */
class ExternalFunction(val symbol: String, val name: String, val params: List<Param>, val returnType: String) {
    class Param(val name: String, val type: String)
}

/**
 * The [functions] declared in one commonMain [file]. On the JVM they compile to static methods of
 * [jvmClass], the file's facade (`FooKt`, or its `@file:JvmName`).
 */
class ExternalSource(val file: File, val kotlinModule: String, val jvmClass: String, val functions: List<ExternalFunction>)
