package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface ViewerContent : JsAny {
var view: View
var renderer: Renderer
var materials: js.array.ReadonlyArray<MaterialInstance>
var lightManager: LightManager
var scene: Scene
var indirectLight: IndirectLight?
var sunlight: Entity
var assetLights: js.array.ReadonlyArray<Entity>
}
