package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("Camutils\$Manipulator")
external class Camutils_Manipulator : JsAny {
fun setViewport(width: Double, height: Double): Unit
fun getLookAt(eye: float3, target: float3, up: float3): Unit
fun raycast(x: Double, y: Double, result: float3): Boolean
fun getRay(x: Double, y: Double, origin: float3, dir: float3): Unit
fun grabBegin(x: Double, y: Double, strafe: Boolean): Unit
fun grabUpdate(x: Double, y: Double): Unit
fun grabEnd(): Unit
fun keyDown(key: Camutils_Key): Unit
fun keyUp(key: Camutils_Key): Unit
fun scroll(x: Double, y: Double, scrolldelta: Double): Unit
fun update(deltaTime: Double): Unit
fun getCurrentBookmark(): Camutils_Bookmark
fun getHomeBookmark(): Camutils_Bookmark
fun jumpToBookmark(bookmark: Camutils_Bookmark): Unit
fun attach(canvas: org.w3c.dom.HTMLCanvasElement): Unit
fun detach(canvas: org.w3c.dom.HTMLCanvasElement): Unit
}
