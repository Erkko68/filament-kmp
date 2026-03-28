#include "filament/Camera.h"

// Function pointer assignments lock exported C signatures.
static FilaEntity (*g_camera_get_entity)(const FilaCamera*) = FilaCamera_getEntity;
static void (*g_camera_set_exposure)(FilaCamera*, float, float, float) = FilaCamera_setExposure;

void fila_camera_signature_compile_only(void) {
    (void)g_camera_get_entity;
    (void)g_camera_set_exposure;
}

