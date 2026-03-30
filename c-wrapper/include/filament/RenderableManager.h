#ifndef FILAMENT_C_RENDERABLE_MANAGER_H
#define FILAMENT_C_RENDERABLE_MANAGER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaRenderablePrimitiveType {
	FILA_RENDERABLE_PRIMITIVE_POINTS = 0,
	FILA_RENDERABLE_PRIMITIVE_LINES = 1,
	FILA_RENDERABLE_PRIMITIVE_TRIANGLES = 4,
} FilaRenderablePrimitiveType;

// Returns true if the entity has a renderable component.
bool FilaRenderableManager_hasComponent(const FilaRenderableManager* manager, FilaEntity entity);

// Gets the renderable instance associated with the entity, or 0 if missing.
FilaRenderableManagerInstance FilaRenderableManager_getInstance(const FilaRenderableManager* manager, FilaEntity entity);

// Returns the number of renderable components.
size_t FilaRenderableManager_getComponentCount(const FilaRenderableManager* manager);

// Returns whether the manager has no components.
bool FilaRenderableManager_empty(const FilaRenderableManager* manager);

// Returns the entity associated with an instance, or 0 for invalid inputs.
FilaEntity FilaRenderableManager_getEntity(const FilaRenderableManager* manager, FilaRenderableManagerInstance instance);

// Writes up to maxCount entities managed by this component manager and returns number written.
size_t FilaRenderableManager_getEntities(const FilaRenderableManager* manager, FilaEntity* outEntities, size_t maxCount);

// Destroys a renderable component from an entity.
void FilaRenderableManager_destroy(FilaRenderableManager* manager, FilaEntity entity);

// Returns the primitive count for an instance.
size_t FilaRenderableManager_getPrimitiveCount(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Sets bits in a renderable instance layer mask.
void FilaRenderableManager_setLayerMask(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		uint8_t select,
		uint8_t values);

// Gets layer mask bits for a renderable instance.
uint8_t FilaRenderableManager_getLayerMask(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Sets coarse draw priority for a renderable instance.
void FilaRenderableManager_setPriority(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		uint8_t priority);

// Gets draw priority for a renderable instance.
uint8_t FilaRenderableManager_getPriority(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Enables or disables frustum culling for a renderable instance.
void FilaRenderableManager_setCulling(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);

// Returns whether frustum culling is enabled for a renderable instance.
bool FilaRenderableManager_isCullingEnabled(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Creates a renderable builder for the specified primitive count.
FilaRenderableManagerBuilder* FilaRenderableManagerBuilder_create(size_t primitiveCount);

// Destroys a renderable builder.
void FilaRenderableManagerBuilder_destroy(FilaRenderableManagerBuilder* builder);

// Sets builder layer-mask bits.
void FilaRenderableManagerBuilder_layerMask(FilaRenderableManagerBuilder* builder, uint8_t select, uint8_t values);

// Sets builder draw priority.
void FilaRenderableManagerBuilder_priority(FilaRenderableManagerBuilder* builder, uint8_t priority);

// Enables or disables frustum culling for built renderable.
void FilaRenderableManagerBuilder_culling(FilaRenderableManagerBuilder* builder, bool enable);

// Enables or disables shadow casting for built renderable.
void FilaRenderableManagerBuilder_castShadows(FilaRenderableManagerBuilder* builder, bool enable);

// Enables or disables shadow receiving for built renderable.
void FilaRenderableManagerBuilder_receiveShadows(FilaRenderableManagerBuilder* builder, bool enable);

// Enables skinning buffer mode for built renderable.
void FilaRenderableManagerBuilder_enableSkinningBuffers(FilaRenderableManagerBuilder* builder, bool enabled);

// Binds a SkinningBuffer slice for built renderable.
void FilaRenderableManagerBuilder_skinning(FilaRenderableManagerBuilder* builder,
		FilaSkinningBuffer* skinningBuffer,
		size_t count,
		size_t offset);

// Enables morphing with MorphTargetBuffer for built renderable.
void FilaRenderableManagerBuilder_morphing(FilaRenderableManagerBuilder* builder,
		FilaMorphTargetBuffer* morphTargetBuffer);

// Sets renderable draw instance count and optional InstanceBuffer transforms.
void FilaRenderableManagerBuilder_instances(FilaRenderableManagerBuilder* builder,
		size_t instanceCount,
		FilaInstanceBuffer* instanceBuffer);

// Binds geometry for a primitive slot.
void FilaRenderableManagerBuilder_geometry(FilaRenderableManagerBuilder* builder,
		size_t index,
		FilaRenderablePrimitiveType type,
		FilaVertexBuffer* vertexBuffer,
		FilaIndexBuffer* indexBuffer,
		size_t offset,
		size_t count);

// Binds material instance for a primitive slot.
void FilaRenderableManagerBuilder_material(FilaRenderableManagerBuilder* builder,
		size_t index,
		const FilaMaterialInstance* materialInstance);

// Builds a renderable component into the given entity. Returns true on success.
bool FilaRenderableManagerBuilder_build(FilaRenderableManagerBuilder* builder, FilaEngine* engine, FilaEntity entity);

// Sets the material instance for a primitive at runtime.
void FilaRenderableManager_setMaterialInstanceAt(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		size_t primitiveIndex,
		const FilaMaterialInstance* materialInstance);

// Gets the material instance for a primitive.
FilaMaterialInstance* FilaRenderableManager_getMaterialInstanceAt(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		size_t primitiveIndex);

// Updates a renderable's skinning buffer association.
void FilaRenderableManager_setSkinningBuffer(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		FilaSkinningBuffer* skinningBuffer,
		size_t count,
		size_t offset);

// Updates morph target weights for a renderable.
void FilaRenderableManager_setMorphWeights(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		const float* weights,
		size_t count,
		size_t offset);

// Sets morph target buffer offset for a primitive.
void FilaRenderableManager_setMorphTargetBufferOffsetAt(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		uint8_t level,
		size_t primitiveIndex,
		size_t offset);

// Gets the morph target buffer bound to a renderable.
FilaMorphTargetBuffer* FilaRenderableManager_getMorphTargetBuffer(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Gets morph target count for a renderable.
size_t FilaRenderableManager_getMorphTargetCount(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Gets draw instance count for a renderable.
size_t FilaRenderableManager_getInstanceCount(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_RENDERABLE_MANAGER_H

