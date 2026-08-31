package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying filament-android object behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: com.google.android.filament.gltfio.Animator get() = nativeObject
@InternalFilamentApi
val AssetLoader.nativeObject: com.google.android.filament.gltfio.AssetLoader get() = nativeObject
@InternalFilamentApi
val FilamentAsset.nativeObject: com.google.android.filament.gltfio.FilamentAsset get() = nativeObject
