package io.github.erkko68.filament

import com.google.android.filament.Renderer as AndroidRenderer

// The Android bindings model each option struct as a mutable upstream object, so the
// common data classes are marshalled across field by field.

internal fun DisplayInfo.toAndroid(): AndroidRenderer.DisplayInfo {
    val n = AndroidRenderer.DisplayInfo()
    n.refreshRate = refreshRate
    return n
}

internal fun FrameRateOptions.toAndroid(): AndroidRenderer.FrameRateOptions {
    val n = AndroidRenderer.FrameRateOptions()
    n.interval = interval
    n.headRoomRatio = headRoomRatio
    n.scaleRate = scaleRate
    n.history = history
    return n
}

internal fun ClearOptions.toAndroid(): AndroidRenderer.ClearOptions {
    val n = AndroidRenderer.ClearOptions()
    n.clearColor = clearColor
    n.clear = clear
    n.discard = discard
    return n
}
