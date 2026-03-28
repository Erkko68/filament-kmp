#include <stddef.h>
#include <stdint.h>

#include "filament/Material.h"
#include "filament/MaterialInstance.h"

// Function pointer assignments lock exported C signatures.
static FilaMaterialBuilder* (*g_mat_builder_create)(void) = FilaMaterialBuilder_create;
static void (*g_mat_builder_destroy)(FilaMaterialBuilder*) = FilaMaterialBuilder_destroy;
static void (*g_mat_builder_package)(FilaMaterialBuilder*, const void*, size_t) = FilaMaterialBuilder_package;
static FilaMaterial* (*g_mat_builder_build)(const FilaMaterialBuilder*, FilaEngine*) = FilaMaterialBuilder_build;
static FilaMaterialInstance* (*g_mat_create_instance)(const FilaMaterial*) = FilaMaterial_createInstance;
static const FilaMaterial* (*g_mat_instance_get_material)(const FilaMaterialInstance*) = FilaMaterialInstance_getMaterial;
static void (*g_mat_instance_set_float)(FilaMaterialInstance*, const char*, float) = FilaMaterialInstance_setParameterFloat;
static void (*g_mat_instance_set_float2)(FilaMaterialInstance*, const char*, float, float) = FilaMaterialInstance_setParameterFloat2;
static void (*g_mat_instance_set_float3)(FilaMaterialInstance*, const char*, float, float, float) = FilaMaterialInstance_setParameterFloat3;
static void (*g_mat_instance_set_float4)(FilaMaterialInstance*, const char*, float, float, float, float) = FilaMaterialInstance_setParameterFloat4;
static void (*g_mat_instance_set_int)(FilaMaterialInstance*, const char*, int32_t) = FilaMaterialInstance_setParameterInt;
static void (*g_mat_instance_set_uint)(FilaMaterialInstance*, const char*, uint32_t) = FilaMaterialInstance_setParameterUint;

void fila_material_signature_compile_only(void) {
    (void)g_mat_builder_create;
    (void)g_mat_builder_destroy;
    (void)g_mat_builder_package;
    (void)g_mat_builder_build;
    (void)g_mat_create_instance;
    (void)g_mat_instance_get_material;
    (void)g_mat_instance_set_float;
    (void)g_mat_instance_set_float2;
    (void)g_mat_instance_set_float3;
    (void)g_mat_instance_set_float4;
    (void)g_mat_instance_set_int;
    (void)g_mat_instance_set_uint;
}

