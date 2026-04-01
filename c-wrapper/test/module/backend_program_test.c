#include "backend/Program.h"

void backend_program_test(void) {
    FilaBackendProgram* program = FilaBackendProgram_create();

    FilaBackendProgram_setPriorityQueue(program, FILA_BACKEND_COMPILER_PRIORITY_QUEUE_CRITICAL);
    FilaBackendProgram_getPriorityQueue(program);

    FilaBackendProgram_setShaderLanguage(program, FILA_BACKEND_SHADER_LANGUAGE_SPIRV);
    FilaBackendProgram_getShaderLanguage(program);

    {
        const char shaderBytes[4] = { 't', 'e', 's', 't' };
        FilaBackendProgram_setShader(program, FILA_BACKEND_SHADER_STAGE_VERTEX, shaderBytes, 4);
    }

    FilaBackendProgram_setCacheId(program, 123u);
    FilaBackendProgram_getCacheId(program);

    FilaBackendProgram_setMultiview(program, true);
    FilaBackendProgram_isMultiview(program);

    FilaBackendProgram_destroy(program);
}

