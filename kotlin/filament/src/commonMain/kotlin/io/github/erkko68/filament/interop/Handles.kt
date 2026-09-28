package io.github.erkko68.filament.interop

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.Texture

// Bridges to classes whose actuals still hold platform-typed handles; each goes away as its class
// moves to commonMain.

@InternalFilamentApi
internal expect val Engine.pointer: NativePointer

@InternalFilamentApi
internal expect val Texture.pointer: NativePointer

@InternalFilamentApi
internal expect fun textureOf(ptr: NativePointer): Texture
