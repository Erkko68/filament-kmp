package io.github.erkko68.filament

/**
 * JVM implementation of NativeSurface.
 * 
 * It wraps a java.lang.Long (raw native window handle or texture pointer).
 */
actual class NativeSurface(val surface: Any)
