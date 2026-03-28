#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include <stdbool.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Sets the scene used when rendering this view.
void FilaView_setScene(FilaView* view, FilaScene* scene);

// Returns the scene currently attached to this view.
FilaScene* FilaView_getScene(FilaView* view);

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

