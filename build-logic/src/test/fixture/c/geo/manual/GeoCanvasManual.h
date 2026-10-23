// Canvas's functions written by hand: what the generator can't write, and what it would write otherwise.
#ifndef GEO_CANVAS_MANUAL_H
#define GEO_CANVAS_MANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Canvas::Adopt(Shape&&): moves the shape in.
void GeoCanvas_Adopt(GeoCanvas* self, GeoShape* shape);

// Canvas::GetDefault(), which the generator binds too: this one wins.
GeoCanvas* GeoCanvas_GetDefault(void);

#ifdef __cplusplus
}
#endif

#endif // GEO_CANVAS_MANUAL_H
