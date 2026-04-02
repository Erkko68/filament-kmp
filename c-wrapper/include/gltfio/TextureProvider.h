#ifndef FILAMENT_C_GLTFIO_TEXTURE_PROVIDER_H
#define FILAMENT_C_GLTFIO_TEXTURE_PROVIDER_H

#include <stdbool.h>

#include "filament/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

FilaGltfioTextureProvider* FilaGltfioTextureProvider_createStb(FilaEngine* engine);
FilaGltfioTextureProvider* FilaGltfioTextureProvider_createKtx2(FilaEngine* engine);
FilaGltfioTextureProvider* FilaGltfioTextureProvider_createWebp(FilaEngine* engine);
bool FilaGltfioTextureProvider_isWebpSupported(void);

void FilaGltfioTextureProvider_destroy(FilaGltfioTextureProvider* provider);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_GLTFIO_TEXTURE_PROVIDER_H

