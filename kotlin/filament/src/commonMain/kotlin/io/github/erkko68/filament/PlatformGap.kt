package io.github.erkko68.filament

/**
 * Platform buckets used by [PlatformGap]. [WEB] covers both the `js` and `wasmJs` targets
 * (they share the same `webMain` actuals and the same filament-kmp.wasm module).
 */
enum class FilamentPlatform { ANDROID, IOS, JVM, WEB }

/**
 * Marks a common API whose platform binding is missing or degraded on the listed platforms.
 *
 * The API exists in `commonMain` so shared code compiles everywhere, but the platform `actual`
 * cannot reach the underlying engine feature — e.g. WebGL or single-threaded wasm lacks it, or the
 * Android SDK doesn't expose it. [behavior] states exactly what the call does
 * there: throws `UnsupportedOperationException`, is a silent no-op, or returns a placeholder.
 *
 * The full per-platform coverage table lives in
 * [Platform Notes](https://github.com/Erkko68/filament-kmp/blob/main/docs/platform-notes.md).
 *
 * @property platforms Platforms on which the binding is missing or degraded.
 * @property behavior What calling the API does on those platforms.
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class PlatformGap(
    val platforms: Array<FilamentPlatform>,
    val behavior: String,
)
