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

void FilaBackendProgram_setCacheId(FilaBackendProgram* program, uint64_t cacheId);
uint64_t FilaBackendProgram_getCacheId(const FilaBackendProgram* program);

void FilaBackendProgram_setMultiview(FilaBackendProgram* program, bool multiview);
bool FilaBackendProgram_isMultiview(const FilaBackendProgram* program);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_PROGRAM_H

