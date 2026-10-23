#pragma once

#include <geo/Base.h>
#include <geo/Math.h>

#include <optional>
#include <string>
#include <string_view>
#include <vector>

namespace geo {

enum class Kind : uint8_t { CIRCLE, POLYGON = 5, GROUP };

// Wider than a C enum's int.
enum Layers : uint64_t { LAYER_NONE = 0, LAYER_ALL = 0xFFFFFFFFFFull };

// A value struct; its fields are mX, read by GetX.
struct Style {
    float mWidth = 1.5f;
    Kind mKind = Kind::CIRCLE;
    math::float3 mColor = { 1, 0, 0 };
    float mWeights[3] = {};
    // The struct keeps the pointer: its setter interns C's string.
    const char* mName = nullptr;
    bool mVisible = true;

    // Reads mWidth already, so the field gets no getter of its own.
    float GetWidth() const { return mWidth; }
};

// Only ever a result: C gets no setter for its pointer.
struct Stats {
    const char* mLabel = nullptr;
    uint32_t mCount = 0;
};

struct Limits : detail::Limits {
    float mMax = 1;
};

class GEO_PUBLIC Shape : public RefTarget<Shape> {
public:
    virtual ~Shape();
    virtual float GetArea() const = 0;

    Kind GetKind() const;
    Style& GetStyle();
    const Style& GetStyle() const;
    void SetStyle(const Style& style);
    Stats GetStats() const;
    Limits GetLimits() const;
    void SetLimits(const Limits& limits);

    void SetName(const char* name);
    void SetName(const char* name, size_t nameLength);
    void SetName(std::string_view name);
    const std::string& GetName() const;
    std::string Describe() const;
    std::string_view GetLabel() const;

    std::optional<float> GetRadius() const;
    void SetRadius(std::optional<float> radius);

    void Move(float dx, float dy);
    void Move(const math::float2& delta);
    void Scale(math::float3& factors);
    void Normalize(math::float4& plane);
    void Project(math::float4* plane);

    [[deprecated]] void Legacy();
    void DebugDump() const;

#if GEO_THREADS
    void DrawAsync();
#elif GEO_FIBERS
    void DrawFiber();
#else
    void DrawSync();
#endif
};

class GEO_PUBLIC Circle : public Shape {
public:
    explicit Circle(float radius = 1.0f);
    float GetArea() const override;
};

class GEO_PUBLIC Polygon : public Shape {
public:
    static constexpr uint32_t MAX_SIDES = 64;

    static Ref<Polygon> Create(uint32_t sides);
    float GetArea() const override;
    std::vector<math::float2> GetPoints() const;
    void SetPoints(const std::vector<math::float2>& points);
    Span<float> GetWeights() const;
    void SetWeights(Span<float> weights);

private:
    Polygon();
};

} // namespace geo
