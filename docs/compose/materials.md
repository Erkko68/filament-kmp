# Materials

How to author, compile, load, and parameterise Filament materials with `filament-compose`.

A "material" in Filament is a precompiled shader package (`.filamat`) describing how a surface reacts to light: PBR LIT, UNLIT, refractive, cloth, subsurface, etc. You author the surface logic in a small `.mat` source file, compile it with the `matc` tool, and ship the resulting `.filamat` blob with your app. At runtime, you load the blob into a [`Material`](../../kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/Material.kt) and create one or more [`MaterialInstance`](../../kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/MaterialInstance.kt) objects from it, each with its own parameter values.

For the full picture of the Filament material system — surface shading model, parameter types, the `matc` flag reference — read Filament's upstream docs:

- **[Materials](https://google.github.io/filament/Materials.md.html)** — full reference for the `.mat` DSL and `matc` compiler.
- **[Material Properties](https://google.github.io/filament/notes/material_properties.html)** — semantic meaning of `baseColor`, `roughness`, `metallic`, `emissive`, `normal`, etc.
- **[Filament Engine](https://google.github.io/filament/Filament.md.html)** — PBR theory and how the material model fits into the render pipeline.

This page covers what's specific to `filament-compose`: where to put files, which helper to call, and which workflow to pick.

## Workflow: precompiled `.filamat` (recommended)

Compile your `.mat` ahead of time with `matc` and load the resulting binary at runtime. This is the **default recommended path** for every target and the only path that works on Web.

### 1. Author the `.mat` source

```glsl
// lit_color.mat
material {
    name : LitColor,
    shadingModel : lit,
    parameters : [
        { type : float3, name : baseColor }
    ]
}

fragment {
    void material(inout MaterialInputs material) {
        prepareMaterial(material);
        material.baseColor.rgb = materialParams.baseColor;
        material.roughness = 0.45;
        material.metallic  = 0.0;
    }
}
```

Place it in your shared resources, e.g. `samples/shared/src/commonMain/composeResources/files/materials/lit_color.mat`.

### 2. Compile with `matc`

```bash
matc -p all -a all -o lit_color.filamat lit_color.mat
```

- `-p all` — generate for desktop *and* mobile platforms.
- `-a all` — generate for OpenGL, Vulkan, Metal *and* WebGL backends.
- `-o` — output `.filamat` path.

Drop the `.filamat` next to the `.mat` (or wherever your Compose resources live). The `.mat` itself doesn't need to ship — only the compiled blob.

Get `matc`: it ships inside Filament's prebuilt release archives — not as a standalone download. Go to **[github.com/google/filament/releases](https://github.com/google/filament/releases)**, grab the tarball matching your host OS (e.g. `filament-v1.x.y-mac.tgz`, `…-linux.tgz`, `…-windows.tgz`), and you'll find `matc` (along with `cmgen`, `gltf_viewer`, and other tools) under `bin/`. Add that `bin/` to your `PATH` or invoke `matc` by absolute path.

The version of `matc` should match the Filament version this library bundles. Check `gradle/libs.versions.toml` → `filamentVersion` and download the same release tag.

### 3. Load at runtime

```kotlin
val mat: Material? = rememberMaterial {
    Res.readBytes("files/materials/lit_color.filamat")
}
mat?.let { template ->
    val instance = rememberMaterialInstance(template).apply {
        setParameter("baseColor", 0.9f, 0.3f, 0.3f)
    }
    Cube(material = instance, position = Position(0f))
}
```

`rememberMaterial { ... }` returns `null` while the bytes are being read — wrap the consumer in `?.let { … }` or guard early. The underlying `Material` is destroyed automatically when the composable leaves the composition.

### 4. Parameterise per instance

A single compiled `Material` can back any number of `MaterialInstance`s, each with different parameter values. Parameter names come from the `parameters: [ ... ]` block of your `.mat` source:

```kotlin
val template = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") } ?: return
val red   = rememberMaterialInstance(template, "red")  .apply { setParameter("baseColor", 0.9f, 0.2f, 0.2f) }
val blue  = rememberMaterialInstance(template, "blue") .apply { setParameter("baseColor", 0.2f, 0.5f, 0.9f) }
val gold  = rememberMaterialInstance(template, "gold") .apply { setParameter("baseColor", 1.0f, 0.85f, 0.3f) }
```

The extra string keys on `rememberMaterialInstance` only matter if you want a fresh instance whenever the key changes; for static colour assignments any unique key works.

### Common parameter types

| `.mat` type   | Kotlin setter signature                               |
| :---          | :---                                                  |
| `float`       | `setParameter(name, Float)`                           |
| `float3`      | `setParameter(name, r, g, b)`                         |
| `float4`      | `setParameter(name, x, y, z, w)`                      |
| `int`, `bool` | `setParameter(name, Int)` / `setParameter(name, Boolean)` |
| `sampler2d`   | `setParameter(name, texture, TextureSampler())`       |

The full list of types and shading model fields is in [Materials.md.html](https://google.github.io/filament/Materials.md.html#materialdefinitions/parametersblock).

## Workflow: runtime compilation with `filamat`

The `filamat` artifact compiles `.mat` source into `.filamat` bytes *at runtime*. Use it only when:

- You're iterating on shaders during development and don't want to round-trip through `matc` on every change.
- You need to generate shader code dynamically from user input or a configuration file.

```kotlin
val package = MaterialBuilder()
    .name("LitColor")
    .platform(MaterialBuilder.Platform.ALL)
    .targetApi(MaterialBuilder.TargetApi.ALL)
    .shading(MaterialBuilder.Shading.LIT)
    .uniformParameter(MaterialBuilder.UniformType.FLOAT3, "baseColor")
    .material("void material(inout MaterialInputs m) { prepareMaterial(m); m.baseColor.rgb = materialParams.baseColor; }")
    .build()
val template = Material.Builder().payload(package.getBuffer()).build(engine)
```

> [!WARNING]
> `filamat` is **not available on the Web target** — the underlying compiler isn't included in the Filament.js prebuilt. Calls to `MaterialBuilder` on JS throw `UnsupportedOperationException`. See [Platform Notes — Web](../platform-notes.md#web--wasm) for the JS-target API limitations.

> [!TIP]
> Runtime compilation also adds ~5–15 MB to the binary (the `filamat` library bundles the shader compiler), and the first build of each material costs a few hundred milliseconds of CPU time. Prefer precompiled `.filamat` for production builds.

## Updating parameters live

A `MaterialInstance`'s parameters are mutable — you can update them every frame without rebuilding anything:

```kotlin
val instance = rememberMaterialInstance(template)

SideEffect {
    // Driven by Compose state — re-runs on recomposition, no GPU work beyond a tiny uniform upload.
    instance.setParameter("baseColor", state.r, state.g, state.b)
}
```

For per-frame updates that aren't state-driven, use `FilamentEffect { onFrame { ... } }` and call `setParameter` from the frame callback.

> [!WARNING]
> **Don't destroy a `MaterialInstance` that's still referenced by a renderable.** If you want to recolour a primitive that's already in the scene, update its parameters in place — don't allocate a new instance and swap it. Filament's render thread can crash when it reads from a dangling instance pointer between the swap and the next frame. The `rememberColorInstance` pattern used in the samples is fine for the case where the renderable is *also* recreated whenever the colour changes (i.e. the colour is a `remember(...)` key on both).

## Texturing

`rememberTexture { … }` loads any image format the platform's `TextureLoader` supports (PNG / JPEG / KTX1 on every backend). Bind it to a `sampler2d` parameter via:

```kotlin
val tex = rememberTexture(type = TextureLoader.TextureType.COLOR) {
    Res.readBytes("files/textures/wood_albedo.png")
}
val instance = rememberMaterialInstance(template)
SideEffect {
    tex?.let { instance.setParameter("albedo", it, TextureSampler()) }
}
```

For PBR work flow conventions (sRGB vs linear, normal map encoding, ORM packing), see [Material Properties](https://google.github.io/filament/notes/material_properties.html).

## Reference

- [`filament-compose` overview](README.md) — full component reference table.
- [Platform Notes — Web](../platform-notes.md#web--wasm) — what does and doesn't work in the JS target.
- Upstream Filament:
  - [Materials](https://google.github.io/filament/Materials.md.html)
  - [Material Properties](https://google.github.io/filament/notes/material_properties.html)
  - [Filament Engine](https://google.github.io/filament/Filament.md.html)
  - [`matc` releases](https://github.com/google/filament/releases)

---

[← Back to Compose Documentation](README.md)
