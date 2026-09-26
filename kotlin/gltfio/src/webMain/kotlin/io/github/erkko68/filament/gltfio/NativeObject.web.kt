package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val AssetLoader.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val FilamentAsset.nativeObject: Int get() = nativeHandle
@InternalFilamentApi
val ResourceLoader.nativeObject: Int get() = nativeHandle
