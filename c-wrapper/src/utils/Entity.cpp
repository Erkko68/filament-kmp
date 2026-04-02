#include <utils/Entity.h>

#include "../../include/utils/Entity.h"

extern "C" {

bool FilaEntity_isNull(FilaEntity entity) {
    return utils::Entity::import(entity).isNull();
}

bool FilaEntity_isValid(FilaEntity entity) {
    return !utils::Entity::import(entity).isNull();
}

uint32_t FilaEntity_getId(FilaEntity entity) {
    return utils::Entity::import(entity).getId();
}

void FilaEntity_clear(FilaEntity* inOutEntity) {
    if (!inOutEntity) {
        return;
    }
    *inOutEntity = 0;
}

} // extern "C"


