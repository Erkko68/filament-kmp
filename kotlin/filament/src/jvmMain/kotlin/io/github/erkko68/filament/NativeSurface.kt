package io.github.erkko68.filament

/**
 * JVM implementation of NativeSurface.
 * 
 * It can wrap:
 * 1. A java.awt.Component (handle extracted via JAWT in JNI)
 * 2. A java.lang.Long (raw native window handle)
 */
actual class NativeSurface(val surface: Any)
