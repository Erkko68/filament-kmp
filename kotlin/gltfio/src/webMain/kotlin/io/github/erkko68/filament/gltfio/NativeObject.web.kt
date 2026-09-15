package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying Filament.js object behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: io.github.erkko68.filament.web.gltfio_Animator get() = jsAnimator
@InternalFilamentApi
val AssetLoader.nativeObject: io.github.erkko68.filament.web.gltfio_AssetLoader get() = jsLoader
@InternalFilamentApi
val FilamentAsset.nativeObject: io.github.erkko68.filament.web.gltfio_FilamentAsset get() = jsAsset
@InternalFilamentApi
val FilamentInstance.nativeObject: io.github.erkko68.filament.web.gltfio_FilamentInstance get() = jsInstance
