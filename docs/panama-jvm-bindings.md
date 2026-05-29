# Project Panama: Replacing JNI with the C Wrapper

> Status: **design proposal**, no code on this branch yet. This document is the
> analysis that should land before any implementation.

This document is a full investigation of the proposal to delete the entire
`java/` JNI stack and replace it with Project Panama (Foreign Function & Memory
API) calls into the existing C wrapper (`c/`). It focuses heavily on the parts
the original sketch glossed over: **how native prebuilts are downloaded, linked,
and bundled**, because that is where most of the real work lives.

---

## TL;DR

The proposal is sound. The Kotlin layer changes are mostly mechanical and end
up looking nearly identical to the existing `nativeMain` cinterop bindings.
The non-trivial work is on the **build / packaging** side:

1. The C wrapper today is **static-only** (4 `.a` archives consumed by Kotlin/Native
   cinterop). For Panama we need a **single combined SHARED library**
   (`libfilament-c.{dylib,so,dll}`) that links all four C-wrapper archives and
   all of their Filament static dependencies into one image.
2. The reason it must be **one** combined shared library — not four — is the same
   reason `java/CMakeLists.txt` already combines its four JNI submodules into a
   single `libfilament-jni`: Filament has process-global singletons (notably
   `EntityManager`) that get duplicated if the static archives are linked into
   more than one shared library, leading to silent corruption when entities
   created in one cross another.
3. Prebuilts download flow is **unchanged** — the same `prebuilts/{target}/lib/`
   tree feeds the new SHARED build that today feeds the JNI build.
4. CI publishing **does** change: today `:java:filament` collects per-host
   artifacts via `-PjniArtifactsDir=…`. The same scheme is reused, swapping
   `libfilament-jni.*` for `libfilament-c.*`.
5. The JDK floor moves from 17 to **22** (FFM API was finalized in JDK 22).
   This is the biggest non-technical cost of the proposal — it affects every
   downstream Compose Desktop consumer.

---

## 1. Where the JNI stack lives today (so we know exactly what dies)

```
filament-kmp/
├── prebuilts/
│   ├── iosArm64/lib/        ←— Filament static archives, per target
│   ├── iosSimulatorArm64/lib/
│   ├── iosX64/lib/
│   ├── macosArm64/lib/      ←— used **only** by :java:filament today
│   └── web/                 ←— filament.js + .wasm
├── include/                 ←— Filament C++ headers (shared by all targets)
├── c/                       ←— C wrapper (4 static libs)
│   ├── CMakeLists.txt
│   ├── filament/{c,cpp}/    ←— FilaEngine_*, FilaView_*, …
│   ├── filamat/{c,cpp}/
│   ├── filament-utils/{c,cpp}/
│   └── gltfio/{c,cpp}/
├── java/                    ←— ENTIRE TREE deleted by Panama
│   ├── CMakeLists.txt       ←— combined SHARED build → libfilament-jni
│   ├── filament/
│   │   ├── build.gradle.kts ←— packages dylib into resources/natives/…
│   │   └── src/main/
│   │       ├── java/io/github/erkko68/filament/jni/   (~40 files)
│   │       └── cpp/                                    (~30 files)
│   ├── filamat/
│   ├── gltfio/
│   └── filament-utils/
└── kotlin/
    └── filament/
        ├── build.gradle.kts ←— `jvmMain` depends on `:java:filament`
        └── src/
            ├── commonMain/  ←— expect declarations (unchanged)
            ├── jvmMain/     ←— wraps JNI Java types (rewritten)
            ├── nativeMain/  ←— cinterop (unchanged)
            ├── androidMain/ ←— delegates to filament-android AAR (unchanged)
            └── jsMain/      ←— Filament.js externals (unchanged)
```

Key files to read alongside this proposal:

- [c/CMakeLists.txt](../c/CMakeLists.txt) — the four `STATIC` C-wrapper targets
  (`filament-c`, `filamat-c`, `filament-utils-c`, `gltfio-c`).
- [java/CMakeLists.txt](../java/CMakeLists.txt) — the *combined* SHARED build
  that exists precisely because shipping multiple Filament-linking dylibs caused
  EntityManager corruption (see comment block at L80–L83).
- [java/filament/build.gradle.kts](../java/filament/build.gradle.kts) — how
  CMake output is staged into JAR resources under `natives/{platform}-{arch}/`.
- [buildSrc/src/main/kotlin/FilamentNative.kt](../buildSrc/src/main/kotlin/FilamentNative.kt)
  — the Kotlin/Native variant: CMake + cinterop static-archive packing per
  Konan target.
- [java/filament/src/main/java/io/github/erkko68/filament/jni/internal/NativeLoader.java](../java/filament/src/main/java/io/github/erkko68/filament/jni/internal/NativeLoader.java)
  — extracts the dylib from the JAR to a tmpfile and `System.load`s it.
- [scripts/gradle/download_filament_prebuilts.py](../scripts/gradle/download_filament_prebuilts.py)
  — `host` pseudo-target already exists for the JNI flow.
- [build.gradle.kts](../build.gradle.kts) (root) — `downloadPrebuilts_<target>`
  task fan-out; `PREBUILT_TARGETS` already lists every host we'd need.

---

## 2. The Kotlin rewrite (the cheap part)

Roughly 48 files under [kotlin/filament/src/jvmMain](../kotlin/filament/src/jvmMain),
plus equivalents in `kotlin/{filamat,gltfio,filament-utils}/src/jvmMain`, get
rewritten. None of them contain real logic today — they all delegate one-to-one
to a Java JNI class. After Panama they delegate one-to-one to a `MethodHandle`.

The result looks **nearly identical** to `nativeMain` (`Engine.native.kt` &
friends). That is the point: both call the same C ABI; only the calling
convention differs (`MemorySegment` vs `CPointer<FilaEngine>`).

For the binding object itself, two options:

| | Hand-written `FilamentC.kt` | `jextract` generated Java |
|-|-|-|
| Output language | Kotlin | Java (Kotlin calls it fine) |
| Style match | Mirrors cinterop signatures | Mechanical, one class per header |
| Maintenance | Manual mirror of C headers | Re-run when headers change |
| Volume | ~150 functions × ~6 lines = ~1k lines | Many thousands of LOC |
| Customisation (overloads, default args) | Free | Need a thin Kotlin wrapper anyway |

**Recommendation: hand-write `FilamentC.kt` for `filament-c`, mirroring the
cinterop `.def` already in use.** It will be smaller and read like the
nativeMain bindings. `jextract` is fine as a one-shot bootstrap to copy
descriptors from but not as a live build step.

### 2.1 Callbacks

The current JNI infrastructure includes `VirtualMachineEnv`, `CallbackUtils`,
`JniCallback` and a dedicated handler thread to safely call back into the JVM
from Filament's worker threads.

Panama replaces this with `Linker.upcallStub(target, descriptor, arena)`. Two
things to be careful about:

- **Stub lifetime.** The upcall stub is only valid as long as its `Arena` is
  alive. For one-shot callbacks (e.g. inside a single `readPixels` call) a
  confined arena around the call site works. For long-lived callbacks (e.g.
  per-renderer frame-completed handlers) the stub must live in a long-lived
  arena attached to the owning Kotlin object.
- **Thread attachment.** Panama upcalls do not require manual
  `AttachCurrentThread` — the JVM handles it transparently. That removes
  `VirtualMachineEnv` and its thread-local cache entirely.

### 2.2 Direct buffers

`NioUtils.cpp` exists today to extract the native address from a direct
`ByteBuffer`. Panama replaces it with `MemorySegment.ofBuffer(byteBuffer)`,
which returns a segment backed by the same off-heap memory — zero copy. For
on-heap arrays we'd allocate a confined arena and `allocateFrom(JAVA_BYTE, …)`
to copy, same as JNI does today via `GetByteArrayElements`.

### 2.3 Structs

`FilaEngine::Config` (18 fields) is currently flattened into 18 JNI parameters.
Panama lets us mirror the C struct as a `MemoryLayout.structLayout(…)` and
write fields with `VarHandle`s — one off-heap allocation, no parameter
flattening. Same approach for the few other struct-passing surfaces
(`FrameRateOptions`, `ClearOptions`, etc.).

---

## 3. The C-wrapper build (the expensive part)

This is the section the original sketch was vague about. Let me unpack each
change explicitly.

### 3.1 Today: four STATIC libraries

[c/CMakeLists.txt](../c/CMakeLists.txt) builds four `add_library(… STATIC …)`
targets, each linking only the Filament static archives it directly needs:

| Target | Sources | Filament deps it links against |
|--------|---------|-------------------------------|
| `filament-c` | `c/filament/cpp/*.cpp` | filament, backend, utils, geometry, ibl-lite, filaflat, filabridge, zstd, smol-v |
| `filamat-c` | `c/filamat/cpp/MaterialBuilder.cpp` | filamat, filament, backend, filabridge, shaders, smol-v, utils |
| `filament-utils-c` | `c/filament-utils/cpp/*.cpp` | filament-c, iblprefilter, ktxreader, camutils, image, imageio-lite, zstd, utils, filaflat, filabridge |
| `gltfio-c` | `c/gltfio/cpp/*.cpp` | gltfio_core, dracodec, stb, filament, backend, utils, filaflat, filabridge, image, imageio-lite, ktxreader, zstd |

Kotlin/Native cinterop consumes these as **static archives** alongside the
prebuilt Filament `.a` files — see
[FilamentNative.kt](../buildSrc/src/main/kotlin/FilamentNative.kt#L120) where
each `.a` is passed via `-staticLibrary` to klib packaging. Final link happens
when the iOS framework is built; everything ends up in one Mach-O image
naturally.

### 3.2 Tomorrow: one combined SHARED library

For Panama the JVM needs a **single shared library** to `dlopen`. Two designs
were considered:

#### Design A — ship four dylibs

`libfilament-c.dylib`, `libfilamat-c.dylib`, `libfilament-utils-c.dylib`,
`libgltfio-c.dylib`. Each statically links the slice of Filament it needs.

> **Rejected.** This is exactly the failure mode that
> [java/CMakeLists.txt L80–L83](../java/CMakeLists.txt#L80-L83) documents:
>
> > Historically each binding […] was built as its own SHARED lib statically
> > linking the entire Filament stack. That gave each dylib its own copy of
> > singletons like EntityManager, leading to crashes when an entity created
> > in one binding was passed to another.
>
> The exact same trap would re-open under Panama with four dylibs. An entity
> created by `gltfio-c` and handed to `filament-c` would be looked up in a
> different `EntityManager` instance.

#### Design B — one combined SHARED library *(chosen)*

`libfilament-c.{dylib,so,dll}` statically links **all four** C-wrapper
sources and the **union** of Filament static archives they reference. Each
Filament static archive is included exactly once, so all singletons resolve
to a single instance.

Concretely, `c/CMakeLists.txt` gains a new target alongside the existing
four:

```cmake
if (FILAMENT_BUILD_SHARED)
    add_library(filament-c-jvm SHARED
        # Reuse the same source lists already defined for the four static targets
        ${FILAMENT_C_SOURCES}
        ${FILAMAT_C_SOURCES}
        ${FILAMENT_UTILS_C_SOURCES}
        ${GLTFIO_C_SOURCES}
    )
    target_link_libraries(filament-c-jvm PRIVATE
        # Union of all four targets' link lines, each archive listed once.
        filament backend utils
        geometry ibl-lite filaflat filabridge zstd smol-v meshoptimizer
        filamat shaders
        iblprefilter ktxreader camutils image imageio-lite
        gltfio_core dracodec stb
    )
    # macOS frameworks: same set already used by the static targets.
    set_target_properties(filament-c-jvm PROPERTIES OUTPUT_NAME "filament-c")
endif()
```

The four `STATIC` targets stay — Kotlin/Native still consumes them per-target
via cinterop. The new SHARED target is gated on `FILAMENT_BUILD_SHARED` so the
iOS builds keep producing only the static archives they actually need.

### 3.3 Prebuilts: which static libs must exist on each host?

The combined SHARED build links the *union* of Filament archives across the
four C-wrapper targets. Cross-referencing
[c/CMakeLists.txt](../c/CMakeLists.txt) with the JNI version in
[java/CMakeLists.txt](../java/CMakeLists.txt), the required set for the JVM
build is:

```
libfilament.a            libbackend.a          libutils.a
libgeometry.a            libibl-lite.a         libfilaflat.a
libfilabridge.a          libzstd.a             libsmol-v.a
libmeshoptimizer.a       libfilamat.a          libshaders.a
libfilament-iblprefilter.a   libktxreader.a    libcamutils.a
libimage.a               libimageio-lite.a     libgltfio_core.a
libdracodec.a            libstb.a
```

Every one of these is already present under
[prebuilts/macosArm64/lib/](../prebuilts/macosArm64/lib/) (verified by listing
the directory) and is downloaded by
[scripts/gradle/download_filament_prebuilts.py](../scripts/gradle/download_filament_prebuilts.py)
for `macosArm64`, `macosX64`, `linuxX64`, `linuxArm64`, `mingwX64`.

**This means: zero changes to the prebuilts download script.** The same tree
that fed `:java:filament`'s CMake feeds the new SHARED target in
`c/CMakeLists.txt`. The `host` pseudo-target in the script (line 88) already
resolves to the right per-host name, so local-dev workflows are unchanged too.

> [!IMPORTANT]
> The list above is a **superset** of what `c/CMakeLists.txt`'s four static
> targets need today. The current C-wrapper static builds link
> `ibl-lite` (the lite variant). The JNI build links `ibl` (the full variant)
> because it pulls in `gltfio`'s IBL paths. Verify whether the C-wrapper
> targets actually need `ibl` rather than `ibl-lite` once combined; if so,
> the static targets quietly drop a dependency that the combined SHARED
> build re-introduces.

### 3.4 Platform / linker notes

- **macOS.** Same frameworks the static targets already pull in
  (`Metal`, `Foundation`, `AppKit`, `QuartzCore`, `CoreVideo`). No change.
- **Linux.** The JNI build [forces clang + libc++](../java/CMakeLists.txt#L5-L10)
  because Filament's prebuilts are built with libc++ and mixing with libstdc++
  fails to link. The new SHARED target must do the same. Easiest: lift the
  Linux compiler/stdlib block out of `java/CMakeLists.txt` and apply it
  conditionally in `c/CMakeLists.txt` when `FILAMENT_BUILD_SHARED` is on.
- **Windows.** Filament ships MSVC-flavoured `.lib` files (see the existing
  `LIB_SUFFIX` switch in `c/CMakeLists.txt`). A combined SHARED build emits
  `filament-c.dll` + `filament-c.lib` (import library). No special Windows
  work beyond enabling the SHARED target on `WIN32`.

### 3.5 Wiring up CMake from Gradle

[kotlin/filament/build.gradle.kts](../kotlin/filament/build.gradle.kts) today
has:

```kotlin
jvmMain.dependencies {
    api(project(":java:filament"))
}
```

After Panama:

```kotlin
jvmMain.dependencies {
    // No external deps — Panama is part of the JDK.
}
```

It also gains a Gradle task analogous to
[java/filament/build.gradle.kts](../java/filament/build.gradle.kts)'s
`cmakeBuild`, but pointing at `c/CMakeLists.txt` with `-DFILAMENT_BUILD_SHARED=ON`
and the same per-host platform/arch detection that already lives in the JNI
build script. Output is staged into the `kotlin:filament` JAR under
`resources/natives/{platform}-{arch}/libfilament-c.{dylib,so,dll}` — identical
layout to what `NativeLoader` already extracts from today.

Move the platform/arch detection out of `java/filament/build.gradle.kts` into
something reusable in `buildSrc` (perhaps as a sibling to `FilamentNative.kt`,
e.g. `FilamentJvmNative.kt`) so the four `kotlin/*` modules can share it.

### 3.6 Single-jar vs four-jars

The current `:java:filament` is the *only* JNI module that physically contains
the dylib (resources). The other three (`:java:filamat`, `:java:gltfio`,
`:java:filament-utils`) depend transitively on it and reuse the same loaded
library at runtime — see the comment at the top of
[java/filament/build.gradle.kts](../java/filament/build.gradle.kts).

The same rule applies to Panama: ship the combined `libfilament-c.*` in the
`kotlin:filament` JAR only; the other three `kotlin:*` JVM artifacts symbol-look
into the already-loaded image. `SymbolLookup.libraryLookup(path, arena)` is
called once at startup by `kotlin:filament`, and the resulting handle is
exposed for the other modules to reuse (small internal helper class — same
shape as `NativeLoader` today, but yielding a `SymbolLookup` instead of just
loading a name).

---

## 4. Bundling, publishing, CI

### 4.1 Local dev (one host)

1. `./gradlew downloadPrebuilts_<host>` — already exists, no change.
2. `./gradlew :kotlin:filament:cmakeBuild` (new task) — builds
   `libfilament-c.{dylib,so,dll}` for the current host, drops it under
   `kotlin/filament/build/cmake/`.
3. `./gradlew :kotlin:filament:jvmJar` — `processResources` copies it into
   `natives/{platform}-{arch}/` inside the JAR.
4. Application launches → `SymbolLookup.libraryLookup(extractToTempFile(…))`.

This mirrors the existing JNI flow step-for-step; only the dylib name differs
(`libfilament-jni.dylib` → `libfilament-c.dylib`).

### 4.2 Multi-platform publish (CI)

The current publish job in CI builds `libfilament-jni.dylib`/`.so`/`.dll` on
three separate runners (mac-arm, linux-x64, windows-x64), uploads each as an
artifact, and feeds them into the publishing host via
`-PjniArtifactsDir=<path>` so the published Maven artifact contains natives
for every supported platform (see
[java/filament/build.gradle.kts L99–L106](../java/filament/build.gradle.kts#L99-L106)).

Under Panama the same pattern applies, with `jniArtifactsDir` renamed to
`cArtifactsDir` (or similar). The CI workflow file needs:

- Per-host runner that builds the new SHARED target and uploads
  `libfilament-c.*` as an artifact under `{platform}-{arch}/`.
- Publishing job that downloads all per-host artifacts and passes their
  parent directory via the new property to `:kotlin:filament:jvmJar`.
- macOS x86_64 may need adding back to CI matrix if we still want to support
  Intel Macs; today only macOS arm64 is in the JNI publish path. The
  `download_filament_prebuilts.py` script already supports `macosX64`, so the
  prebuilts side is ready.

### 4.3 What the published JAR looks like

```
kotlin/filament-<version>-jvm.jar
├── META-INF/
├── io/github/erkko68/filament/*.class             ←— compiled jvmMain
└── natives/
    ├── macos-arm64/libfilament-c.dylib
    ├── macos-x64/libfilament-c.dylib              (if we keep x64 mac support)
    ├── linux-x64/libfilament-c.so
    ├── linux-arm64/libfilament-c.so
    └── windows-x64/filament-c.dll
```

Compared to today, the only difference is the dylib name. JAR size will be
similar (likely slightly smaller — there is no JNI glue, but the C wrapper
is more verbose than JNI when exposing builders).

---

## 5. JDK floor

Foreign Function & Memory API timeline:

| JDK | Status | Flag |
|-----|--------|------|
| 17 (current floor) | Not available | — |
| 21 | Preview | `--enable-preview --enable-native-access=ALL-UNNAMED` |
| 22 | Stable | `--enable-native-access=ALL-UNNAMED` |
| 24+ | Stable | warnings until module declares native access |

This is the **largest non-technical cost** of the proposal. Bumping the JVM
floor from 17 to 22 affects every downstream Compose Desktop consumer of
filament-kmp. JDK 22 is non-LTS (next LTS is 25, released Sep 2025). Options:

1. **Bump floor to 22 cleanly.** Cleanest code, recent enough that most
   active Compose Desktop projects can move. Drops anyone stuck on 17/21.
2. **Bump floor to 21 with `--enable-preview`.** Code still works, but
   every consumer must add JVM args to their app launcher and IDE run
   config. Brittle.
3. **Dual implementation: keep JNI + add Panama behind a flag.** Defeats
   most of the simplification benefit (the `java/` tree stays). Could
   serve as a transitional state on a release or two before deprecating.

A reasonable plan is **(1) for the next minor with a clear deprecation
note**, possibly aligning the bump to wait for JDK 25 LTS in late 2025 if
release timing allows.

---

## 6. What stays untouched

To be explicit, nothing in the following list changes:

- `commonMain` `expect` declarations across all KMP modules.
- `androidMain` — keeps delegating to `com.google.android.filament:filament-android`.
- `nativeMain` (iOS) — cinterop bindings stay as-is; the same four C-wrapper
  STATIC archives feed them.
- `jsMain` — Filament.js / WASM bundle unaffected.
- `prebuilts/` layout and download script (`host` target, per-platform names).
- The C wrapper sources themselves (`c/filament/{c,cpp}/*` etc.).
- The four `STATIC` CMake targets in `c/CMakeLists.txt` — they continue to
  exist for iOS. We only **add** a new SHARED target.
- `include/` Filament headers tree.

---

## 7. Migration order (suggested)

A safe order, each step independently shippable:

1. **Add the combined SHARED target to `c/CMakeLists.txt`**, gated on
   `FILAMENT_BUILD_SHARED=OFF` by default. No behavioural change yet.
2. **Move the Linux clang/libc++ block** out of `java/CMakeLists.txt` into
   shared CMake helpers so step 1 picks it up without duplication.
3. **Pilot Panama in `kotlin:filament` only** for a small surface — say,
   `Engine` + `Renderer` + `View` + `SwapChain`. Keep `:java:filament` as
   the source of truth; expose Panama paths behind an internal flag and
   run both side-by-side in `kotlin:filament`'s tests to verify identical
   behaviour.
4. **Port the rest of `kotlin:filament/jvmMain`** to Panama; delete its
   `:java:filament` dependency.
5. **Repeat for `kotlin:filamat`, `kotlin:gltfio`, `kotlin:filament-utils`.**
6. **Delete the four `:java:*` modules** + `java/CMakeLists.txt` +
   `java/` tree wholesale. Remove `include(":java:*")` from
   `settings.gradle.kts`.
7. **Bump JDK floor** in `:kotlin:*` modules and root toolchain config to
   22. Update sample apps. Update CI matrix.
8. **CI publish workflow**: rename `jniArtifactsDir` → `cArtifactsDir`,
   produce `libfilament-c.*` per host runner.

Steps 1–2 are safe refactors. Step 3 is the real go/no-go gate — if Panama
shows unexpected behavioural drift (struct alignment, callback threading,
crash-on-GC, etc.) we abort having spent a contained amount of effort.
Steps 4–8 are mechanical once 3 is green.

---

## 8. Open questions worth resolving before step 3

1. **Does Filament's C++ ABI on Windows expose anything that needs MSVC
   name-mangling that the static `.lib` archives don't already handle?**
   The JNI build works there today, so probably not, but it's the platform
   where SHARED builds historically break first.
2. **Do we want to expose `MemorySegment` in any public API**, or hide it
   entirely behind Kotlin value classes? Keeping it hidden keeps consumer
   code identical across JVM and Native; surfacing it leaks JDK-22 types
   into the public surface and constrains downstream JDK floors.
3. **Long-lived upcall stubs**: which callbacks survive the call site? At
   minimum `BeginFrameInternal`, `readPixels` completion handlers, and
   `Stream` acquired/released callbacks. Each needs an arena tied to the
   owning Kotlin object's lifetime — ensure we have a uniform pattern, not
   ad-hoc per callsite.
4. **GraalVM native-image** consumers (if any): FFM API is supported but
   needs reachability metadata for every downcall. Out of scope for this
   doc but worth noting.

---

## 9. Summary scorecard

| Concern | Verdict |
|---------|---------|
| Kotlin layer rewrite | Mechanical, mirrors `nativeMain` cinterop bindings |
| Native build changes | One new SHARED target combining the four static ones |
| Prebuilts download script | **No changes required** |
| Prebuilts layout on disk | **No changes required** |
| Per-host JAR packaging | Identical to today, dylib renamed |
| CI publish workflow | Property rename + dylib rename; structure unchanged |
| Singleton/EntityManager hazard | Solved by combined SHARED build (same trick as JNI) |
| JDK floor | **Bumps from 17 to 22** — biggest external cost |
| Lines deleted | ~5000 in C++ glue, ~6000 in Java JNI wrappers, ~70 files |
| Lines added | ~1000 in `FilamentC.kt` + small `SymbolLookup` loader |
| Net code reduction | Substantial; layers go from 4 to 2 |

The proposal is worth pursuing if the JDK-22 floor is acceptable. The
biggest implementation risk is **not** the Kotlin rewrite, the prebuilts, or
the bundling — all of those are well-understood mechanical work. The risk
is whatever surprises the combined SHARED build of the C wrapper turns up
on Linux/Windows, which is exactly the territory step 3 above is designed
to surface cheaply.
