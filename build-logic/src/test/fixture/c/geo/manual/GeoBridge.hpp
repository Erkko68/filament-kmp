// The helpers only Geo can write, on top of the generated Bridge.hpp.
#pragma once

#include <algorithm>
#include <cstring>

#include <geo/Base.h>

#include "../generated/Bridge.hpp"

namespace geo_c {

// A reference for C to release: to the pointer, or to the one a Ref holds.
template<typename T>
T* retain(T* p) {
    if (p) p->addRef();
    return p;
}

template<typename T>
T* retain(const geo::Ref<T>& r) { return retain(r.get()); }

// convert(value) when r holds one; else its error, copied into C's buffer, and convert's empty result.
template<typename T, typename F>
auto result(const geo::Result<T>& r, char* error, uint32_t capacity, F convert) -> decltype(convert(r.get())) {
    if (!r.hasError()) return convert(r.get());
    if (error && capacity) {
        const std::string& message = r.getError();
        size_t n = std::min<size_t>(message.size(), capacity - 1);
        std::memcpy(error, message.data(), n);
        error[n] = '\0';
    }
    return {};
}

} // namespace geo_c
