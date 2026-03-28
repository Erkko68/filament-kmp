#ifndef FILAMENT_C_VIEW_H
#define FILAMENT_C_VIEW_H

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Sets the scene used when rendering this view.
void FilaView_setScene(FilaView* view, FilaScene* scene);

// Returns the scene currently attached to this view.
FilaScene* FilaView_getScene(FilaView* view);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_VIEW_H

