#ifndef FILAMENT_C_LIGHT_MANAGER_H
#define FILAMENT_C_LIGHT_MANAGER_H

#include <stdbool.h>
#include <stddef.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaLightType {
	FILA_LIGHT_TYPE_SUN = 0,
	FILA_LIGHT_TYPE_DIRECTIONAL = 1,
	FILA_LIGHT_TYPE_POINT = 2,
	FILA_LIGHT_TYPE_FOCUSED_SPOT = 3,
	FILA_LIGHT_TYPE_SPOT = 4,
} FilaLightType;

// Returns true if the entity has a light component.
bool FilaLightManager_hasComponent(const FilaLightManager* manager, FilaEntity entity);

// Gets the light instance associated with the entity, or 0 if missing.
FilaLightManagerInstance FilaLightManager_getInstance(const FilaLightManager* manager, FilaEntity entity);

// Returns the number of light components.
size_t FilaLightManager_getComponentCount(const FilaLightManager* manager);

// Returns whether the manager has no components.
bool FilaLightManager_empty(const FilaLightManager* manager);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaLightManager_getEntity(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Writes up to maxCount entities managed by this component manager and returns number written.
size_t FilaLightManager_getEntities(const FilaLightManager* manager, FilaEntity* outEntities, size_t maxCount);

// Destroys a light component from an entity.
void FilaLightManager_destroy(FilaLightManager* manager, FilaEntity entity);

// Returns the type of a light instance. Defaults to directional for invalid inputs.
FilaLightType FilaLightManager_getType(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Creates a light builder for the specified light type.
FilaLightManagerBuilder* FilaLightManagerBuilder_create(FilaLightType type);

// Destroys a light builder.
void FilaLightManagerBuilder_destroy(FilaLightManagerBuilder* builder);

// Sets the light direction for directional and spot lights.
void FilaLightManagerBuilder_direction(FilaLightManagerBuilder* builder, float x, float y, float z);

// Sets the light position for point and spot lights.
void FilaLightManagerBuilder_position(FilaLightManagerBuilder* builder, float x, float y, float z);

// Sets light color in linear RGB.
void FilaLightManagerBuilder_color(FilaLightManagerBuilder* builder, float r, float g, float b);

// Sets light intensity in lux for directional/sun, lumens otherwise.
void FilaLightManagerBuilder_intensity(FilaLightManagerBuilder* builder, float intensity);

// Sets the influence radius for point and spot lights.
void FilaLightManagerBuilder_falloff(FilaLightManagerBuilder* builder, float radius);

// Sets inner/outer spot light cone angles in radians.
void FilaLightManagerBuilder_spotLightCone(FilaLightManagerBuilder* builder, float inner, float outer);

// Enables or disables shadow casting for this light.
void FilaLightManagerBuilder_castShadows(FilaLightManagerBuilder* builder, bool enable);

// Builds the light component into the given entity. Returns true on success.
bool FilaLightManagerBuilder_build(FilaLightManagerBuilder* builder, FilaEngine* engine, FilaEntity entity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_LIGHT_MANAGER_H

