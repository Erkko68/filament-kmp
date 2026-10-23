#pragma once

#include <cstddef>
#include <cstdint>
#include <string>

#define GEO_PUBLIC __attribute__((visibility("default")))

// What the API is built on; not API itself.
namespace geo {

// What a Ref counts: C adds and releases references, never deletes.
template<class T>
class RefTarget {
public:
    void addRef() const;
    void release() const;
};

template<class T>
class Ref {
public:
    Ref() = default;
    Ref(T* p);
    T* get() const;
};

// A value, or the error that stopped one.
template<class T>
class Result {
public:
    bool hasError() const;
    const T& get() const;
    const std::string& getError() const;
};

// An id C holds as an integer.
class Handle {
public:
    static Handle fromId(uint32_t id);
    uint32_t getId() const;

private:
    uint32_t mId = 0;
};

// A view of elements the caller keeps.
template<class T>
class Span {
public:
    using value_type = T;
    Span(const T* data, size_t size);
    size_t size() const;
    const T& operator[](size_t i) const;
};

// A buffer the library hands back through the callback once it's done with it.
class Blob {
public:
    using Callback = void (*)(void* buffer, size_t size, void* user);
    Blob(void* buffer, size_t size, Callback callback, void* user);
};

namespace detail {
// Shares Limits' C name: C binds its fields on Limits.
struct Limits {
    float mMin = 0;
};
} // namespace detail

} // namespace geo
