package dev.filament.kmp

import com.google.android.filament.Viewport as AndroidViewport

actual class Viewport actual constructor(left: Int, bottom: Int, width: Int, height: Int) {
    internal val androidViewport: AndroidViewport = AndroidViewport(left, bottom, width, height)

    actual var left: Int
        get() = androidViewport.left
        set(value) {
            androidViewport.left = value
        }

    actual var bottom: Int
        get() = androidViewport.bottom
        set(value) {
            androidViewport.bottom = value
        }

    actual var width: Int
        get() = androidViewport.width
        set(value) {
            androidViewport.width = value
        }

    actual var height: Int
        get() = androidViewport.height
        set(value) {
            androidViewport.height = value
        }
}

