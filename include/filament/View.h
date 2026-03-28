#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include <stdbool.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Sets the scene used when rendering this view.
void FilaView_setScene(FilaView* view, FilaScene* scene);

// Returns the scene currently attached to this view.
FilaScene* FilaView_getScene(FilaView* view);

// C representation of filament::Viewport.
typedef struct FilaViewport {
	int32_t left;
	int32_t bottom;
	uint32_t width;
	uint32_t height;
} FilaViewport;

// Sets the rectangular region rendered by this view.
void FilaView_setViewport(FilaView* view, FilaViewport viewport);

// Gets the rectangular region rendered by this view.
FilaViewport FilaView_getViewport(const FilaView* view);

// Sets the camera used when rendering this view.
void FilaView_setCamera(FilaView* view, FilaCamera* camera);

// Returns true when a camera is currently attached to this view.
bool FilaView_hasCamera(const FilaView* view);

// Returns the camera attached to this view, or NULL if none is set.
FilaCamera* FilaView_getCamera(FilaView* view);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEW_H

