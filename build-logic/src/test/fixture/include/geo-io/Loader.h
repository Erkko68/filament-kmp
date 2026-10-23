#pragma once

#include <geo/Shape.h>

// A second module, whose types build on the first's.
namespace geo::io {

enum class Format { OBJ, SVG };

class GEO_PUBLIC Loader {
public:
    explicit Loader(Format format);
    ~Loader();
    Format GetFormat() const;
    Ref<Shape> Load(const char* path);
};

} // namespace geo::io
