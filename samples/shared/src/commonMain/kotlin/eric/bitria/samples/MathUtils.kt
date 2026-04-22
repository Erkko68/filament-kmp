package eric.bitria.samples

import kotlin.math.cos
import kotlin.math.sin

fun translation(x: Double, y: Double, z: Double): DoubleArray {
    return doubleArrayOf(
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
          x,   y,   z, 1.0
    )
}

fun scaling(s: Double): DoubleArray {
    return doubleArrayOf(
          s, 0.0, 0.0, 0.0,
        0.0,   s, 0.0, 0.0,
        0.0, 0.0,   s, 0.0,
        0.0, 0.0, 0.0, 1.0
    )
}

fun identity(): DoubleArray {
    return doubleArrayOf(
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0,
        0.0, 0.0, 0.0, 1.0
    )
}

fun rotationY(angle: Double): DoubleArray {
    val c = cos(angle)
    val s = sin(angle)
    return doubleArrayOf(
        c, 0.0, -s, 0.0,
        0.0, 1.0, 0.0, 0.0,
        s, 0.0, c, 0.0,
        0.0, 0.0, 0.0, 1.0
    )
}

fun multiplyMatrices(a: DoubleArray, b: DoubleArray, result: DoubleArray) {
    val tmp = DoubleArray(16)
    for (i in 0..3) {
        for (j in 0..3) {
            var sum = 0.0
            for (k in 0..3) sum += a[i + k * 4] * b[k + j * 4]
            tmp[i + j * 4] = sum
        }
    }
    for (i in 0..15) result[i] = tmp[i]
}
