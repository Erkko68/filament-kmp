package io.github.erkko68.filament.interop

import io.github.erkko68.filament.IndirectLight
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Skybox

// Bridges to classes whose actuals still hold platform-typed handles; each goes away as its class
// moves to commonMain.

@InternalFilamentApi
internal expect val Skybox.pointer: NativePointer

@InternalFilamentApi
internal expect val IndirectLight.pointer: NativePointer
