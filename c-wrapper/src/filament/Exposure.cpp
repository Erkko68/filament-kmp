#include <filament/Camera.h>
#include <filament/Exposure.h>

#include "../../include/filament/Exposure.h"

extern "C" {

float FilaExposure_ev100Camera(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return filament::Exposure::ev100(*cppCamera);
}

float FilaExposure_ev100(float aperture, float shutterSpeed, float sensitivity) {
    return filament::Exposure::ev100(aperture, shutterSpeed, sensitivity);
}

float FilaExposure_ev100FromLuminance(float luminance) {
    return filament::Exposure::ev100FromLuminance(luminance);
}

float FilaExposure_ev100FromIlluminance(float illuminance) {
    return filament::Exposure::ev100FromIlluminance(illuminance);
}

float FilaExposure_exposureCamera(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return filament::Exposure::exposure(*cppCamera);
}

float FilaExposure_exposureFromParams(float aperture, float shutterSpeed, float sensitivity) {
    return filament::Exposure::exposure(aperture, shutterSpeed, sensitivity);
}

float FilaExposure_exposureFromEv100(float ev100) {
    return filament::Exposure::exposure(ev100);
}

float FilaExposure_luminanceCamera(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return filament::Exposure::luminance(*cppCamera);
}

float FilaExposure_luminanceFromParams(float aperture, float shutterSpeed, float sensitivity) {
    return filament::Exposure::luminance(aperture, shutterSpeed, sensitivity);
}

float FilaExposure_luminanceFromEv100(float ev100) {
    return filament::Exposure::luminance(ev100);
}

float FilaExposure_illuminanceCamera(const FilaCamera* camera) {
    if (!camera) {
        return 0.0f;
    }
    auto cppCamera = reinterpret_cast<const filament::Camera*>(camera);
    return filament::Exposure::illuminance(*cppCamera);
}

float FilaExposure_illuminanceFromParams(float aperture, float shutterSpeed, float sensitivity) {
    return filament::Exposure::illuminance(aperture, shutterSpeed, sensitivity);
}

float FilaExposure_illuminanceFromEv100(float ev100) {
    return filament::Exposure::illuminance(ev100);
}

} // extern "C"

