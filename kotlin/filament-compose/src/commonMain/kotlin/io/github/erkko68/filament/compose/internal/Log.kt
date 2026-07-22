package io.github.erkko68.filament.compose.internal

/**
 * Library warning channel — routes to each platform's native log sink (Logcat, stderr, the
 * browser console) instead of raw stdout, so app logs can filter it by the `filament-compose` tag.
 */
internal expect fun logWarn(message: String)
