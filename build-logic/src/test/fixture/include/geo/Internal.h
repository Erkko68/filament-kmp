#pragma once

#include <geo/Base.h>

namespace geo {

// Left out whole, and everything taking it: -geo::Debug.
class GEO_PUBLIC Debug {
public:
    void Dump();
};

// A handle without members: -geo::Registry::.*
class GEO_PUBLIC Registry {
public:
    static Registry& Get();
    void Clear();
};

} // namespace geo
