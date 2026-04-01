#include <stdio.h>

#include "backend/Program.h"

int main(void) {
    FilaBackendProgram* program = FilaBackendProgram_create();
    if (!program) {
        printf("Program create failed\n");
        return 1;
    }

    FilaBackendProgram_setName(program, "backend-program");
    {
        char outName[64] = {0};
        size_t n = FilaBackendProgram_copyName(program, outName, sizeof(outName));
        if (n == 0 || outName[0] == '\0') {
            printf("Program name copy failed\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
    }

    {
        const char shaderBytes[8] = { 's', 'h', 'a', 'd', 'e', 'r', '\0', '\0' };
        FilaBackendProgram_setShader(program, FILA_BACKEND_SHADER_STAGE_VERTEX, shaderBytes, sizeof(shaderBytes));
        if (FilaBackendProgram_getShaderSize(program, FILA_BACKEND_SHADER_STAGE_VERTEX) == 0) {
            printf("Program shader set failed\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
        FilaBackendProgram_clearShader(program, FILA_BACKEND_SHADER_STAGE_VERTEX);
    }

    {
        FilaBackendProgramDescriptorBindingEntry entries[2] = {
            { "uTex0", FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT, 0 },
            { "uTex1", FILA_BACKEND_DESCRIPTOR_TYPE_SAMPLER_2D_FLOAT, 1 },
        };
        if (!FilaBackendProgram_setDescriptorBindings(program, 0, entries, 2)) {
            printf("Program descriptor set failed\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
        if (FilaBackendProgram_getDescriptorBindingCount(program, 0) != 2) {
            printf("Program descriptor count mismatch\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
        FilaBackendProgram_clearDescriptorBindings(program, 0);
    }

    {
        FilaBackendProgramPushConstantEntry constants[2] = {
            { "pc0", FILA_BACKEND_CONSTANT_TYPE_INT },
            { "pc1", FILA_BACKEND_CONSTANT_TYPE_FLOAT },
        };
        if (!FilaBackendProgram_setPushConstants(program, FILA_BACKEND_SHADER_STAGE_VERTEX, constants, 2)) {
            printf("Program push constants set failed\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
        if (FilaBackendProgram_getPushConstantCount(program, FILA_BACKEND_SHADER_STAGE_VERTEX) != 2) {
            printf("Program push constant count mismatch\n");
            FilaBackendProgram_destroy(program);
            return 1;
        }
        FilaBackendProgram_clearPushConstants(program, FILA_BACKEND_SHADER_STAGE_VERTEX);
    }

    FilaBackendProgram_destroy(program);
    return 0;
}

