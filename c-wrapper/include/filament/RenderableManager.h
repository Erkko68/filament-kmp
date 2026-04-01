#ifndef FILAMENT_C_RENDERABLE_MANAGER_H
#define FILAMENT_C_RENDERABLE_MANAGER_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "Box.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaRenderablePrimitiveType {
	FILA_RENDERABLE_PRIMITIVE_POINTS = 0,
	FILA_RENDERABLE_PRIMITIVE_LINES = 1,
	FILA_RENDERABLE_PRIMITIVE_TRIANGLES = 4,
} FilaRenderablePrimitiveType;

typedef enum FilaRenderableGeometryType {
	FILA_RENDERABLE_GEOMETRY_TYPE_DYNAMIC = 0,
	FILA_RENDERABLE_GEOMETRY_TYPE_STATIC_BOUNDS = 1,
	FILA_RENDERABLE_GEOMETRY_TYPE_STATIC = 2,
} FilaRenderableGeometryType;

typedef struct FilaRenderableBone {
	float unitQuaternion[4]; // quaternion in w,x,y,z order
	float translation[3];
	float reserved;
} FilaRenderableBone;

typedef struct FilaRenderableBoneIndexAndWeight {
	float boneIndex;
	float weight;
} FilaRenderableBoneIndexAndWeight;

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

// Sets and gets renderable channel.
void FilaRenderableManager_setChannel(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		uint8_t channel);
uint8_t FilaRenderableManager_getChannel(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Enables or disables frustum culling for a renderable instance.
void FilaRenderableManager_setCulling(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);

// Returns whether frustum culling is enabled for a renderable instance.
bool FilaRenderableManager_isCullingEnabled(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);

// Runtime toggles and query helpers.
void FilaRenderableManager_setFogEnabled(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);
bool FilaRenderableManager_getFogEnabled(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);
void FilaRenderableManager_setLightChannel(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		unsigned int channel,
		bool enable);
bool FilaRenderableManager_getLightChannel(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		unsigned int channel);
void FilaRenderableManager_setCastShadows(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);
void FilaRenderableManager_setReceiveShadows(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);
void FilaRenderableManager_setScreenSpaceContactShadows(FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		bool enable);
bool FilaRenderableManager_isShadowCaster(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);
bool FilaRenderableManager_isShadowReceiver(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance);
bool FilaRenderableManager_isScreenSpaceContactShadowsEnabled(const FilaRenderableManager* manager,
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

// Builder advanced flags.
void FilaRenderableManagerBuilder_channel(FilaRenderableManagerBuilder* builder, uint8_t channel);
void FilaRenderableManagerBuilder_lightChannel(FilaRenderableManagerBuilder* builder, unsigned int channel, bool enable);
void FilaRenderableManagerBuilder_fog(FilaRenderableManagerBuilder* builder, bool enabled);
void FilaRenderableManagerBuilder_screenSpaceContactShadows(FilaRenderableManagerBuilder* builder, bool enable);
void FilaRenderableManagerBuilder_blendOrder(FilaRenderableManagerBuilder* builder, size_t primitiveIndex, uint16_t order);
void FilaRenderableManagerBuilder_globalBlendOrderEnabled(FilaRenderableManagerBuilder* builder, size_t primitiveIndex, bool enabled);

// Sets geometry mutability constraints for the built renderable.
void FilaRenderableManagerBuilder_geometryType(FilaRenderableManagerBuilder* builder,
		FilaRenderableGeometryType geometryType);

// Enables skinning buffer mode for built renderable.
void FilaRenderableManagerBuilder_enableSkinningBuffers(FilaRenderableManagerBuilder* builder, bool enabled);

// Binds a SkinningBuffer slice for built renderable.
void FilaRenderableManagerBuilder_skinning(FilaRenderableManagerBuilder* builder,
		FilaSkinningBuffer* skinningBuffer,
		size_t count,
		size_t offset);

// Enables classic skinning with an explicit bone count and identity/default transforms.
void FilaRenderableManagerBuilder_skinningBoneCount(FilaRenderableManagerBuilder* builder,
		size_t boneCount);

// Enables classic skinning with initial column-major 4x4 float bone transforms.
void FilaRenderableManagerBuilder_skinningMat4f(FilaRenderableManagerBuilder* builder,
		size_t boneCount,
		const float* transforms4x4);

// Enables classic skinning with initial quaternion+translation bones.
void FilaRenderableManagerBuilder_skinningBones(FilaRenderableManagerBuilder* builder,
		size_t boneCount,
		const FilaRenderableBone* bones);

// Defines advanced skinning index/weight pairs for a primitive.
void FilaRenderableManagerBuilder_boneIndicesAndWeights(FilaRenderableManagerBuilder* builder,
		size_t primitiveIndex,
		const FilaRenderableBoneIndexAndWeight* indicesAndWeights,
		size_t count,
		size_t bonesPerVertex);

// Defines variable-length advanced skinning index/weight pairs per vertex for a primitive.
// The sum of all entries in pairsPerVertex must equal pairCount.
void FilaRenderableManagerBuilder_boneIndicesAndWeightsVector(FilaRenderableManagerBuilder* builder,
		size_t primitiveIndex,
		const FilaRenderableBoneIndexAndWeight* indicesAndWeights,
		size_t pairCount,
		const size_t* pairsPerVertex,
		size_t vertexCount);

// Enables morphing with MorphTargetBuffer for built renderable.
void FilaRenderableManagerBuilder_morphing(FilaRenderableManagerBuilder* builder,
		FilaMorphTargetBuffer* morphTargetBuffer);

// Enables legacy morphing with the requested morph target count.
void FilaRenderableManagerBuilder_morphingLegacy(FilaRenderableManagerBuilder* builder,
		size_t targetCount);

// Sets morph target buffer offset range for a primitive.
void FilaRenderableManagerBuilder_morphingOffset(FilaRenderableManagerBuilder* builder,
		uint8_t level,
		size_t primitiveIndex,
		size_t offset);

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

// Binds geometry for a primitive slot with explicit min/max index range.
void FilaRenderableManagerBuilder_geometryIndexedRange(FilaRenderableManagerBuilder* builder,
	 size_t index,
	 FilaRenderablePrimitiveType type,
	 FilaVertexBuffer* vertexBuffer,
	 FilaIndexBuffer* indexBuffer,
	 size_t offset,
	 size_t minIndex,
	 size_t maxIndex,
	 size_t count);

// Binds material instance for a primitive slot.
void FilaRenderableManagerBuilder_material(FilaRenderableManagerBuilder* builder,
	 size_t index,
	 const FilaMaterialInstance* materialInstance);

// Sets object-space axis-aligned bounding box for the built renderable.
void FilaRenderableManagerBuilder_boundingBox(FilaRenderableManagerBuilder* builder,
	 const FilaBox* boundingBox);

// Builds a renderable component into the given entity. Returns true on success.
bool FilaRenderableManagerBuilder_build(FilaRenderableManagerBuilder* builder, FilaEngine* engine, FilaEntity entity);

// Sets object-space axis-aligned bounding box for a renderable instance.
void FilaRenderableManager_setAxisAlignedBoundingBox(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 const FilaBox* boundingBox);

// Gets object-space axis-aligned bounding box for a renderable instance. Returns false on invalid input.
bool FilaRenderableManager_getAxisAlignedBoundingBox(const FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 FilaBox* outBoundingBox);

// Rebinds geometry for a primitive at runtime.
void FilaRenderableManager_setGeometryAt(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex,
	 FilaRenderablePrimitiveType type,
	 FilaVertexBuffer* vertexBuffer,
	 FilaIndexBuffer* indexBuffer,
	 size_t offset,
	 size_t count);

// Sets the material instance for a primitive at runtime.
void FilaRenderableManager_setMaterialInstanceAt(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex,
	 const FilaMaterialInstance* materialInstance);

// Gets the material instance for a primitive.
FilaMaterialInstance* FilaRenderableManager_getMaterialInstanceAt(const FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex);
void FilaRenderableManager_clearMaterialInstanceAt(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex);
void FilaRenderableManager_setBlendOrderAt(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex,
	 uint16_t order);
uint16_t FilaRenderableManager_getBlendOrderAt(const FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex);
void FilaRenderableManager_setGlobalBlendOrderEnabledAt(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex,
	 bool enabled);
bool FilaRenderableManager_isGlobalBlendOrderEnabledAt(const FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 size_t primitiveIndex);

// Returns enabled vertex attribute slots as a bitmask for a primitive.
uint32_t FilaRenderableManager_getEnabledAttributesAt(const FilaRenderableManager* manager,
		FilaRenderableManagerInstance instance,
		size_t primitiveIndex);

// Updates a renderable's skinning buffer association.
void FilaRenderableManager_setSkinningBuffer(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 FilaSkinningBuffer* skinningBuffer,
	 size_t count,
	 size_t offset);

// Updates classic skinning bone transforms from column-major 4x4 float matrices.
void FilaRenderableManager_setBonesMat4f(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 const float* transforms4x4,
	 size_t count,
	 size_t offset);

// Updates classic skinning bone transforms from quaternion+translation pairs.
void FilaRenderableManager_setBones(FilaRenderableManager* manager,
	 FilaRenderableManagerInstance instance,
	 const FilaRenderableBone* bones,
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

