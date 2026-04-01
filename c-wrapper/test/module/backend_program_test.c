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
        {
            char outShader[8];
            FilaBackendProgram_getShaderSize(program, FILA_BACKEND_SHADER_STAGE_VERTEX);
            FilaBackendProgram_copyShader(
                program, FILA_BACKEND_SHADER_STAGE_VERTEX, outShader, sizeof(outShader));
        }
    }

    FilaBackendProgram_setName(program, "test-program");
    {
        char outName[32];
        FilaBackendProgram_getNameSize(program);
        FilaBackendProgram_copyName(program, outName, sizeof(outName));
    }

    FilaBackendProgram_setSingleDescriptorBinding(
        program,
        0,
        "uTexture",
        FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT,
        1);
    FilaBackendProgram_getDescriptorBindingCount(program, 0);
    {
        FilaBackendDescriptorType outType = FILA_BACKEND_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
        FilaBackendDescriptorBinding outBinding = 0;
        FilaBackendProgram_getDescriptorBindingAt(program, 0, 0, &outType, &outBinding);
    }

    FilaBackendProgram_setSinglePushConstant(
        program,
        FILA_BACKEND_SHADER_STAGE_VERTEX,
        "pc0",
        FILA_BACKEND_CONSTANT_TYPE_INT);
    FilaBackendProgram_getPushConstantCount(program, FILA_BACKEND_SHADER_STAGE_VERTEX);
    {
        FilaBackendConstantType outType = FILA_BACKEND_CONSTANT_TYPE_BOOL;
        FilaBackendProgram_getPushConstantTypeAt(
            program,
            FILA_BACKEND_SHADER_STAGE_VERTEX,
            0,
            &outType);
    }

    FilaBackendProgram_setCacheId(program, 123u);
    FilaBackendProgram_getCacheId(program);

    FilaBackendProgram_setMultiview(program, true);
    FilaBackendProgram_isMultiview(program);

    FilaBackendProgram_destroy(program);
}

