#include "filament/Camera.h"

// Function pointer assignments lock exported C signatures.
static FilaEntity (*g_camera_get_entity)(const FilaCamera*) = FilaCamera_getEntity;
static void (*g_camera_set_exposure)(FilaCamera*, float, float, float) = FilaCamera_setExposure;
static void (*g_camera_set_projection_fov)(FilaCamera*, double, double, double, double, FilaCameraFov) = FilaCamera_setProjectionFov;
static void (*g_camera_look_at)(FilaCamera*, double, double, double, double, double, double, double, double, double) = FilaCamera_lookAt;

void fila_camera_signature_compile_only(void) {
    (void)g_camera_get_entity;
    (void)g_camera_set_exposure;
    (void)g_camera_set_projection_fov;
    (void)g_camera_look_at;
}

