// Hand-written counterparts of ../generated's TODO(handwritten) entries.
#ifndef FILA_MANUAL_FILACAMUTILSMANIPULATORMANUAL_H
#define FILA_MANUAL_FILACAMUTILSMANIPULATORMANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// A ray the raycast callback gets: 6 floats, origin then direction. FilaCamutilsRay_hit answers it; a miss doesn't.
typedef struct FilaCamutilsRay FilaCamutilsRay;
// The callback and user data a manipulator raycasts with.
typedef struct FilaCamutilsRaycast FilaCamutilsRaycast;

// Builder::raycastCallback through C's callback, which gets a FilaCamutilsRay. Destroy the result with
// FilaCamutilsRaycast_destroy once the manipulators built with it are gone.
FilaCamutilsRaycast* FilaCamutilsManipulatorBuilder_raycastCallback(FilaCamutilsManipulatorBuilder* self, FilaArgCallback callback, void* callbackUser);
void FilaCamutilsRaycast_destroy(FilaCamutilsRaycast* raycast);
// The ray hits at origin + direction * t.
void FilaCamutilsRay_hit(FilaCamutilsRay* ray, float t);

#ifdef __cplusplus
}
#endif

#endif // FILA_MANUAL_FILACAMUTILSMANIPULATORMANUAL_H
