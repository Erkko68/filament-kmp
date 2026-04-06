#include <utils/EntityManager.h>
#include <utils/Entity.h>

#include "FilaCommon.h"
#include "../c/EntityManager.h"

using namespace utils;

extern "C" {

FilaEntityManager* FilaEntityManager_get() {
    return reinterpret_cast<FilaEntityManager*>(&EntityManager::get());
}

FilaEntity FilaEntityManager_create(FilaEntityManager* em) {
    return FILA_CAST(EntityManager, em)->create().getId();
}

void FilaEntityManager_createArray(FilaEntityManager* em, size_t n, FilaEntity* outEntities) {
    // Entities are just uint32_t IDs. We can safely cast the pointer.
    FILA_CAST(EntityManager, em)->create(n, reinterpret_cast<Entity*>(outEntities));
}

void FilaEntityManager_destroy(FilaEntityManager* em, FilaEntity entityId) {
    Entity entity = Entity::import(entityId);
    FILA_CAST(EntityManager, em)->destroy(entity);
}

void FilaEntityManager_destroyArray(FilaEntityManager* em, size_t n, const FilaEntity* entities) {
    FILA_CAST(EntityManager, em)->destroy(n, reinterpret_cast<Entity*>(const_cast<FilaEntity*>(entities)));
}

bool FilaEntityManager_isAlive(FilaEntityManager* em, FilaEntity entityId) {
    Entity entity = Entity::import(entityId);
    return FILA_CAST(EntityManager, em)->isAlive(entity);
}

} // extern "C"
