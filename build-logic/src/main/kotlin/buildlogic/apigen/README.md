# apigen

Generates a C API over a C++ library's headers, the Kotlin `external fun`s of that C API, and the JNI and wasm glue
that binds them on each platform. It knows no particular library: a library hands it an
[`ApiGenConfig`](ApiGenConfig.kt) and a `c/` dir, and its build calls `registerApiGenTasks(config)`. Filament's
config is [`FilamentApi.kt`](../filament/FilamentApi.kt).

## Stages

| Package | Reads → writes | Task |
| :--- | :--- | :--- |
| `cpp/` | clang's JSON AST of the API headers → `CppApi`, the C++ model | `apiModel` (report) |
| `c/` | `CppApi` → `c/<module>/generated`; the functions those and `c/<module>/manual` declare → `c/api-manifest.json` | `generateCApi` (committed) |
| `kotlin/` | `api-manifest.json` → each Kotlin module's `capi` package | `generateKotlinExternals` (committed) |
| `bindings/` | `api-manifest.json` → JNI forwarders, wasm export lists and type tables | `generateBindings` (build dir) |
| `report/` | what the C and Kotlin APIs bind, and whether it's right | `apiCoverage`, `apiGaps`, `apiVerify` |

[`ApiGen`](ApiGen.kt) runs each stage on what the one before wrote; the tasks only call it. Everything after `c/`
reads the manifest, not the C++, so a hand-written C function gets its external and glue like a generated one.

## A new library

1. The config:
   - `sources`: the include dir, and the clang `-ast-dump-filter`s covering the API (`ns::` for a whole namespace).
   - `c`: the C prefix, the C++ namespace of the forwarders' helpers, the namespaces C names leave out, and the
     library's own bridge header and typedefs.
   - `naming`: how the library names things, which the generated names follow: `memberPrefix` for `mField`, and the
     verbs of its getters and setters.
   - `types`: how the library's own types cross into C: IDs as scalars, math types as mirrored structs, its
     containers and strings, reference counting, value-or-error results, and a `CustomBridges` object for what no
     table expresses.
   - `templates`: the class and function template instantiations C binds.
   - `kotlin`: the package of each module's externals, the interop package, the wasm runtimes; null for a C API alone.
2. `c/api-headers.txt`:

   ```
   [module]   the C module the headers below generate into; the first is the base the others build on
   glob       API headers, relative to the include dir (`filament/*.h`); `!glob` drops one
   -regex     a declaration left out, matched against its whole qualified name: a record and all it nests, a
              member, or one overload as `Record::method\(Type, …\)`; `Record::.*` keeps only its handle
   ```

   A skip that matches nothing fails the generator, so the list can't go stale.
3. `c/<module>/manual/`: C for what the generator leaves as `TODO(handwritten)`, or writes otherwise: a function
   declared here is never generated. In the first module, the `c.bridgeHeader` holds the helpers only the library
   can write: `retain` for `types.refs`, `result` for `types.results`.
4. The Kotlin runtime, in the interop package: `expect annotation class ExternalSymbolName(val name: String)`
   (`kotlin.native.SymbolName` on Native) and `expect class NativePointer` (a `Long` on the JVM and Native, an `Int`
   on web).

`./gradlew generateKotlinExternals` then writes the C API, its manifest and the externals, and `apiCoverage` shows
what got bound.

## Tests

`./gradlew -p build-logic test` runs every stage on [`src/test/fixture`](../../../../test/fixture), a small library
laid out like this repo whose config ([`GeoApi.kt`](../../../../test/kotlin/buildlogic/apigen/GeoApi.kt)) uses what
Filament's doesn't, and compares the whole tree with the committed output. `-Papigen.update` rewrites it, and the diff
is the review. Like the generator, it needs `clang` and `clang++` on the path.
