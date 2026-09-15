package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.InternalFilamentApi

// Escape hatch: the underlying FFM handle behind each wrapper, for interop with code
// that talks to Filament directly. Read-only — the wrapper owns the object's lifetime.

@InternalFilamentApi
val Animator.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val AssetLoader.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val FilamentAsset.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val FilamentInstance.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
@InternalFilamentApi
val ResourceLoader.nativeObject: java.lang.foreign.MemorySegment? get() = nativeHandle
