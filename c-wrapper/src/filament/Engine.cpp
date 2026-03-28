#include <filament/Engine.h>
#include <filament/Renderer.h>
#include <filament/Scene.h>
#include <filament/SwapChain.h>
#include <filament/View.h>

#include <utils/Entity.h>

#include "filament/Engine.h" // Our C Header
#include "filament/Types.h"  // Our C Types

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

}
