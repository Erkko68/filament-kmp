// What the generator can't write of Canvas.
#ifndef GEO_CANVAS_MANUAL_H
#define GEO_CANVAS_MANUAL_H

#include "../generated/Types.h"

#ifdef __cplusplus
extern "C" {
#endif

// Canvas::Adopt(Shape&&): moves the shape in.
void GeoCanvas_Adopt(GeoCanvas* self, GeoShape* shape);

#ifdef __cplusplus
}
#endif

#endif // GEO_CANVAS_MANUAL_H
