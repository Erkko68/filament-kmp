# `:desktop` — desktop runtime for `:jni`

Builds `libfilament-c.{dylib,so,dll}` (the C API + the [`:jni`](../jni/README.md) runtime) for the host
and ships the loader that finds it at runtime. Every `:kotlin:*` JVM target depends on it.

| Artifact | Contents |
|---|---|
| `filament-jni-desktop` | `FilamentLoader` — no natives. Its Gradle metadata depends on **every** runtime jar below by default, and exposes one variant per platform (`OperatingSystemFamily` × `MachineArchitecture`) |
| `filament-jni-runtime-{macos-arm64, linux-x64, linux-arm64, windows-x64, windows-arm64}` | one platform's `libfilament-c` (+ `.sha256`) under `natives/<os>-<arch>/` |

- **Native build:** `:desktop:cmakeBuild` builds the `filament-c-jni` target of [`c/CMakeLists.txt`](../c/CMakeLists.txt)
  for `FilamentTarget.host()`, with `jni.h` from the JDK running Gradle. `-Pfilament.debug=true` builds it in Debug
  (on Windows the `/MTd`↔`/MT` CRT mismatch with the Release prebuilts breaks that link).
- **Runtime jars:** each `runtime-<id>` jar takes the host's library from `cmakeBuild` and every other platform's
  from `-PcArtifactsDir=<dir>` (`<dir>/<id>/`), which CI's publish job fills; publishing an empty jar fails.
- **Loading:** `Filament.init()` calls `FilamentLoader.load()`, which extracts the library once into a
  content-hash-keyed cache dir (`~/.filament-kmp/filament-c-<hash>/`) and `System.load`s it. Knobs:
  `filament.library.path` (load from that directory instead), `filament.data.path` (cache root),
  `filament.data.cleanup.days` (stale-cache purge age, default 30, `<= 0` disables).
- **Seal:** only `Java_*` and `JNI_OnLoad` are exported; Filament's C++ stays local to the image.

## One platform instead of five

Plain consumers get every runtime jar (zero config). Gradle consumers that set the two attributes
get just theirs:

```kotlin
configurations.matching { it.isCanBeResolved }.configureEach {
    attributes {
        attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(OperatingSystemFamily.MACOS))
        attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(MachineArchitecture.ARM64))
    }
}
```

Packaged desktop apps (jpackage / Compose Desktop distributions) bundle the whole runtime classpath, so
per-platform installers should set these attributes, or depend on one runtime jar directly (it pulls the
loader and excludes its siblings):

```kotlin
implementation("io.github.erkko68.filament:filament-jni-runtime-macos-arm64:<version>")
```
