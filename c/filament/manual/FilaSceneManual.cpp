#include "../generated/Includes.hpp"
#include "FilaSceneManual.h"

extern "C" {

uint32_t FilaScene_forEach(const FilaScene* self, FilaEntity* out, uint32_t outCapacity) {
    uint32_t count = 0;
    fila::cpp(self)->forEach([&](utils::Entity entity) {
        if (count < outCapacity) out[count] = utils::Entity::smuggle(entity);
        count++;
    });
    return count;
}

} // extern "C"
