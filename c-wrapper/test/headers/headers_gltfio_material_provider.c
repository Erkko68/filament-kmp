#include "gltfio/MaterialProvider.h"

void test_headers_gltfio_material_provider(void) {
    (void)FilaGltfioMaterialProvider_createUbershaderProvider((FilaEngine*)0, (const void*)0, 0u);
    FilaGltfioMaterialProvider_destroyMaterials((FilaGltfioMaterialProvider*)0);
    (void)FilaGltfioMaterialProvider_getMaterialsCount((const FilaGltfioMaterialProvider*)0);
    FilaGltfioMaterialProvider_destroy((FilaGltfioMaterialProvider*)0);
}

