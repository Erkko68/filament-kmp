#ifndef FILAMENT_C_EXPOSURE_H
#define FILAMENT_C_EXPOSURE_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

float FilaExposure_ev100Camera(const FilaCamera* camera);
float FilaExposure_ev100(float aperture, float shutterSpeed, float sensitivity);
float FilaExposure_ev100FromLuminance(float luminance);
float FilaExposure_ev100FromIlluminance(float illuminance);

float FilaExposure_exposureCamera(const FilaCamera* camera);
float FilaExposure_exposureFromParams(float aperture, float shutterSpeed, float sensitivity);
float FilaExposure_exposureFromEv100(float ev100);

float FilaExposure_luminanceCamera(const FilaCamera* camera);
float FilaExposure_luminanceFromParams(float aperture, float shutterSpeed, float sensitivity);
float FilaExposure_luminanceFromEv100(float ev100);

float FilaExposure_illuminanceCamera(const FilaCamera* camera);
float FilaExposure_illuminanceFromParams(float aperture, float shutterSpeed, float sensitivity);
float FilaExposure_illuminanceFromEv100(float ev100);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_EXPOSURE_H

