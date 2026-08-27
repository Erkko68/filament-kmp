package io.github.erkko68.filament

/**
 * Marks a raw native handle that filament-kmp's own modules share across module boundaries.
 *
 * These members expose backend pointers directly: writing one, or reading one after the
 * owning object has been destroyed, crashes the process. They are not part of the supported
 * API and may change or disappear in any release.
 */
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "Raw native handle: an implementation detail of filament-kmp, not supported API.",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS)
annotation class InternalFilamentApi
