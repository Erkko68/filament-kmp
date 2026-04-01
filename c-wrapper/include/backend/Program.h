#ifndef FILAMENT_C_BACKEND_PROGRAM_H
#define FILAMENT_C_BACKEND_PROGRAM_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "DriverEnums.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendProgram FilaBackendProgram;

typedef struct FilaBackendProgramDescriptorBindingEntry {
    const char* name;
    FilaBackendDescriptorType type;
    FilaBackendDescriptorBinding binding;
} FilaBackendProgramDescriptorBindingEntry;

typedef struct FilaBackendProgramPushConstantEntry {
    const char* name;
    FilaBackendConstantType type;
} FilaBackendProgramPushConstantEntry;

FilaBackendProgram* FilaBackendProgram_create(void);
void FilaBackendProgram_destroy(FilaBackendProgram* program);

void FilaBackendProgram_setPriorityQueue(
    FilaBackendProgram* program, FilaBackendCompilerPriorityQueue priorityQueue);
FilaBackendCompilerPriorityQueue FilaBackendProgram_getPriorityQueue(
    const FilaBackendProgram* program);

void FilaBackendProgram_setShaderLanguage(
    FilaBackendProgram* program, FilaBackendShaderLanguage shaderLanguage);
FilaBackendShaderLanguage FilaBackendProgram_getShaderLanguage(const FilaBackendProgram* program);

void FilaBackendProgram_setShader(
    FilaBackendProgram* program, FilaBackendShaderStage shaderStage, const void* data, size_t size);
size_t FilaBackendProgram_getShaderSize(
    const FilaBackendProgram* program, FilaBackendShaderStage shaderStage);
size_t FilaBackendProgram_copyShader(
    const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, void* outData,
    size_t outDataSize);

void FilaBackendProgram_setName(FilaBackendProgram* program, const char* name);
size_t FilaBackendProgram_getNameSize(const FilaBackendProgram* program);
size_t FilaBackendProgram_copyName(
    const FilaBackendProgram* program, char* outName, size_t outNameSize);

// Replaces descriptor bindings for a set with the provided entries.
bool FilaBackendProgram_setDescriptorBindings(
    FilaBackendProgram* program, FilaBackendDescriptorSet set,
    const FilaBackendProgramDescriptorBindingEntry* entries, uint32_t count);

void FilaBackendProgram_setSingleDescriptorBinding(
    FilaBackendProgram* program, FilaBackendDescriptorSet set, const char* name,
    FilaBackendDescriptorType type, FilaBackendDescriptorBinding binding);
uint32_t FilaBackendProgram_getDescriptorBindingCount(
    const FilaBackendProgram* program, FilaBackendDescriptorSet set);
bool FilaBackendProgram_getDescriptorBindingAt(
    const FilaBackendProgram* program, FilaBackendDescriptorSet set, uint32_t index,
    FilaBackendDescriptorType* outType, FilaBackendDescriptorBinding* outBinding);

// Replaces push constants for a shader stage with the provided entries.
bool FilaBackendProgram_setPushConstants(
    FilaBackendProgram* program, FilaBackendShaderStage shaderStage,
    const FilaBackendProgramPushConstantEntry* entries, uint32_t count);

void FilaBackendProgram_setSinglePushConstant(
    FilaBackendProgram* program, FilaBackendShaderStage shaderStage, const char* name,
    FilaBackendConstantType type);
uint32_t FilaBackendProgram_getPushConstantCount(
    const FilaBackendProgram* program, FilaBackendShaderStage shaderStage);
bool FilaBackendProgram_getPushConstantTypeAt(
    const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, uint32_t index,
    FilaBackendConstantType* outType);

void FilaBackendProgram_setCacheId(FilaBackendProgram* program, uint64_t cacheId);
uint64_t FilaBackendProgram_getCacheId(const FilaBackendProgram* program);

void FilaBackendProgram_setMultiview(FilaBackendProgram* program, bool multiview);
bool FilaBackendProgram_isMultiview(const FilaBackendProgram* program);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PROGRAM_H

