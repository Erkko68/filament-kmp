#pragma once

#include <geo/Internal.h>
#include <geo/Shape.h>

#include <array>
#include <functional>
#include <vector>

namespace geo {

using Listener = std::function<void(Shape* shape, float progress)>;
using Visitor = void (*)(void* user, uint32_t index);

// C binds the instantiation the config lists.
template<typename T>
class Box {
public:
    Box();
    T Get() const;
    void Set(T value);
};

class GEO_PUBLIC Canvas {
public:
    Canvas();
    ~Canvas();

    void Add(Shape* shape);
    void Add(const std::vector<Shape*>& shapes);
    void Adopt(Shape&& shape);
    void Attach(Debug* debug);
    size_t GetCount() const;
    std::vector<uint32_t> GetIds() const;

    Handle GetHandle() const;
    void SetHandle(Handle handle);
    Handle* GetHandles();

    Layers GetLayers() const;
    void SetLayers(Layers layers);

    void SetListener(Listener listener);
    void Visit(Visitor visitor, void* user) const;
    void Upload(Blob&& blob);

    Result<Ref<Shape>> Load(const char* path);
    Result<math::float3> Measure() const;

    void SetBackground(std::array<float, 4> rgba);
    void Fill(std::array<float, 4>& rgba) const;

    Box<float>& GetBox();

    template<typename T> void SetValue(const char* name, T value);
    template<typename T> T GetValue(const char* name) const;
    template<typename T> T Convert(T value) const;

    void operator()(float time);
    static Canvas* GetDefault();
};

GEO_PUBLIC float Distance(const math::float2& a, const math::float2& b);
GEO_PUBLIC void Reset();

} // namespace geo
