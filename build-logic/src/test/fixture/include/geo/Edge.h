#pragma once

#include <geo/Shape.h>

#include <memory>
#include <vector>

// What C++ allows that C has no word for.
namespace geo {

// A const field has no setter; an array of bytes stays bytes.
struct Flags {
    const uint32_t mId = 7;
    uint32_t mBits : 3;
    char mTag[8] = {};
};

class GEO_PUBLIC Edge {
public:
    Edge();
    ~Edge();

    // C can't pass the rest.
    void Log(const char* format, ...);
    // Nothing the generator foresees: the compiler rejects its forwarder, which becomes a TODO.
    void Consume() &&;
    // C owns what it gets, and gives up what it passes.
    std::unique_ptr<Edge> Clone() const;
    void Merge(std::unique_ptr<Edge> other);
    std::vector<uint8_t> GetBytes() const;
    void SetBytes(const std::vector<uint8_t>& bytes);
    void Wide(wchar_t c);
};

} // namespace geo
