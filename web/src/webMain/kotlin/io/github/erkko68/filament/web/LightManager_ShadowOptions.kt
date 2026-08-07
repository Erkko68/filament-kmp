package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface LightManager_ShadowOptions_Vsm : JsAny {
var elvsm: Boolean?
var blurWidth: Double?
}

external interface LightManager_ShadowOptions : JsAny {
var mapSize: Double?
var shadowCascades: Double?
var cascadeSplitPositions: js.array.ReadonlyArray<JsNumber>?
var vsm: LightManager_ShadowOptions_Vsm?
var constantBias: Double?
var normalBias: Double?
var shadowFar: Double?
var shadowNearHint: Double?
var shadowFarHint: Double?
var stable: Boolean?
var polygonOffsetConstant: Double?
var polygonOffsetSlope: Double?
var screenSpaceContactShadows: Boolean?
var stepCount: Double?
var maxShadowDistance: Double?
var lispsm: Boolean?
var shadowBulbRadius: Double?
var penumbraScale: Double?
var penumbraRatioScale: Double?
var maxPenumbraRatio: Double?
var maxSearchRadius: Double?
var transform: quat?
}
