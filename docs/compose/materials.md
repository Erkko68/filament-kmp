# Materials

How to author, compile, load, and parameterise Filament materials with `filament-compose`.

A "material" in Filament is a precompiled shader package (`.filamat`) describing how a surface reacts to light: PBR LIT, UNLIT, refractive, cloth, subsurface, etc. You author the surface logic in a small `.mat` source file, compile it with the `matc` tool, and ship the resulting `.filamat` blob with your app. At runtime, you load the blob into a [`Material`](../../kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/Material.kt) and create one or more [`MaterialInstance`](../../kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/MaterialInstance.kt) objects from it, each with its own parameter values.

For the full picture of the Filament material system — surface shading model, parameter types, the `matc` flag reference — read Filament's upstream docs:

- **[Materials](https://google.github.io/filament/Materials.md.html)** — full reference for the `.mat` DSL and `matc` compiler.
- **[Material Properties](https://google.github.io/filament/notes/material_properties.html)** — semantic meaning of `baseColor`, `roughness`, `metallic`, `emissive`, `normal`, etc.
- **[Filament Engine](https://google.github.io/filament/Filament.md.html)** — PBR theory and how the material model fits into the render pipeline.

This page covers what's specific to `filament-compose`: where to put files, which helper to call, and which workflow to pick.

## Built-in standard materials (no `matc`, no asset shipping)

For the common cases — a solid colour, a texture, a glow — `filament-compose` ships precompiled
materials so you don't author a `.mat`, run `matc`, or ship a `.filamat` at all. They work on every
target, **including Web** (where runtime material compilation isn't available). Each helper returns a
ready `MaterialInstance` you drop straight into a primitive:

```kotlin
// LIT solid colour (baseColor / metallic / roughness / reflectance)
Cube(material = rememberColorMaterialInstance(Color(0.9f, 0.25f, 0.3f)))

// UNLIT flat colour — ignores scene lighting
Plane(material = rememberUnlitColorMaterialInstance(Color(0.1f, 0.1f, 0.12f)))

// LIT textured base colour (geometry needs uv0 — all built-in primitives supply it)
val tex = rememberTexture { Res.readBytes("files/textures/wood.png") }
tex?.let { Sphere(material = rememberTexturedMaterialInstance(it)) }

// UNLIT emissive — glows through bloom when intensity > 1
Sphere(material = rememberEmissiveMaterialInstance(Color(1f, 0.85f, 0.4f), intensity = 4f))

// LIT with alpha transparency — pre-multiplied, two-pass so solids self-composite correctly
Sphere(material = rememberTransparentColorMaterialInstance(Color(0.3f, 0.6f, 0.9f), alpha = 0.35f))
```

Parameters track Compose state automatically — pass a new `color`/`intensity` and the instance is
updated in place, no `SideEffect` needed. The base [`Material`](../../kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/Material.kt)
behind each type is built once and shared across every helper call within a `rememberFilamentScene`,
then freed with the scene. To instantiate one yourself, use `rememberStandardMaterial(StandardMaterial.Lit)`.

The built-ins cover the 90% case. For anything they don't (cloth, subsurface, refraction, custom
shading, extra parameters) author your own `.mat` and use the precompiled workflow below — these
built-ins are exactly that workflow, done for you.

## Workflow: precompiled `.filamat` (recommended for custom materials)

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
val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
val instance = rememberMaterialInstance(mat) {
    setParameter("baseColor", 0.9f, 0.3f, 0.3f)
}
Cube(material = instance, position = Position(0f))
```

`rememberMaterial { ... }` returns `null` while the bytes are being read, and the whole chain
tolerates it: `rememberMaterialInstance` passes the null through and primitives simply don't
render until the material arrives — no unwrapping needed. The underlying `Material` is destroyed
automatically when the composable leaves the composition.

### 4. Parameterise per instance

A single compiled `Material` can back any number of `MaterialInstance`s, each with different parameter values. Parameter names come from the `parameters: [ ... ]` block of your `.mat` source:

```kotlin
val template = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
val red  = rememberMaterialInstance(template) { setParameter("baseColor", 0.9f, 0.2f, 0.2f) }
val blue = rememberMaterialInstance(template) { setParameter("baseColor", 0.2f, 0.5f, 0.9f) }
val gold = rememberMaterialInstance(template) { setParameter("baseColor", 1.0f, 0.85f, 0.3f) }
```

Each call site owns one instance; add keys after `template` when the `configure` block reads
Compose state that should re-apply on change.

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

### Declaratively (recommended)

The keyed `rememberMaterialInstance` overload re-applies a `configure` block whenever any key changes,
so parameters follow Compose state with no `SideEffect`. The same instance is updated in place — never
swapped — so it's safe to keep referenced by a renderable:

```kotlin
val template = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
val instance = rememberMaterialInstance(template, color) {
    setParameter("baseColor", color)   // re-runs whenever `color` changes
}
Cube(material = instance)
```

`configure` runs once on creation and again on every key change. `setParameter` also accepts a
[`Color`](../../kotlin/filament-compose/src/commonMain/kotlin/io/github/erkko68/filament/compose/scene/Types.kt)
directly (a `float3`). The built-in helpers (`rememberColorMaterialInstance`, …) are thin wrappers
over this overload.

### Imperatively

A `MaterialInstance`'s parameters are mutable — you can also update them every frame:

```kotlin
val instance = rememberMaterialInstance(template)

SideEffect {
    // Driven by Compose state — re-runs on recomposition, no GPU work beyond a tiny uniform upload.
    instance?.setParameter("baseColor", state.r, state.g, state.b)
}
```

For per-frame updates that aren't state-driven, use `FilamentEffect { onFrame { ... } }` and call `setParameter` from the frame callback.

> [!WARNING]
> **Don't destroy a `MaterialInstance` that's still referenced by a renderable.** If you want to recolour a primitive that's already in the scene, update its parameters in place — the keyed `rememberMaterialInstance` overload (or the built-in helpers) does exactly this. Don't allocate a new instance and swap it: Filament's render thread can crash when it reads from a dangling instance pointer between the swap and the next frame.

## Texturing

`rememberTexture { … }` loads any image format the platform's `TextureLoader` supports (PNG / JPEG / KTX1 on every backend). Bind it to a `sampler2d` parameter via:

```kotlin
val tex = rememberTexture(type = TextureLoader.TextureType.COLOR) {
    Res.readBytes("files/textures/wood_albedo.png")
}
val instance = rememberMaterialInstance(template, tex) {
    tex?.let { setParameter("albedo", it, TextureSampler()) }
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
