#ifndef FILAMENT_C_LIGHT_MANAGER_H
#define FILAMENT_C_LIGHT_MANAGER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

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

typedef struct FilaLightManagerShadowOptionsVsm {
	bool elvsm;
	float blurWidth;
} FilaLightManagerShadowOptionsVsm;

typedef struct FilaLightManagerShadowOptions {
	uint32_t mapSize;
	uint8_t shadowCascades;
	float cascadeSplitPositions[3];
	float constantBias;
	float normalBias;
	float shadowFar;
	float shadowNearHint;
	float shadowFarHint;
	bool stable;
	bool lispsm;
	float polygonOffsetConstant;
	float polygonOffsetSlope;
	bool screenSpaceContactShadows;
	uint8_t stepCount;
	float maxShadowDistance;
	FilaLightManagerShadowOptionsVsm vsm;
	float shadowBulbRadius;
	float transform[4]; // quaternion in w,x,y,z order
} FilaLightManagerShadowOptions;

// Computes uniform shadow cascade split positions for a given cascade count.
void FilaLightManagerShadowCascades_computeUniformSplits(float* splitPositions, uint8_t cascades);

// Computes logarithmic shadow cascade split positions for a given cascade count and depth range.
void FilaLightManagerShadowCascades_computeLogSplits(float* splitPositions,
		uint8_t cascades,
		float near,
		float far);

// Computes practical shadow cascade split positions for a given cascade count and depth range.
void FilaLightManagerShadowCascades_computePracticalSplits(float* splitPositions,
		uint8_t cascades,
		float near,
		float far,
		float lambda);

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
bool FilaLightManager_isDirectional(const FilaLightManager* manager, FilaLightManagerInstance instance);
bool FilaLightManager_isPointLight(const FilaLightManager* manager, FilaLightManagerInstance instance);
bool FilaLightManager_isSpotLight(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Sets and gets light position.
void FilaLightManager_setPosition(FilaLightManager* manager, FilaLightManagerInstance instance, float x, float y, float z);
bool FilaLightManager_getPosition(const FilaLightManager* manager, FilaLightManagerInstance instance, float outPosition[3]);

// Sets and gets light direction.
void FilaLightManager_setDirection(FilaLightManager* manager, FilaLightManagerInstance instance, float x, float y, float z);
bool FilaLightManager_getDirection(const FilaLightManager* manager, FilaLightManagerInstance instance, float outDirection[3]);

// Sets and gets light color (linear RGB).
void FilaLightManager_setColor(FilaLightManager* manager, FilaLightManagerInstance instance, float r, float g, float b);
bool FilaLightManager_getColor(const FilaLightManager* manager, FilaLightManagerInstance instance, float outColor[3]);

// Sets and gets light intensity.
void FilaLightManager_setIntensity(FilaLightManager* manager, FilaLightManagerInstance instance, float intensity);
float FilaLightManager_getIntensity(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Sets light intensity in candela and light channel controls.
void FilaLightManager_setIntensityCandela(FilaLightManager* manager, FilaLightManagerInstance instance, float intensity);
void FilaLightManager_setIntensityWattsEfficiency(FilaLightManager* manager,
		FilaLightManagerInstance instance,
		float watts,
		float efficiency);
void FilaLightManager_setLightChannel(FilaLightManager* manager, FilaLightManagerInstance instance, unsigned int channel, bool enable);
bool FilaLightManager_getLightChannel(const FilaLightManager* manager, FilaLightManagerInstance instance, unsigned int channel);

// Sets and gets falloff radius.
void FilaLightManager_setFalloff(FilaLightManager* manager, FilaLightManagerInstance instance, float radius);
float FilaLightManager_getFalloff(const FilaLightManager* manager, FilaLightManagerInstance instance);

// Spot and sun controls.
void FilaLightManager_setSpotLightCone(FilaLightManager* manager, FilaLightManagerInstance instance, float inner, float outer);
float FilaLightManager_getSpotLightOuterCone(const FilaLightManager* manager, FilaLightManagerInstance instance);
float FilaLightManager_getSpotLightInnerCone(const FilaLightManager* manager, FilaLightManagerInstance instance);
void FilaLightManager_setSunAngularRadius(FilaLightManager* manager, FilaLightManagerInstance instance, float angularRadius);
float FilaLightManager_getSunAngularRadius(const FilaLightManager* manager, FilaLightManagerInstance instance);
void FilaLightManager_setSunHaloSize(FilaLightManager* manager, FilaLightManagerInstance instance, float haloSize);
float FilaLightManager_getSunHaloSize(const FilaLightManager* manager, FilaLightManagerInstance instance);
void FilaLightManager_setSunHaloFalloff(FilaLightManager* manager, FilaLightManagerInstance instance, float haloFalloff);
float FilaLightManager_getSunHaloFalloff(const FilaLightManager* manager, FilaLightManagerInstance instance);
void FilaLightManager_setShadowCaster(FilaLightManager* manager, FilaLightManagerInstance instance, bool shadowCaster);
bool FilaLightManager_isShadowCaster(const FilaLightManager* manager, FilaLightManagerInstance instance);
void FilaLightManager_setShadowOptions(FilaLightManager* manager,
		FilaLightManagerInstance instance,
		const FilaLightManagerShadowOptions* options);
bool FilaLightManager_getShadowOptions(const FilaLightManager* manager,
		FilaLightManagerInstance instance,
		FilaLightManagerShadowOptions* outOptions);

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
void FilaLightManagerBuilder_intensityWattsEfficiency(FilaLightManagerBuilder* builder,
		float watts,
		float efficiency);

// Sets the influence radius for point and spot lights.
void FilaLightManagerBuilder_falloff(FilaLightManagerBuilder* builder, float radius);

// Sets inner/outer spot light cone angles in radians.
void FilaLightManagerBuilder_spotLightCone(FilaLightManagerBuilder* builder, float inner, float outer);

// Enables or disables shadow casting for this light.
void FilaLightManagerBuilder_castShadows(FilaLightManagerBuilder* builder, bool enable);
void FilaLightManagerBuilder_castLight(FilaLightManagerBuilder* builder, bool enable);
void FilaLightManagerBuilder_lightChannel(FilaLightManagerBuilder* builder, unsigned int channel, bool enable);
void FilaLightManagerBuilder_intensityCandela(FilaLightManagerBuilder* builder, float intensity);
void FilaLightManagerBuilder_sunAngularRadius(FilaLightManagerBuilder* builder, float angularRadius);
void FilaLightManagerBuilder_sunHaloSize(FilaLightManagerBuilder* builder, float haloSize);
void FilaLightManagerBuilder_sunHaloFalloff(FilaLightManagerBuilder* builder, float haloFalloff);
void FilaLightManagerBuilder_shadowOptions(FilaLightManagerBuilder* builder,
		const FilaLightManagerShadowOptions* options);

// Builds the light component into the given entity. Returns true on success.
bool FilaLightManagerBuilder_build(FilaLightManagerBuilder* builder, FilaEngine* engine, FilaEntity entity);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_LIGHT_MANAGER_H

