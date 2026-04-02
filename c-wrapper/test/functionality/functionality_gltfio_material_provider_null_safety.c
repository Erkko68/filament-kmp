#include <stdio.h>

#include "gltfio/MaterialProvider.h"

int main(void) {
    printf("Running functionality_gltfio_material_provider_null_safety...\n");

    if (FilaGltfioMaterialProvider_createUbershaderProvider((FilaEngine*)0, (const void*)0, 0u) != (FilaGltfioMaterialProvider*)0 ||
            FilaGltfioMaterialProvider_getMaterialsCount((const FilaGltfioMaterialProvider*)0) != 0u) {
        printf("MaterialProvider null defaults mismatch\n");
        return 1;
    }

    FilaGltfioMaterialProvider_destroyMaterials((FilaGltfioMaterialProvider*)0);
    FilaGltfioMaterialProvider_destroy((FilaGltfioMaterialProvider*)0);

    printf("functionality_gltfio_material_provider_null_safety completed\n");
    return 0;
}

