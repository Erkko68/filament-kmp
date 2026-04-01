#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "filament/Color.h"
#include "filament/Engine.h"
#include "filament/Material.h"
#include "filament/MaterialInstance.h"

static int validateMaterialMetadata(
        FilaEngine* engine,
        const FilaMaterial* material,
        const char* label,
        bool requireNonZeroCount) {
    if (!material) {
        printf("%s material missing\n", label);
        return 1;
    }

    const size_t parameterCount = FilaMaterial_getParameterCount(material);
    if (requireNonZeroCount && parameterCount == 0u) {
        printf("%s material unexpectedly has zero parameters\n", label);
        return 1;
    }

    {
        FilaMaterialParameterInfo firstInfo[1];
        const size_t firstWritten = FilaMaterial_getParameters(material, firstInfo, 1u);
        if (firstWritten > 1u) {
            printf("%s first-parameter contract mismatch\n", label);
            return 1;
        }
        if (firstWritten == 1u && (firstInfo[0].name == NULL || firstInfo[0].name[0] == '\0')) {
            printf("%s first parameter has invalid name\n", label);
            return 1;
        }
    }

    if (FilaMaterial_getParameters(material, (FilaMaterialParameterInfo*)0, 16u) != 0u) {
        printf("%s null output pointer contract mismatch\n", label);
        return 1;
    }

    {
        FilaMaterialParameterInfo infos[64];
        const size_t requested = parameterCount < 64u ? parameterCount : 64u;
        const size_t written = FilaMaterial_getParameters(material, infos, requested);
        if (written > requested) {
            printf("%s parameter write count mismatch\n", label);
            return 1;
        }

        for (size_t i = 0u; i < written; ++i) {
            if (infos[i].name == NULL || infos[i].name[0] == '\0') {
                printf("%s parameter entry has invalid name\n", label);
                return 1;
            }
            if (infos[i].isSampler && infos[i].isSubpass) {
                printf("%s parameter entry has conflicting type flags\n", label);
                return 1;
            }
            if (!FilaMaterial_hasParameter(material, infos[i].name)) {
                printf("%s hasParameter mismatch\n", label);
                return 1;
            }
            if (FilaMaterial_isSampler(material, infos[i].name) != infos[i].isSampler) {
                printf("%s isSampler mismatch\n", label);
                return 1;
            }
        }
    }

    {
        FilaMaterialInstance* defaultInstance = FilaMaterial_getDefaultInstance((FilaMaterial*)material);
        if (!defaultInstance) {
            printf("%s default instance missing\n", label);
            return 1;
        }
        if (FilaMaterialInstance_getMaterial(defaultInstance) != material) {
            printf("%s default instance parent mismatch\n", label);
            return 1;
        }
    }

    (void)engine;
    return 0;
}

static int readBinaryFile(const char* path, unsigned char** outData, size_t* outSize) {
    FILE* fp = fopen(path, "rb");
    if (!fp) {
        return 0;
    }
    if (fseek(fp, 0, SEEK_END) != 0) {
        fclose(fp);
        return 0;
    }
    long end = ftell(fp);
    if (end <= 0) {
        fclose(fp);
        return 0;
    }
    if (fseek(fp, 0, SEEK_SET) != 0) {
        fclose(fp);
        return 0;
    }

    unsigned char* data = (unsigned char*)malloc((size_t)end);
    if (!data) {
        fclose(fp);
        return 0;
    }
    if (fread(data, 1u, (size_t)end, fp) != (size_t)end) {
        free(data);
        fclose(fp);
        return 0;
    }
    fclose(fp);
    *outData = data;
    *outSize = (size_t)end;
    return 1;
}

int main(void) {
    printf("Running functionality_color_options_material_metadata...\n");

    // Exercise APIs that should be safe on null handles.
    const char* name = FilaMaterial_getName((const FilaMaterial*)0);
    if (name != NULL) {
        printf("Expected null material name for null material handle\n");
        return 1;
    }

    if (FilaMaterial_hasParameter((const FilaMaterial*)0, "baseColor")) {
        printf("Unexpected parameter presence on null material\n");
        return 1;
    }

    if (FilaMaterial_isSampler((const FilaMaterial*)0, "baseColor")) {
        printf("Unexpected sampler classification on null material\n");
        return 1;
    }

    if (FilaMaterial_getParameterCount((const FilaMaterial*)0) != 0) {
        printf("Unexpected parameter count on null material\n");
        return 1;
    }

    {
        FilaMaterialParameterInfo parameterInfos[2];
        if (FilaMaterial_getParameters((const FilaMaterial*)0, parameterInfos, 2u) != 0u ||
                FilaMaterial_getParameters((const FilaMaterial*)0, (FilaMaterialParameterInfo*)0, 0u) != 0u) {
            printf("Unexpected parameter metadata readback on null material\n");
            return 1;
        }
    }

    if (FilaMaterial_getRequiredAttributes((const FilaMaterial*)0) != 0u) {
        printf("Unexpected required attributes on null material\n");
        return 1;
    }

    if (FilaMaterial_getDefaultInstance((FilaMaterial*)0) != (FilaMaterialInstance*)0) {
        printf("Unexpected default instance on null material\n");
        return 1;
    }

    {
        FilaEngine* engine = FilaEngine_create();
        if (!engine) {
            printf("Engine creation failed\n");
            return 1;
        }

        {
            const FilaMaterial* defaultMaterial = FilaEngine_getDefaultMaterial(engine);
            if (validateMaterialMetadata(engine, defaultMaterial, "Default", false)) {
                FilaEngine_destroy(&engine);
                return 1;
            }
        }

        {
            const char* fixturePath = getenv("FILA_TEST_MATERIAL_PACKAGE");
            if (fixturePath && fixturePath[0] != '\0') {
                unsigned char* packageData = NULL;
                size_t packageSize = 0u;
                if (!readBinaryFile(fixturePath, &packageData, &packageSize)) {
                    printf("Failed to read FILA_TEST_MATERIAL_PACKAGE: %s\n", fixturePath);
                    FilaEngine_destroy(&engine);
                    return 1;
                }

                FilaMaterialBuilder* builder = FilaMaterialBuilder_create();
                FilaMaterialBuilder_package(builder, packageData, packageSize);
                FilaMaterial* material = FilaMaterialBuilder_build(builder, engine);
                free(packageData);
                FilaMaterialBuilder_destroy(builder);

                if (!material) {
                    printf("Failed to build material from FILA_TEST_MATERIAL_PACKAGE\n");
                    FilaEngine_destroy(&engine);
                    return 1;
                }

                if (validateMaterialMetadata(engine, material, "Fixture", true)) {
                    FilaEngine_destroyMaterial(engine, material);
                    FilaEngine_destroy(&engine);
                    return 1;
                }
                FilaEngine_destroyMaterial(engine, material);
            } else {
                printf("FILA_TEST_MATERIAL_PACKAGE not set; skipping fixture-backed metadata checks\n");
            }
        }

        FilaEngine_destroy(&engine);
    }

    printf("functionality_color_options_material_metadata completed\n");
    return 0;
}

