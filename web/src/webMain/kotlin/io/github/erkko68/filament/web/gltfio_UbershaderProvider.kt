package io.github.erkko68.filament.web

@JsName("gltfio\$UbershaderProvider")
external class gltfio_UbershaderProvider(engine: Engine) : JsAny {
fun destroyMaterials(): Unit
fun getMaterialsCount(): Double
fun needsDummyData(attrib: VertexAttribute): Boolean
fun getMaterials(): js.array.ReadonlyArray<Material>
}
