package io.github.erkko68.filament.wasm

// Kotlin Long <-> wasm i64 (a JS BigInt). Kotlin/JS doesn't represent Long as BigInt by
// default, so the stdlib's toJsBigInt() is a no-op cast there; go through the 32-bit halves.

fun Long.toI64(): JsBigInt = i64((this ushr 32).toInt(), toInt())

fun JsBigInt.toKotlinLong(): Long = (i64High(this).toLong() shl 32) or (i64Low(this).toLong() and 0xFFFFFFFFL)

private fun i64(high: Int, low: Int): JsBigInt = js("(BigInt(high) << BigInt(32)) | BigInt(low >>> 0)")
private fun i64High(value: JsBigInt): Int = js("Number(BigInt.asIntN(32, value >> BigInt(32)))")
private fun i64Low(value: JsBigInt): Int = js("Number(BigInt.asIntN(32, value))")
