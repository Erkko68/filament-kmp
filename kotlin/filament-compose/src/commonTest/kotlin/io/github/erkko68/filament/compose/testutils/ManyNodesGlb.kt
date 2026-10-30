package io.github.erkko68.filament.compose.testutils

/** A glb of [nodes] nodes, each with its own triangle mesh and material: its load writes commands in proportion. */
fun manyNodesGlb(nodes: Int): ByteArray {
    val json = buildString {
        append("""{"asset":{"version":"2.0"},"scene":0,"scenes":[{"nodes":[""")
        append((0 until nodes).joinToString(","))
        append("""]}],"nodes":[""")
        append((0 until nodes).joinToString(",") { """{"mesh":$it}""" })
        append("""],"meshes":[""")
        append((0 until nodes).joinToString(",") {
            """{"primitives":[{"attributes":{"POSITION":0,"NORMAL":1,"TEXCOORD_0":2},"indices":3,"material":$it}]}"""
        })
        append("""],"materials":[""")
        append((0 until nodes).joinToString(",") { """{"pbrMetallicRoughness":{"baseColorFactor":[${it % 7 / 7f},0.5,0.3,1]}}""" })
        append("""],"accessors":[""")
        append("""{"bufferView":0,"componentType":5126,"count":3,"type":"VEC3","min":[0,0,0],"max":[1,1,0]},""")
        append("""{"bufferView":1,"componentType":5126,"count":3,"type":"VEC3"},""")
        append("""{"bufferView":2,"componentType":5126,"count":3,"type":"VEC2"},""")
        append("""{"bufferView":3,"componentType":5123,"count":3,"type":"SCALAR"}""")
        append("""],"bufferViews":[""")
        append("""{"buffer":0,"byteOffset":0,"byteLength":36},{"buffer":0,"byteOffset":36,"byteLength":36},""")
        append("""{"buffer":0,"byteOffset":72,"byteLength":24},{"buffer":0,"byteOffset":96,"byteLength":6}""")
        append("""],"buffers":[{"byteLength":104}]}""")
    }.encodeToByteArray().let { it + ByteArray(-it.size and 3) { ' '.code.toByte() } }

    val floats = floatArrayOf(
        0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 0f, // positions
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, // normals
        0f, 0f, 1f, 0f, 0f, 1f, // UVs
    )
    val bin = ByteArray(104)
    floats.forEachIndexed { i, f -> bin.putLe(i * 4, f.toRawBits()) }
    listOf(0, 1, 2).forEachIndexed { i, index -> bin[96 + i * 2] = index.toByte() }

    val glb = ByteArray(12 + 8 + json.size + 8 + bin.size)
    glb.putLe(0, 0x46546C67) // "glTF"
    glb.putLe(4, 2)
    glb.putLe(8, glb.size)
    glb.putLe(12, json.size)
    glb.putLe(16, 0x4E4F534A) // "JSON"
    json.copyInto(glb, 20)
    glb.putLe(20 + json.size, bin.size)
    glb.putLe(24 + json.size, 0x004E4942) // "BIN"
    bin.copyInto(glb, 28 + json.size)
    return glb
}

private fun ByteArray.putLe(offset: Int, value: Int) = repeat(4) { this[offset + it] = (value shr (it * 8)).toByte() }
