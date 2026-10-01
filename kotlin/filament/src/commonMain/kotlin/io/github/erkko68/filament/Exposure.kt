package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*

/**
 * Utilities to compute exposure, exposure value at ISO 100 (EV100), luminance and illuminance
 * using a physically-based camera model. Aperture is in f-stops, shutter speed in seconds, sensitivity in ISO.
 */
object Exposure {
    init { Filament.init() } // CPU-only, usable before any Engine exists

    /** The exposure value (EV at ISO 100) of [camera]. */
    fun ev100(camera: Camera): Float = FilaExposure_ev100_Camera(camera.nativeHandle)

    /** The exposure value (EV at ISO 100) of the given exposure parameters. */
    fun ev100(aperture: Float, shutterSpeed: Float, sensitivity: Float): Float =
        FilaExposure_ev100_float_float_float(aperture, shutterSpeed, sensitivity)

    /** The exposure value (EV at ISO 100) for an average [luminance] in cd/m². */
    fun ev100FromLuminance(luminance: Float): Float = FilaExposure_ev100FromLuminance(luminance)

    /** The exposure value (EV at ISO 100) for an [illuminance] in lux. */
    fun ev100FromIlluminance(illuminance: Float): Float = FilaExposure_ev100FromIlluminance(illuminance)

    /** The photometric exposure of [camera]. */
    fun exposure(camera: Camera): Float = FilaExposure_exposure_Camera(camera.nativeHandle)

    /** The photometric exposure of the given parameters; like `exposure(ev100(...))` but more precise. */
    fun exposure(aperture: Float, shutterSpeed: Float, sensitivity: Float): Float =
        FilaExposure_exposure_float_float_float(aperture, shutterSpeed, sensitivity)

    /** The photometric exposure for [ev100]. */
    fun exposure(ev100: Float): Float = FilaExposure_exposure_float(ev100)

    /** The incident luminance in cd/m² for [camera] acting as a spot meter. */
    fun luminance(camera: Camera): Float = FilaExposure_luminance_Camera(camera.nativeHandle)

    /** The incident luminance in cd/m² for the given parameters; like `luminance(ev100(...))` but more precise. */
    fun luminance(aperture: Float, shutterSpeed: Float, sensitivity: Float): Float =
        FilaExposure_luminance_float_float_float(aperture, shutterSpeed, sensitivity)

    /** The luminance in cd/m² a camera would expose correctly at [ev100]. */
    fun luminance(ev100: Float): Float = FilaExposure_luminance_float(ev100)

    /** The illuminance in lux for [camera] acting as an incident light meter. */
    fun illuminance(camera: Camera): Float = FilaExposure_illuminance_Camera(camera.nativeHandle)

    /** The illuminance in lux for the given parameters; like `illuminance(ev100(...))` but more precise. */
    fun illuminance(aperture: Float, shutterSpeed: Float, sensitivity: Float): Float =
        FilaExposure_illuminance_float_float_float(aperture, shutterSpeed, sensitivity)

    /** The illuminance in lux a camera would expose correctly at [ev100]. */
    fun illuminance(ev100: Float): Float = FilaExposure_illuminance_float(ev100)
}
