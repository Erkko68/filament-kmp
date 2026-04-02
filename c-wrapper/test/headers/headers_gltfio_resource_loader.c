#include "gltfio/ResourceLoader.h"
#include "gltfio/TextureProvider.h"

void test_headers_gltfio_resource_loader(void) {
    FilaGltfioResourceLoader* loader = FilaGltfioResourceLoader_create((FilaEngine*)0, (const char*)0, false);
    FilaGltfioResourceLoader_setConfiguration(loader, (FilaEngine*)0, (const char*)0, false);
    (void)FilaGltfioResourceLoader_hasResourceData((const FilaGltfioResourceLoader*)0, "uri");
    (void)FilaGltfioResourceLoader_addResourceDataCopy(loader, "uri", "abc", 3u);
    FilaGltfioResourceLoader_addTextureProvider(loader, "image/png", (FilaGltfioTextureProvider*)0);
    FilaGltfioResourceLoader_evictResourceData(loader);
    (void)FilaGltfioResourceLoader_loadResources(loader, (FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioResourceLoader_asyncBeginLoad(loader, (FilaGltfioFilamentAsset*)0);
    (void)FilaGltfioResourceLoader_asyncGetLoadProgress((const FilaGltfioResourceLoader*)0);
    FilaGltfioResourceLoader_asyncUpdateLoad(loader);
    FilaGltfioResourceLoader_asyncCancelLoad(loader);
    FilaGltfioResourceLoader_destroy(loader);
}

