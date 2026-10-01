// Helpers the generated forwarders call; filament's generated Includes.hpp includes this.
#pragma once

#include <array>
#include <bit>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <iterator>
#include <memory>
#include <optional>
#include <string_view>
#include <vector>

#include <utils/FixedCapacityVector.h>
#include <utils/Slice.h>
#include <utils/StaticString.h>

namespace fila {

// Called by a function whose C++ the target's headers leave out (#if guards around the declaration).
[[noreturn]] inline void unavailable(const char* name) {
    std::fprintf(stderr, "%s is unavailable on this platform\n", name);
    std::abort();
}

// C and C++ types naming one object (a handle, a math mirror): cpp() and c() convert pointers between them.
#define FILA_TYPE(C, ...) \
    inline __VA_ARGS__* cpp(C* p) { return reinterpret_cast<__VA_ARGS__*>(p); } \
    inline const __VA_ARGS__* cpp(const C* p) { return reinterpret_cast<const __VA_ARGS__*>(p); } \
    inline C* c(__VA_ARGS__* p) { return reinterpret_cast<C*>(p); } \
    inline const C* c(const __VA_ARGS__* p) { return reinterpret_cast<const C*>(p); }

// C's array as the FixedCapacityVector<T>, std::vector<T>, Slice<T> or std::array<T, N> the callee takes; element(i) makes each T. A Slice's
// elements live as long as the Items, until the end of the call.
template<typename F>
struct Items {
    uint32_t count;
    F element;
    mutable std::shared_ptr<void> storage;

    template<typename T>
    utils::FixedCapacityVector<T> vector() const {
        auto v = utils::FixedCapacityVector<T>::with_capacity(count);
        for (uint32_t i = 0; i < count; i++) v.push_back(T(element(i)));
        return v;
    }

    template<typename T>
    operator utils::FixedCapacityVector<T>() const { return vector<T>(); }

    template<typename T>
    operator std::vector<T>() const {
        std::vector<T> v;
        v.reserve(count);
        for (uint32_t i = 0; i < count; i++) v.push_back(T(element(i)));
        return v;
    }

    template<typename T, size_t N>
    operator std::array<T, N>() const {
        std::array<T, N> a{};
        for (uint32_t i = 0; i < count && i < N; i++) a[i] = T(element(i));
        return a;
    }

    template<typename T>
    operator utils::Slice<T>() const {
        auto v = std::make_shared<utils::FixedCapacityVector<std::remove_const_t<T>>>(vector<std::remove_const_t<T>>());
        storage = v;
        return { v->data(), v->size() };
    }
};

template<typename F>
Items<F> items(uint32_t count, F element) { return { count, element }; }

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

// C's array as the std::array<T, N> the callee takes by pointer or reference: element(i) makes each T, and
// store(t, i) writes the callee's changes back into C's array when the call ends.
template<typename F, typename S>
struct Updated {
    uint32_t count;
    F element;
    S store;
    std::shared_ptr<void> storage;
    void (*writeBack)(Updated&) = nullptr;

    template<typename T, size_t N>
    std::array<T, N>* array() {
        auto a = std::make_shared<std::array<T, N>>();
        for (uint32_t i = 0; i < count && i < N; i++) (*a)[i] = T(element(i));
        storage = a;
        writeBack = [](Updated& u) {
            auto& a = *static_cast<std::array<T, N>*>(u.storage.get());
            for (uint32_t i = 0; i < u.count && i < N; i++) u.store(a[i], i);
        };
        return a.get();
    }

    template<typename T, size_t N>
    operator std::array<T, N>*() { return array<T, N>(); }

    template<typename T, size_t N>
    operator std::array<T, N>&() { return *array<T, N>(); }

    ~Updated() { if (writeBack) writeBack(*this); }
};

template<typename F, typename S>
Updated<F, S> updated(uint32_t count, F element, S store) { return { count, element, store }; }

// Fills an array field from C's array, up to either's size.
template<typename T, size_t N, typename F>
void assign(T (&array)[N], const Items<F>& items) {
    for (uint32_t i = 0; i < items.count && i < N; i++) array[i] = T(items.element(i));
}

// Stores up to capacity of items into C's array; returns how many there are.
template<typename V, typename F>
uint32_t copy(const V& items, uint32_t capacity, F store) {
    for (uint32_t i = 0; i < capacity && i < std::size(items); i++) store(items[i], i);
    return uint32_t(std::size(items));
}

// C's nullable pointer as the std::optional the callee takes; convert(*p) makes its value.
template<typename T, typename F>
auto optional(const T* p, F convert) -> std::optional<decltype(convert(*p))> {
    if (!p) return std::nullopt;
    return convert(*p);
}

// Stores an optional's value, if it has one; returns whether it did.
template<typename T, typename F>
bool present(const std::optional<T>& o, F store) {
    if (o) store(*o);
    return o.has_value();
}

// StaticString only has a literal constructor. Everything taking one copies it (builderMakeName).
inline utils::StaticString staticString(const char* s) {
    static_assert(sizeof(utils::StaticString) == sizeof(std::string_view));
    return std::bit_cast<utils::StaticString>(std::string_view(s));
}

} // namespace fila
