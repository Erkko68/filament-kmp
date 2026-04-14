/*
 * Copyright (C) 2022 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package io.github.erkko68.filament.utils

import io.github.erkko68.filament.utils.Half.Companion.POSITIVE_INFINITY
import io.github.erkko68.filament.utils.Half.Companion.POSITIVE_ZERO

import kotlin.jvm.JvmInline

fun Half(value: Double) = Half(floatToHalf(value.toFloat()))

fun Double.toHalf() = Half(floatToHalf(toFloat()))

val Double.h: Half
    get() = Half(floatToHalf(toFloat()))

fun Half(value: Float) = Half(floatToHalf(value))

fun Float.toHalf() = Half(floatToHalf(this))

val Float.h: Half
    get() = Half(floatToHalf(this))

fun Half(value: String) = Half(floatToHalf(value.toFloat()))

fun String.toHalf() = Half(floatToHalf(toFloat()))

@JvmInline
value class Half(val v: UShort) : Comparable<Half> {
    companion object {
        const val SIZE = 16
        val EPSILON = Half(0x1400.toUShort())
        const val MAX_EXPONENT = 15
        const val MIN_EXPONENT = -14
        val LOWEST_VALUE = Half(0xfbff.toUShort())
        val MAX_VALUE = Half(0x7bff.toUShort())
        val MIN_NORMAL = Half(0x0400.toUShort())
        val MIN_VALUE = Half(0x0001.toUShort())
        val NaN = Half(0x7e00.toUShort())
        val NEGATIVE_INFINITY = Half(0xfc00.toUShort())
        val NEGATIVE_ZERO = Half(0x8000.toUShort())
        val POSITIVE_INFINITY = Half(0x7c00.toUShort())
        val POSITIVE_ZERO = Half(0x0000.toUShort())

        fun fromBits(bits: Int) = Half((bits and 0xffff).toUShort())
    }

    val sign: Half
        get() {
            val bits = v.toInt()
            val abs = bits and FP16_ABS
            return when {
                abs > FP16_EXPONENT_MAX -> NaN
                abs == 0 -> POSITIVE_ZERO
                else -> if (bits and FP16_SIGN_MASK != 0) Half(-1.0f) else Half(1.0f)
            }
        }

    val exponent: Int
        get() = ((v.toInt() ushr FP16_EXPONENT_SHIFT) and FP16_EXPONENT_MASK) - FP16_EXPONENT_BIAS

    val significand: Int
        get() = v.toInt() and FP16_SIGNIFICAND_MASK

    val absoluteValue: Half
        get() = Half((v.toInt() and FP16_ABS).toUShort())

    val ulp: Half
        get() = when {
            isNaN() -> NaN
            isInfinite() -> POSITIVE_INFINITY
            v.toInt() and FP16_ABS == 0x7bff -> Half(0x4c00.toUShort())
            else -> {
                val d = absoluteValue
                d.nextUp() - d
            }
        }

    fun toBits() = v.toInt()
    fun toByte() = halfToShort(v).toInt().toByte()
    fun toShort() = halfToShort(v).toInt().toShort()
    fun toInt() = halfToShort(v).toInt()
    fun toLong() = halfToShort(v).toLong()
    fun toFloat() = halfToShort(v)
    fun toDouble() = halfToShort(v).toDouble()

    fun isNaN() = (v.toInt() and FP16_ABS) > FP16_EXPONENT_MAX
    fun isInfinite() = (v.toInt() and FP16_ABS) == FP16_EXPONENT_MAX
    fun isFinite() = (v.toInt() and FP16_EXPONENT_MAX) != FP16_EXPONENT_MAX
    fun isZero() = (v.toInt() and FP16_ABS) == 0
    fun isNormalized() = (v.toInt() and FP16_EXPONENT_MAX) != 0
        && (v.toInt() and FP16_EXPONENT_MAX) != FP16_EXPONENT_MAX

    fun withSign(sign: Half) =
        Half(((sign.v.toInt() and FP16_SIGN_MASK) or (v.toInt() and FP16_ABS)).toUShort())

    fun nextUp(): Half = when {
        isNaN() || v == POSITIVE_INFINITY.v -> this
        isZero() -> MIN_VALUE
        else -> Half((toBits() + if (v.toInt() and FP16_SIGN_MASK == 0) 1 else -1).toUShort())
    }

    fun nextDown(): Half = when {
        isNaN() || v == NEGATIVE_INFINITY.v -> this
        isZero() -> -MIN_VALUE
        else -> Half((toBits() + if (v.toInt() and FP16_SIGN_MASK == 0) -1 else 1).toUShort())
    }

    fun nextTowards(to: Half) = when {
        isNaN() || to.isNaN() -> NaN
        to == this -> this
        to > this -> nextUp()
        else -> nextDown()
    }

    fun roundToInt() = when {
        isNaN() -> throw IllegalArgumentException("Cannot round NaN value.")
        else -> round(this).toInt()
    }

    fun roundToLong() = when {
        isNaN() -> throw IllegalArgumentException("Cannot round NaN value.")
        else -> round(this).toLong()
    }

    operator fun unaryMinus() = Half((v.toInt() xor FP16_SIGN_MASK).toUShort())
    operator fun unaryPlus() = Half(v)

    operator fun plus(other: Half): Half {
        val xbits = toBits()
        val ybits = other.toBits()
        val sub = ((xbits xor ybits) and FP16_SIGN_MASK) != 0
        var ax = xbits and FP16_ABS
        var ay = ybits and FP16_ABS
        if (ax >= FP16_EXPONENT_MAX || ay >= FP16_EXPONENT_MAX) {
            return Half((
                if (ax > FP16_EXPONENT_MAX || ay > FP16_EXPONENT_MAX) quiet(ax, ay)
                else if (ay != FP16_EXPONENT_MAX) xbits
                else if (sub && ax == FP16_EXPONENT_MAX) FP16_QUIET_NAN
                else ybits
            ).toUShort())
        }
        if (ax == 0) return if (ay != 0) other else Half((xbits and ybits).toUShort())
        if (ay == 0) return this
        val s = (if (sub && ay > ax) ybits else xbits) and FP16_SIGN_MASK
        if (ay > ax) {
            val t = ax
            ax = ay
            ay = t
        }
        var e = (ax shr 10) + if (ax <= FP16_SIGNIFICAND_MASK) 1 else 0
        val d = e - (ay shr 10) - if (ay <= FP16_SIGNIFICAND_MASK) 1 else 0
        var mx = ((ax and FP16_SIGNIFICAND_MASK) or
            ((if (ax > FP16_SIGNIFICAND_MASK) 1 else 0) shl 10)) shl 3
        var my: Int
        if (d < 13) {
            my = ((ay and FP16_SIGNIFICAND_MASK) or
                ((if (ay > FP16_SIGNIFICAND_MASK) 1 else 0) shl 10)) shl 3
            my = (my shr d) or (if ((my and ((1 shl d) - 1)) != 0) 1 else 0)
        } else {
            my = 1
        }
        if (sub) {
            mx -= my
            if (mx == 0) return POSITIVE_ZERO
            while (mx < 0x2000 && e > 1) {
                mx = mx shl 1
                e--
            }
        } else {
            mx += my
            val i = mx shr 14
            e += i
            if (e > 30) return Half((s or FP16_EXPONENT_MAX).toUShort())
            mx = (mx shr i) or (mx and i)
        }
        val v = s +((e - 1) shl 10) + (mx shr 3)
        val G = (mx shr 2) and 1
        val S = if (mx and 0x3 != 0) 1 else 0
        return Half((v + (G and (S or v))).toUShort())
    }

    operator fun minus(other: Half) = this + (-other)

    operator fun times(other: Half): Half {
        val xbits = toBits()
        val ybits = other.toBits()
        val s = (xbits xor ybits) and FP16_SIGN_MASK
        var e = -16
        var ax = xbits and FP16_ABS
        var ay = ybits and FP16_ABS
        if (ax >= FP16_EXPONENT_MAX || ay >= FP16_EXPONENT_MAX) {
            return Half((when {
                ax > FP16_EXPONENT_MAX || ay > FP16_EXPONENT_MAX -> quiet(ax, ay)
                (ax == FP16_EXPONENT_MAX && ay == 0) || (ay == FP16_EXPONENT_MAX && ax == 0) -> FP16_QUIET_NAN
                else -> s or FP16_EXPONENT_MAX
            }).toUShort())
        }
        if (ax == 0 || ay == 0) return Half(s.toUShort())
        while (ax < 0x400) {
            ax = ax shl 1
            e--
        }
        while (ay < 0x400) {
            ay = ay shl 1
            e--
        }
        val m =
            ((ax and FP16_SIGNIFICAND_MASK) or 0x400).toLong() *
            ((ay and FP16_SIGNIFICAND_MASK) or 0x400).toLong()
        val i = (m shr 21).toInt()
        e += (ax shr 10) + (ay shr 10) + i
        if (e > 29) return Half((s or FP16_EXPONENT_MAX).toUShort())
        else if (e < -11) return Half(s.toUShort())
        return fixedToHalf(s, e, (m shr i).toUInt(), (m and ((1L shl i) - 1)).toUInt(), 20)
    }

    operator fun div(other: Half): Half {
        val xbits = toBits()
        val ybits = other.toBits()
        val s = (xbits xor ybits) and FP16_SIGN_MASK
        var e = 14
        var ax = xbits and FP16_ABS
        var ay = ybits and FP16_ABS
        if (ax >= FP16_EXPONENT_MAX || ay >= FP16_EXPONENT_MAX) {
            return Half((when {
                ax > FP16_EXPONENT_MAX || ay > FP16_EXPONENT_MAX -> quiet(ax, ay)
                ax == ay -> FP16_QUIET_NAN
                else -> s or (if (ax == FP16_EXPONENT_MAX) FP16_EXPONENT_MAX else 0)
            }).toUShort())
        }
        if (ax == 0) return Half((if (ay == 0) FP16_QUIET_NAN else s).toUShort())
        if (ay == 0) return Half((s or FP16_EXPONENT_MAX).toUShort())
        while (ax < 0x400) {
            ax = ax shl 1
            e--
        }
        while (ay < 0x400) {
            ay = ay shl 1
            e++
        }
        var mx = ((ax and FP16_SIGNIFICAND_MASK) or 0x400).toUInt()
        var my = ((ay and FP16_SIGNIFICAND_MASK) or 0x400).toUInt()
        val i = if (mx < my) 1 else 0
        e += (ax shr 10) - (ay shr 10) - i
        if (e > 29) return Half((s or FP16_EXPONENT_MAX).toUShort())
        else if (e < -11) return Half(s.toUShort())
        mx = mx shl (12 + i)
        my = my shl 1
        return fixedToHalf(s, e, mx / my, if (mx % my != 0U) 1U else 0U, 11)
    }

    operator fun inc() = this + Half(0x3c00.toUShort())
    operator fun dec() = this + Half(0xbc00.toUShort())

    override fun compareTo(other: Half): Int {
        var x = v.toShort().toInt()
        var y = other.v.toShort().toInt()
        if (x and FP16_ABS > FP16_EXPONENT_MAX) x = FP16_NAN
        if (y and FP16_ABS > FP16_EXPONENT_MAX) y = FP16_NAN
        if (x == y) return 0
        val a = (x xor (FP16_SIGN_MASK or (FP16_SIGN_MASK - (x shr 15)))) + (x shr 15)
        val b = (y xor (FP16_SIGN_MASK or (FP16_SIGN_MASK - (y shr 15)))) + (y shr 15)
        return if (a < b) -1 else 1
    }

    override fun toString() = toFloat().toString()

    fun toHexString(): String {
        val o = StringBuilder()
        val bits = v.toInt()
        val s = bits ushr FP16_SIGN_SHIFT
        val e = bits ushr FP16_EXPONENT_SHIFT and FP16_EXPONENT_MASK
        val m = bits and FP16_SIGNIFICAND_MASK
        if (e == 0x1f) {
            if (m == 0) {
                if (s != 0) o.append('-')
                o.append("Infinity")
            } else {
                o.append("NaN")
            }
        } else {
            if (s == 1) o.append('-')
            if (e == 0) {
                if (m == 0) {
                    o.append("0x0.0p0")
                } else {
                    o.append("0x0.")
                    val significand = m.toString(16)
                    o.append(significand.replaceFirst("0{2,}$".toRegex(), ""))
                    o.append("p-14")
                }
            } else {
                o.append("0x1.")
                val significand = m.toString(16)
                o.append(significand.replaceFirst("0{2,}$".toRegex(), ""))
                o.append('p')
                o.append((e - FP16_EXPONENT_BIAS).toString())
            }
        }
        return o.toString()
    }
}

fun sqrt(x: Half): Half {
    val bits = x.toBits()
    var a = bits and FP16_ABS
    var e = 15
    if (a == 0 || a >= FP16_EXPONENT_MAX) {
        return Half((when {
            a > FP16_EXPONENT_MAX -> quiet(bits)
            bits > FP16_SIGN_MASK -> FP16_QUIET_NAN
            else -> bits
        }).toUShort())
    }
    while (a < 0x400) {
        a = a shl 1
        e--
    }
    var r = ((a and FP16_SIGNIFICAND_MASK) or 0x400).toUInt() shl 10
    e += a shr 10
    val i = e and 1
    r = r shl i
    e = (e - i) / 2
    var m = 0U
    var b = 1U shl 20
    while (b != 0U) {
        if (r < m + b) {
            m = m shr 1
        } else {
            r -= m + b
            m = (m shr 1) + b
        }
        b = b shr 2
    }
    val v = (e shl 10).toUInt() + (m and 0x3ffU)
    val G = if (r > m) 1U else 0U
    val S = if (r != 0U) 1U else 0U
    return Half((v + (G and (S or v))).toUShort())
}

fun abs(x: Half) = x.absoluteValue

fun min(x: Half, y: Half): Half {
    val a = x.toBits()
    if (a and FP16_ABS > FP16_EXPONENT_MAX) return Half.NaN
    val b = y.toBits()
    if (b and FP16_ABS > FP16_EXPONENT_MAX) return Half.NaN
    if (a and FP16_ABS == 0 && b and FP16_ABS == 0) {
        return if (a and FP16_SIGN_MASK != 0) x else y
    }
    return if ((if (a and FP16_SIGN_MASK != 0) 0x8000 - (a and 0xffff) else a and 0xffff) <
               (if (b and FP16_SIGN_MASK != 0) 0x8000 - (b and 0xffff) else b and 0xffff)) x else y
}

fun max(x: Half, y: Half): Half {
    val a = x.toBits()
    if (a and FP16_ABS > FP16_EXPONENT_MAX) return Half.NaN
    val b = y.toBits()
    if (b and FP16_ABS > FP16_EXPONENT_MAX) return Half.NaN
    if (a and FP16_ABS == 0 && b and FP16_ABS == 0) {
        return if (a and FP16_SIGN_MASK != 0) y else x
    }
    return if ((if (a and FP16_SIGN_MASK != 0) 0x8000 - (a and 0xffff) else a and 0xffff) >
               (if (b and FP16_SIGN_MASK != 0) 0x8000 - (b and 0xffff) else b and 0xffff)) x else y
}

fun round(x: Half): Half {
    val bits = x.toBits()
    var a = bits and FP16_ABS
    var result = bits
    if (a < 0x3c00) {
        result = (result and FP16_SIGN_MASK) or (0x3c00 and (if (a >= 0x3800) 0xffff else 0x0))
    } else if (a < 0x6400) {
        a = 25 - (a shr 10)
        val mask = (1 shl a) - 1
        result += 1 shl (a - 1)
        result = result and mask.inv()
    } else {
        if (a > FP16_EXPONENT_MAX) result = quiet(result)
    }
    return Half(result.toUShort())
}

fun floor(x: Half): Half {
    val bits = x.toBits()
    var a = bits and FP16_ABS
    var result = bits
    if (a < 0x3c00) {
        result = (result and FP16_SIGN_MASK) or (0x3c00 and if (bits > 0x8000) 0xffff else 0x0)
    } else if (a < 0x6400) {
        a = 25 - (a shr 10)
        val mask = (1 shl a) - 1
        result += mask and -(bits shr 15)
        result = result and mask.inv()
    } else {
        if (a > FP16_EXPONENT_MAX) result = quiet(result)
    }
    return Half(result.toUShort())
}

fun ceil(x: Half): Half {
    val bits = x.toBits()
    var a = bits and FP16_ABS
    var result = bits
    if (a < 0x3c00) {
        result = result and FP16_SIGN_MASK
        result = result or (0x3c00 and -((bits shr 15).inv() and if (a != 0) 1 else 0))
    } else if (a < 0x6400) {
        a = 25 - (a shr 10)
        val mask = (1 shl a) - 1
        result += mask and (bits shr 15) - 1
        result = result and mask.inv()
    } else {
        if (a > FP16_EXPONENT_MAX) result = quiet(result)
    }
    return Half(result.toUShort())
}

fun truncate(x: Half): Half {
    val bits = x.toBits()
    var a = bits and FP16_ABS
    var result = bits
    if (a < 0x3c00) {
        result = result and FP16_SIGN_MASK
    } else if (a < 0x6400) {
        a = 25 - (a shr 10)
        val mask = (1 shl a) - 1
        result = result and mask.inv()
    } else {
        if (a > FP16_EXPONENT_MAX) result = quiet(result)
    }
    return Half(result.toUShort())
}

private const val FP16_SIGN_SHIFT       = 15
private const val FP16_SIGN_MASK        = 0x8000
private const val FP16_EXPONENT_SHIFT   = 10
private const val FP16_EXPONENT_MASK    = 0x1f
private const val FP16_SIGNIFICAND_MASK = 0x3ff
private const val FP16_EXPONENT_BIAS    = 15
private const val FP16_ABS              = 0x7fff
private const val FP16_EXPONENT_MAX     = 0x7c00
private const val FP16_NAN              = 0x7e00
private const val FP16_QUIET_NAN        = 0x7fff
private const val FP32_SIGN_SHIFT       = 31
private const val FP32_EXPONENT_SHIFT   = 23
private const val FP32_EXPONENT_MASK    = 0xff
private const val FP32_SIGNIFICAND_MASK = 0x7fffff
private const val FP32_EXPONENT_BIAS    = 127
private const val FP32_QNAN_MASK        = 0x400000
private const val FP32_DENORMAL_MAGIC   = 126 shl 23
private       val FP32_DENORMAL_FLOAT   = Float.fromBits(FP32_DENORMAL_MAGIC)

private fun floatToHalf(f: Float): UShort {
    val bits: Int = f.toBits()
    val s = bits ushr FP32_SIGN_SHIFT
    var e = (bits ushr FP32_EXPONENT_SHIFT) and FP32_EXPONENT_MASK
    var m = bits and FP32_SIGNIFICAND_MASK
    var outE = 0
    var outM = 0
    if (e == 0xff) {
        outE = 0x1f
        outM = if (m != 0) 0x200 else 0
    } else {
        e = e - FP32_EXPONENT_BIAS + FP16_EXPONENT_BIAS
        if (e >= 0x1f) {
            outE = 0x1f
            outM = 0
        } else if (e <= 0) {
            if (e < -10) {
            } else {
                val shift = 13 - e
                m = (m or 0x800000)
                val sticky = if (m and ((1 shl (shift - 1)) - 1) != 0) 1 else 0
                val guard = (m ushr (shift - 1)) and 1
                m = (m ushr shift) + (guard and (sticky or (m ushr shift)))
                outM = m
            }
        } else {
            outE = e
            outM = m shr 13
            if (m and 0x1000 != 0) {
                var out = outE shl FP16_EXPONENT_SHIFT or outM
                out++
                return (out or (s shl FP16_SIGN_SHIFT)).toUShort()
            }
        }
    }
    return (s shl FP16_SIGN_SHIFT or (outE shl FP16_EXPONENT_SHIFT) or outM).toUShort()
}

private fun halfToShort(h: UShort): Float {
    val bits = h.toInt()
    val s = bits and FP16_SIGN_MASK
    val e = bits ushr FP16_EXPONENT_SHIFT and FP16_EXPONENT_MASK
    val m = bits and FP16_SIGNIFICAND_MASK
    var outE = 0
    var outM = 0
    if (e == 0) {
        if (m != 0) {
            var o: Float = Float.fromBits(FP32_DENORMAL_MAGIC + m)
            o -= FP32_DENORMAL_FLOAT
            return if (s == 0) o else -o
        }
    } else {
        outM = m shl 13
        if (e == 0x1f) {
            outE = 0xff
            if (outM != 0) {
                outM = outM or FP32_QNAN_MASK
            }
        } else {
            outE = e - FP16_EXPONENT_BIAS + FP32_EXPONENT_BIAS
        }
    }
    val out = (s shl 16) or (outE shl FP32_EXPONENT_SHIFT) or outM
    return Float.fromBits(out)
}

private inline fun quiet(x: Int) = x or 0x200
private inline fun quiet(x: Int, y: Int) =
    (if (x and FP16_ABS > FP16_EXPONENT_MAX) x else y) or 0x200

private fun fixedToHalf(sign: Int, e: Int, m: UInt, s: UInt, fraction: Int): Half {
    val v: UInt
    val S_bit: UInt
    val G_bit: UInt
    if (e < 0) {
        v = sign.toUInt() + (m shr (fraction - 10 - e))
        G_bit = (m shr (fraction - 11 - e)) and 1U
        S_bit = s or (if ((m and ((1U shl (fraction - 11 - e)) - 1U)) != 0U) 1U else 0U)
    } else {
        v = sign.toUInt() + (e.toUInt() shl 10) + (m shr (fraction - 10))
        G_bit = (m shr (fraction - 11)) and 1U
        S_bit = s or (if ((m and ((1U shl (fraction - 11)) - 1U)) != 0U) 1U else 0U)
    }
    return Half((v + (G_bit and (S_bit or v))).toUShort())
}
