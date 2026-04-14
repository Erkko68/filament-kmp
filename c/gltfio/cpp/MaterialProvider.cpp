#include <gltfio/MaterialProvider.h>
#include "../c/MaterialProvider.h"

using namespace filament;
using namespace filament::gltfio;

extern "C" {

void FilaMaterialProvider_destroy(FilaMaterialProvider* provider) {
    delete (MaterialProvider*) provider;
}

FilaMaterialProvider* FilaMaterialProvider_createUbershaderProvider(FilaEngine* engine, const void* archive, size_t archiveByteCount) {
    return (FilaMaterialProvider*) createUbershaderProvider((Engine*) engine, archive, archiveByteCount);
}

FilaMaterialProvider* FilaMaterialProvider_createJitShaderProvider(FilaEngine* engine) {
    return (FilaMaterialProvider*) createJitShaderProvider((Engine*) engine);
}

void FilaMaterialProvider_destroyMaterials(FilaMaterialProvider* provider) {
    ((MaterialProvider*) provider)->destroyMaterials();
}

size_t FilaMaterialProvider_getMaterialsCount(FilaMaterialProvider* provider) {
    return ((MaterialProvider*) provider)->getMaterialsCount();
}

}
