@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying cinterop pointer behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaAnimator>? get() = nativeHandle
@InternalFilamentApi
val AssetLoader.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaAssetLoader>? get() = nativeHandle
@InternalFilamentApi
val FilamentAsset.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaFilamentAsset>? get() = nativeHandle
@InternalFilamentApi
val ResourceLoader.nativeObject: kotlinx.cinterop.CPointer<cnames.structs.FilaResourceLoader>? get() = nativeHandle
