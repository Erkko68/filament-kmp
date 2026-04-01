#include "../../include/backend/Program.h"

#include <backend/Program.h>

struct FilaBackendProgram {
    filament::backend::Program* impl;
};

extern "C" {

FilaBackendProgram* FilaBackendProgram_create(void) {
    auto* program = new FilaBackendProgram;
    program->impl = new filament::backend::Program();
    return program;
}

void FilaBackendProgram_destroy(FilaBackendProgram* program) {
    if (!program) {
        return;
    }
    delete program->impl;
    delete program;
}

void FilaBackendProgram_setPriorityQueue(
        FilaBackendProgram* program, FilaBackendCompilerPriorityQueue priorityQueue) {
    if (!program) {
        return;
    }
    program->impl->priorityQueue(static_cast<filament::backend::CompilerPriorityQueue>(priorityQueue));
}

FilaBackendCompilerPriorityQueue FilaBackendProgram_getPriorityQueue(
        const FilaBackendProgram* program) {
    if (!program) {
        return FILA_BACKEND_COMPILER_PRIORITY_QUEUE_HIGH;
    }
    return static_cast<FilaBackendCompilerPriorityQueue>(program->impl->getPriorityQueue());
}

void FilaBackendProgram_setShaderLanguage(
        FilaBackendProgram* program, FilaBackendShaderLanguage shaderLanguage) {
    if (!program) {
        return;
    }
    program->impl->shaderLanguage(static_cast<filament::backend::ShaderLanguage>(shaderLanguage));
}

FilaBackendShaderLanguage FilaBackendProgram_getShaderLanguage(const FilaBackendProgram* program) {
    if (!program) {
        return FILA_BACKEND_SHADER_LANGUAGE_ESSL3;
    }
    return static_cast<FilaBackendShaderLanguage>(program->impl->getShaderLanguage());
}

void FilaBackendProgram_setShader(
        FilaBackendProgram* program, FilaBackendShaderStage shaderStage, const void* data,
        size_t size) {
    if (!program || !data || size == 0) {
        return;
    }
    program->impl->shader(static_cast<filament::backend::ShaderStage>(shaderStage), data, size);
}

void FilaBackendProgram_setCacheId(FilaBackendProgram* program, uint64_t cacheId) {
    if (!program) {
        return;
    }
    program->impl->cacheId(cacheId);
}

uint64_t FilaBackendProgram_getCacheId(const FilaBackendProgram* program) {
    if (!program) {
        return 0;
    }
    return program->impl->getCacheId();
}

void FilaBackendProgram_setMultiview(FilaBackendProgram* program, bool multiview) {
    if (!program) {
        return;
    }
    program->impl->multiview(multiview);
}

bool FilaBackendProgram_isMultiview(const FilaBackendProgram* program) {
    if (!program) {
        return false;
    }
    return program->impl->isMultiview();
}

}

