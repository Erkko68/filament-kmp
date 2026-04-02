package dev.filament.kmp

expect object MathUtils {
    /**
     * Packs the tangent frame represented by the specified tangent, bitangent, and normal into a
     * quaternion.
     *
     * <p>
     * Reflection is preserved by encoding it as the sign of the w component in the resulting
     * quaternion. Since -0 cannot always be represented on the GPU, this function computes a bias
     * to ensure values are always either positive or negative, never 0. The bias is computed based
     * on a per-element storage size of 2 bytes, making the resulting quaternion suitable for
     * storage into an SNORM16 vector.
     * </p>
     *
     * @param tangentX the X component of the tangent
     * @param tangentY the Y component of the tangent
     * @param tangentZ the Z component of the tangent
     * @param bitangentX the X component of the bitangent
     * @param bitangentY the Y component of the bitangent
     * @param bitangentZ the Z component of the bitangent
     * @param normalX the X component of the normal
     * @param normalY the Y component of the normal
     * @param normalZ the Z component of the normal
     * @param quaternion a float array of at least size 4 for the quaternion result to be stored
     */
    fun packTangentFrame(
        tangentX: Float,
        tangentY: Float,
        tangentZ: Float,
        bitangentX: Float,
        bitangentY: Float,
        bitangentZ: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        quaternion: FloatArray,
    )

    /**
     * Packs the tangent frame represented by the specified tangent, bitangent, and normal into a
     * quaternion.
     *
     * <p>
     * Reflection is preserved by encoding it as the sign of the w component in the resulting
     * quaternion. Since -0 cannot always be represented on the GPU, this function computes a bias
     * to ensure values are always either positive or negative, never 0. The bias is computed based
     * on a per-element storage size of 2 bytes, making the resulting quaternion suitable for
     * storage into an SNORM16 vector.
     * </p>
     *
     * @param tangentX the X component of the tangent
     * @param tangentY the Y component of the tangent
     * @param tangentZ the Z component of the tangent
     * @param bitangentX the X component of the bitangent
     * @param bitangentY the Y component of the bitangent
     * @param bitangentZ the Z component of the bitangent
     * @param normalX the X component of the normal
     * @param normalY the Y component of the normal
     * @param normalZ the Z component of the normal
     * @param quaternion a float array of at least size 4 for the quaternion result to be stored
     * @param offset offset, in elements, into the quaternion array to store the results
     */
    fun packTangentFrame(
        tangentX: Float,
        tangentY: Float,
        tangentZ: Float,
        bitangentX: Float,
        bitangentY: Float,
        bitangentZ: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float,
        quaternion: FloatArray,
        offset: Int,
    )
}

