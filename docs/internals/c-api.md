# The Generated C API

Every platform calls the same C API: the `Fila*` functions in [`c/`](../../c). JVM and Android call
it over JNI, iOS calls the symbols directly, and web calls it as wasm. It is **generated from
Filament's public C++ headers**, so the Kotlin API follows C++ (names, owners, overloads, defaults)
and a Filament upgrade is mostly a regenerate. How Kotlin then reaches the C API on each platform
is covered in [Native Bindings](bindings.md).

It is written for contributors. Library users only need to know the result: the Kotlin API matches
[Filament's C++ API](https://google.github.io/filament/) on every platform.

## The pipeline

```
include/ (Filament headers, per filaVersion)
   │  c/api-headers.txt: which headers, which C module, what to skip
   ▼
clang JSON AST ──► C++ API model ───────────► apiModel         (report)
                        │
                        ├──► generateCApi ──► c/<module>/generated/Fila*.{h,cpp}     (committed)
                        │                       + c/<module>/manual/ (hand-written leftovers)
                        │                       └──► c/api-manifest.json: every C function, by header (committed)
                        │                             │
                        │    generateKotlinExternals ◄┤
                        │         └──► kotlin/<module>/src/commonMain/.../capi/*.kt  (committed)
                        │                             │
                        │    generateBindings ◄───────┘  JNI forwarders, wasm export lists  (build/, not committed)
                        │
                        ├──► apiCoverage ──► c/api-coverage.txt: bound in C, called by Kotlin? (committed)
                        └──► apiGaps ──► C++ API the C layer doesn't call               (report)
```

| Task | Reads | Writes |
| :--- | :--- | :--- |
| `./gradlew apiModel` | the headers `c/api-headers.txt` lists | `build/reports/api-model.txt`: the C++ surface clang sees |
| `./gradlew generateCApi` | the same headers + `c/api-headers.txt` + `c/<module>/manual/*.h` | `c/<module>/generated/`, each module's `Types.h`, and `c/api-manifest.json` |
| `./gradlew generateKotlinExternals` | `c/api-manifest.json` | each Kotlin module's `capi` package |
| `./gradlew generateBindings` | `c/api-manifest.json` | `build/generated/bindings/` (runs as part of every native build) |
| `./gradlew apiCoverage` | the headers + `c/` + the Kotlin sources | `c/api-coverage.txt`; warns when `c/` or `capi` is stale |
| `./gradlew apiGaps` | Filament's libraries + the `c/` objects | `build/reports/api-gaps.txt` (macOS and Linux hosts) |

The generated C and Kotlin files are committed. A diff in them is the review surface for an upgrade
or a generator change. Don't edit them by hand: the next run overwrites them.

The generator lives in [`build-logic/…/buildlogic/apigen`](../../build-logic/src/main/kotlin/buildlogic/apigen),
one package per stage (`cpp/`, `c/`, `kotlin/`, `externals/`, `gaps/`).

## Scope: `c/api-headers.txt`

[`c/api-headers.txt`](../../c/api-headers.txt) lists, per C module (`[filament]`, `[gltfio]`,
`[filamat]`, `[filament-utils]`), the header globs that are API. Every public class and free
function they declare gets C functions. Types they only *use* from other headers (`backend/`,
`math/`, `utils/`) are bridged as C types.

The scope is declared, not inferred:

- `!glob` drops a header; `-filament::View::setDebugCamera` drops a declaration (a record with
  everything nested in it, a member, or one overload as `-Record::method\(Type\)`). Each `-` entry is
  a regex matched against the whole qualified name, so `-Record::.*` keeps the handle but none of
  its members.
- Each skip has a comment saying why: debugging aids, engine internals (`getDriver`,
  `getJobSystem`), C++-only sugar another overload covers, upstream's "internal use" blocks.
- The generated header notes every member it skipped. An entry that names nothing fails the
  build, so the list can't go stale across upgrades.

## How C++ maps to C

One mapping per C++ shape, so the same kind of declaration always looks the same in C and in Kotlin:

| C++ | C | Kotlin |
| :--- | :--- | :--- |
| class (`Engine`, `View`, `Builder`) | opaque handle `FilaEngine*`; methods take it as `self` | a class holding the handle |
| value struct (`BloomOptions`, `Engine::Config`) | handle with `_create`/`_destroy` and a getter/setter per field, created with the C++ defaults | a plain class with `var` fields, copied through a handle on each get/set |
| `filament::math` vector/matrix | layout-compatible mirror (`FilaFloat3`, `FilaMat4`) by pointer | `FloatArray`/`DoubleArray` |
| enum | integer-typed C enum (`FilaBloomOptionsBlendMode`) | Kotlin enum, same constants and order |
| overloads | one function each, suffixed by the parameter types that tell them apart (`FilaEngine_destroy_View`) | Kotlin overloads |
| strings, `StaticString` | `const char*` (+ length where C++ takes one) | `String` |
| `Slice`, `FixedCapacityVector`, `std::array`, C arrays | pointer + `uint32_t` count; out-arrays are capacity-bounded | arrays / `List` |
| `std::optional<T>` | parameter: nullable pointer (`NULL` is `nullopt`); result: `bool` return + `out` pointer | nullable |
| `std::function` / callback aliases | C function pointer + `void* user` of the same shape | lambda (`Callbacks` registry) |
| buffer descriptors (uploads) | pointer + size + release callback + user | array + `onRelease` |
| templates | the instantiations a table lists (e.g. `Manipulator<float>`, `constant<int32_t/float/bool>`) | one class / overloads |
| `std::chrono` durations and time points | `int64_t` nanoseconds | `Long` |

Signatures also follow the C ABI rules that let one Kotlin declaration bind on every target (fixed
widths, no 8/16-bit integer parameters, 64-bit results through an out-pointer, no structs by
value); see [Declaring a binding](bindings.md#declaring-a-binding).

Names are `Fila` plus the C++ path without the `filament`/`backend`/`math` namespaces:
`filament::View::getBloomOptions` → `FilaView_getBloomOptions`,
`filament::gltfio::AssetLoader::createAsset` → `FilaGltfioAssetLoader_createAsset`.

## Hand-written leftovers: `c/<module>/manual`

What has no mechanical mapping stays hand-written, and the generated header says so where the
function would have been, with the C++ signature it stands in for:

```c
// handwritten in manual/ FilaScene_forEach: void filament::Scene::forEach(utils::Invocable<void (utils::Entity)> && functor) const
//     utils::Invocable<void (utils::Entity)> &&: C callbacks take at most one pointer
```

Today that is a small set: `Scene::forEach`, `Material::getParameters`, `View::pick`'s callback,
`SwapChain` frame callbacks, a few gltfio/filamat/filament-utils helpers
(`FilaGltfioManual`, `FilaFilamatMaterialBuilderManual`, `FilaUtilsManual`,
`FilaImageKtx1BundleManual`), `FramePacer::VsyncTick::timelines`, `FrameHistoryStream::getNewFrames`, and `TangentSpaceMesh::Builder::aux`. A manual function follows the
same ABI rules and gets its Kotlin external generated the same way. One C++ function can become
several, named `<function>_<case>` (`FilaGeometryTangentSpaceMeshBuilder_aux_float2`, … one per
`std::variant` alternative).

When `generateCApi` meets a declaration it can't bridge and no manual function covers, it leaves a
`TODO(handwritten)` comment with the reason instead of code. Each TODO is either written in
`manual/`, skipped in `api-headers.txt` with a reason, or a generator improvement.

## From C to the Kotlin API

The generated `capi` externals are `internal`: they are the raw C surface, one `external fun` per
`Fila*` function. The public Kotlin API is hand-written on top of them in `commonMain`, one class
per C++ class, and follows the C++ header:

- **Names, owners and defaults are C++'s.** A method lives on the class that declares it in C++,
  keeps its C++ name and takes C++'s default arguments. Nested types stay nested where C++ nests
  them; `Options.h`'s structs are top-level because they are in C++.
- **Kotlin shape where Kotlin has one.** A `getX()`/`setX()` pair is a `var`, a zero-argument
  getter a `val`, a value struct a plain class, a nullable C++ result (a failed `build`, a missing
  flag) a nullable Kotlin type, and a self-destroying type is `AutoCloseable`.
- **No invented API.** A convenience wrapper that doesn't exist in C++ doesn't belong in the
  bindings; it goes in `filament-compose` or your own code.

## Checking coverage: `apiCoverage` and `apiGaps`

`./gradlew apiCoverage` writes `c/api-coverage.txt`, one line per C function the C++ API calls for:
`wrapped` (a public Kotlin wrapper calls it), `unwrapped` (bound, with an internal external, but no wrapper calls it yet), `todo` (to
write by hand, with the reason) or `skipped` (left out on purpose). It's committed, so after an
upgrade `git diff c/api-coverage.txt` shows what upstream added or removed and how far each got;
`grep '^unwrapped'` is the Kotlin backlog. It also warns when the committed `c/*/generated` or `capi`
files aren't what the generators would write now. It never fails the build. Changes to our public
Kotlin API are `apiDump`'s: review the diff in each module's `api/`.

A second section lists what no function carries: each enum value and public class constant of the bound
API, `declared` when the hand-written Kotlin API has that name in the matching class or enum (found
through the API's aliases too: `Texture.InternalFormat` for `backend::TextureFormat`), `undeclared`
when it doesn't, or `untyped` when Kotlin has no declaration of the enum at all. It matches names in the
Kotlin sources, it doesn't compile them; `grep '^undeclared'` after an upgrade shows the new ones.
Ones Kotlin leaves out on purpose are `-` entries in `c/api-headers.txt` (`-Record::[A-Z_]+` for
all of a record's constants), which drops them from the list.

`./gradlew apiGaps` compares Filament's public C++ methods (from clang's AST, inline ones included,
plus template instances only the libraries define) against the symbols the `c/` objects reference
when built at `-O0`, so inline calls stay calls. It reports by symbol, never by parsing names: a
cross-check that doesn't trust the generator's view. Struct fields and enum values aren't covered.
It needs `clang++`, `nm` and `c++filt`, so it runs on macOS and Linux hosts.

## Known limits

- **Native panics abort the process, by design.** A Filament precondition failure (`utils::Panic`),
  such as a wrong-sized buffer or destroying an engine with live objects, is misuse to fix. On JVM
  and Android a panic handler set in `JNI_OnLoad` logs its message and call stack, then aborts:
  Filament built with exceptions doesn't log them itself. iOS's prebuilt has no exceptions and does
  the same on its own. Web throws a JS error with the message. Catching them on every platform would
  need a check after every call from Kotlin/Native, which can't unwind C++. Known bad inputs are
  checked on the Kotlin side first (a non-`.filamat` payload makes `Material.Builder.build` return
  null instead of panicking).
- **API guarded by `#if`** in a header (e.g. `UTILS_HAS_THREADING`) is generated on every target;
  where the condition is false it panics. The Kotlin method then carries `@PlatformGap`, like every
  other per-platform difference (mostly single-threaded wasm; see [Platform Notes](../guide/platform-notes.md#api-coverage)).

## Upstream

Upstream is moving the same way for its own bindings:
[google/filament#10410](https://github.com/google/filament/pull/10410) annotates the headers and
[#10426](https://github.com/google/filament/pull/10426) generates the Android Java from them (in
1.77.2). We don't depend on it, since every platform runs on our C API, but both read the same
headers, so the two stay close.

---

[← Back to docs](../README.md)
