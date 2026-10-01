#include "../generated/Includes.hpp"
#include "FilaGltfioManual.h"

#include <gltfio/materials/uberarchive.h>

#include <bit>

extern "C" {

const void* FilaGltfio_getUberarchiveData(void) { return UBERARCHIVE_DEFAULT_DATA; }

uint32_t FilaGltfio_getUberarchiveSize(void) { return UBERARCHIVE_DEFAULT_SIZE; }

// ResourceConfiguration's layout, so gltfPath can be set without naming the deprecated field.
struct ResourceConfigurationLayout {
    filament::Engine* engine;
    const char* gltfPath;
    bool normalizeSkinningWeights;
};
// Fails once upstream drops gltfPath: then call setConfiguration directly and delete this.
static_assert(sizeof(ResourceConfigurationLayout) == sizeof(filament::gltfio::ResourceConfiguration));

void FilaGltfioResourceLoader_setConfiguration(FilaGltfioResourceLoader* self, const FilaGltfioResourceConfiguration* config) {
    auto const& c = *fila::cpp(config);
    fila::cpp(self)->setConfiguration(std::bit_cast<filament::gltfio::ResourceConfiguration>(
            ResourceConfigurationLayout{ c.engine, "", c.normalizeSkinningWeights }));
}

} // extern "C"
