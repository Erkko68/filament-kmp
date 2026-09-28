package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the native C handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val AssetLoader.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val FilamentAsset.nativeObject: Long get() = nativeHandle
@InternalFilamentApi
val ResourceLoader.nativeObject: Long get() = nativeHandle
