#ifndef FILAMENT_C_ENGINE_H
#define FILAMENT_C_ENGINE_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Creates a filament::Engine instance
FilaEngine* FilaEngine_create();

// Destroys a filament::Engine instance
void FilaEngine_destroy(FilaEngine** engine);

// Creates a filament::Renderer instance
FilaRenderer* FilaEngine_createRenderer(FilaEngine* engine);

// Destroys a filament::Renderer instance
void FilaEngine_destroyRenderer(FilaEngine* engine, FilaRenderer* renderer);

// Creates a filament::Scene instance.
FilaScene* FilaEngine_createScene(FilaEngine* engine);

// Destroys a filament::Scene instance.
void FilaEngine_destroyScene(FilaEngine* engine, FilaScene* scene);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_ENGINE_H
