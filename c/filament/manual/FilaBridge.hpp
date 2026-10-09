// The helpers only Filament can write, on top of the generated Bridge.hpp; filament's generated Includes.hpp includes this.
#pragma once

#include <bit>
#include <string_view>

#include <utils/FixedCapacityVector.h>
#include <utils/Slice.h>
#include <utils/StaticString.h>

#include "../generated/Bridge.hpp"

namespace fila {

// C's callback as the utils::Invocable the callee takes: the lambda, or an empty one (unset) when C passed NULL.
template<typename L>
struct Callable {
    bool set;
    L lambda;

    template<typename T>
    operator T() && { return set ? T(std::move(lambda)) : T(); }
};

template<typename L>
Callable<L> callable(bool set, L lambda) { return { set, std::move(lambda) }; }

// StaticString only has a literal constructor. Everything taking one copies it (builderMakeName).
inline utils::StaticString staticString(const char* s) {
    static_assert(sizeof(utils::StaticString) == sizeof(std::string_view));
    return std::bit_cast<utils::StaticString>(std::string_view(s));
}

} // namespace fila
