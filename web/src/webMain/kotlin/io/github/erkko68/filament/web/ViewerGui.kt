// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class ViewerGui : JsAny {
constructor (engine: Engine, scene: Scene, view: View, sidebarWidth: Double)
fun renderUserInterface(timeStepInSeconds: Double, guiView: View, pixelRatio: Double): Unit
fun getSettings(): viewer_Settings
fun mouseEvent(mouseX: Double, mouseY: Double, mouseButton: Boolean, mouseWheelY: Double, control: Boolean): Unit
fun keyDownEvent(keyCode: Double): Unit
fun keyUpEvent(keyCode: Double): Unit
fun keyPressEvent(charCode: Double): Unit
fun delete(): Unit
}
