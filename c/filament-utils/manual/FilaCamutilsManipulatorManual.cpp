#include "../generated/Includes.hpp"
#include "FilaCamutilsManipulatorManual.h"

#include <cstddef>

using Manipulator = filament::camutils::Manipulator<float>;

struct FilaCamutilsRay {
    float origin[3];
    float direction[3];
    float t;
    bool hit;
};

struct FilaCamutilsRaycast {
    FilaArgCallback callback;
    void* user;
};

static_assert(offsetof(FilaCamutilsRay, direction) == 3 * sizeof(float));

extern "C" {

FilaCamutilsRaycast* FilaCamutilsManipulatorBuilder_raycastCallback(FilaCamutilsManipulatorBuilder* self, FilaArgCallback callback, void* callbackUser) {
    auto* raycast = new FilaCamutilsRaycast{ callback, callbackUser };
    fila::cpp(self)->raycastCallback([](Manipulator::vec3 const& origin, Manipulator::vec3 const& dir, float* t, void* user) {
        FilaCamutilsRay ray{ { origin.x, origin.y, origin.z }, { dir.x, dir.y, dir.z }, 0, false };
        auto* r = static_cast<FilaCamutilsRaycast*>(user);
        r->callback(&ray, r->user);
        if (ray.hit) *t = ray.t;
        return ray.hit;
    }, raycast);
    return raycast;
}

void FilaCamutilsRaycast_destroy(FilaCamutilsRaycast* raycast) {
    delete raycast;
}

void FilaCamutilsRay_hit(FilaCamutilsRay* ray, float t) {
    ray->t = t;
    ray->hit = true;
}

} // extern "C"
