#include <filament/Engine.h>
#include <filament/Fence.h>
#include <filament/IndexBuffer.h>
#include <filament/InstanceBuffer.h>
#include <filament/IndirectLight.h>
#include <filament/LightManager.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/Renderer.h>
#include <filament/RenderableManager.h>
#include <filament/Scene.h>
#include <filament/SkinningBuffer.h>
#include <filament/Skybox.h>
#include <filament/Stream.h>
#include <filament/SwapChain.h>
#include <filament/Texture.h>
#include <filament/TransformManager.h>
#include <filament/VertexBuffer.h>
#include <filament/View.h>
#include <filament/BufferObject.h>

#include <utils/Entity.h>

#include "../../include/filament/Engine.h" // Our C Header
#include "../../include/filament/Types.h"  // Our C Types

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}
} // namespace

extern "C" {

FilaEngine* FilaEngine_create() {
    return reinterpret_cast<FilaEngine*>(filament::Engine::create());
}

void FilaEngine_destroy(FilaEngine** engine) {
    if (engine && *engine) {
        auto cppEngine = reinterpret_cast<filament::Engine*>(*engine);
        filament::Engine::destroy(&cppEngine);
        *engine = nullptr;
    }
}

FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaRenderer*>(cppEngine->createRenderer());
}

void FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer) {
    if (!engine || !renderer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppRenderer = reinterpret_cast<filament::Renderer*>(renderer);
    cppEngine->destroy(cppRenderer);
}

FilaSwapChain* FilaEngine_createSwapChain(FilaEngine* engine, void* nativeWindow, uint64_t flags) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSwapChain*>(cppEngine->createSwapChain(nativeWindow, flags));
}

FilaSwapChain* FilaEngine_createSwapChainHeadless(FilaEngine* engine, uint32_t width, uint32_t height, uint64_t flags) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaSwapChain*>(cppEngine->createSwapChain(width, height, flags));
}

void FilaEngine_destroySwapChain(FilaEngine* engine, FilaSwapChain* swapChain) {
    if (!engine || !swapChain) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSwapChain = reinterpret_cast<filament::SwapChain*>(swapChain);
    cppEngine->destroy(cppSwapChain);
}

FilaScene* FilaEngine_createScene(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaScene*>(cppEngine->createScene());
}

void FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene) {
    if (!engine || !scene) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppEngine->destroy(cppScene);
}

FilaView* FilaEngine_createView(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaView*>(cppEngine->createView());
}

void FilaEngine_destroyView(FilaEngine* engine, FilaView* view) {
    if (!engine || !view) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppView = reinterpret_cast<filament::View*>(view);
    cppEngine->destroy(cppView);
}

FilaCamera* FilaEngine_createCamera(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaCamera*>(cppEngine->createCamera(toEntity(entity)));
}

FilaCamera* FilaEngine_getCameraComponent(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaCamera*>(cppEngine->getCameraComponent(toEntity(entity)));
}

void FilaEngine_destroyCameraComponent(FilaEngine* engine, FilaEntity entity) {
    if (!engine) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppEngine->destroyCameraComponent(toEntity(entity));
}

FilaFence* FilaEngine_createFence(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaFence*>(cppEngine->createFence());
}

void FilaEngine_destroyFence(FilaEngine* engine, FilaFence* fence) {
    if (!engine || !fence) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppFence = reinterpret_cast<filament::Fence*>(fence);
    cppEngine->destroy(cppFence);
}

void FilaEngine_destroyVertexBuffer(FilaEngine* engine, FilaVertexBuffer* vertexBuffer) {
    if (!engine || !vertexBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppVertexBuffer = reinterpret_cast<filament::VertexBuffer*>(vertexBuffer);
    cppEngine->destroy(cppVertexBuffer);
}

void FilaEngine_destroyIndexBuffer(FilaEngine* engine, FilaIndexBuffer* indexBuffer) {
    if (!engine || !indexBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppIndexBuffer = reinterpret_cast<filament::IndexBuffer*>(indexBuffer);
    cppEngine->destroy(cppIndexBuffer);
}

void FilaEngine_destroyMaterial(FilaEngine* engine, FilaMaterial* material) {
    if (!engine || !material) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMaterial = reinterpret_cast<filament::Material*>(material);
    cppEngine->destroy(cppMaterial);
}

void FilaEngine_destroyMaterialInstance(FilaEngine* engine, FilaMaterialInstance* materialInstance) {
    if (!engine || !materialInstance) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMaterialInstance = reinterpret_cast<filament::MaterialInstance*>(materialInstance);
    cppEngine->destroy(cppMaterialInstance);
}

void FilaEngine_destroyTexture(FilaEngine* engine, FilaTexture* texture) {
    if (!engine || !texture) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    cppEngine->destroy(cppTexture);
}

void FilaEngine_destroySkybox(FilaEngine* engine, FilaSkybox* skybox) {
    if (!engine || !skybox) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSkybox = reinterpret_cast<filament::Skybox*>(skybox);
    cppEngine->destroy(cppSkybox);
}

void FilaEngine_destroyIndirectLight(FilaEngine* engine, FilaIndirectLight* indirectLight) {
    if (!engine || !indirectLight) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppIndirectLight = reinterpret_cast<filament::IndirectLight*>(indirectLight);
    cppEngine->destroy(cppIndirectLight);
}

void FilaEngine_destroyColorGrading(FilaEngine* engine, FilaColorGrading* colorGrading) {
    if (!engine || !colorGrading) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppColorGrading = reinterpret_cast<filament::ColorGrading*>(colorGrading);
    cppEngine->destroy(cppColorGrading);
}

void FilaEngine_destroyRenderTarget(FilaEngine* engine, FilaRenderTarget* renderTarget) {
    if (!engine || !renderTarget) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppRenderTarget = reinterpret_cast<filament::RenderTarget*>(renderTarget);
    cppEngine->destroy(cppRenderTarget);
}

void FilaEngine_destroyStream(FilaEngine* engine, FilaStream* stream) {
    if (!engine || !stream) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    cppEngine->destroy(cppStream);
}

void FilaEngine_destroySkinningBuffer(FilaEngine* engine, FilaSkinningBuffer* skinningBuffer) {
    if (!engine || !skinningBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppSkinningBuffer = reinterpret_cast<filament::SkinningBuffer*>(skinningBuffer);
    cppEngine->destroy(cppSkinningBuffer);
}

void FilaEngine_destroyMorphTargetBuffer(FilaEngine* engine, FilaMorphTargetBuffer* morphTargetBuffer) {
    if (!engine || !morphTargetBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppMorphTargetBuffer = reinterpret_cast<filament::MorphTargetBuffer*>(morphTargetBuffer);
    cppEngine->destroy(cppMorphTargetBuffer);
}

void FilaEngine_destroyInstanceBuffer(FilaEngine* engine, FilaInstanceBuffer* instanceBuffer) {
    if (!engine || !instanceBuffer) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppInstanceBuffer = reinterpret_cast<filament::InstanceBuffer*>(instanceBuffer);
    cppEngine->destroy(cppInstanceBuffer);
}

void FilaEngine_destroyBufferObject(FilaEngine* engine, FilaBufferObject* bufferObject) {
    if (!engine || !bufferObject) {
        return;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBufferObject = reinterpret_cast<filament::BufferObject*>(bufferObject);
    cppEngine->destroy(cppBufferObject);
}

FilaTransformManager* FilaEngine_getTransformManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaTransformManager*>(&cppEngine->getTransformManager());
}

FilaLightManager* FilaEngine_getLightManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaLightManager*>(&cppEngine->getLightManager());
}

FilaRenderableManager* FilaEngine_getRenderableManager(FilaEngine* engine) {
    if (!engine) {
        return nullptr;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaRenderableManager*>(&cppEngine->getRenderableManager());
}

}
