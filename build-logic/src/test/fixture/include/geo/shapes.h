// A library unlike Jolt in every convention the generator used to assume: include guards, snake_case, no export
// macro, no member prefix, std containers and strings.
#ifndef GEO_SHAPES_H
#define GEO_SHAPES_H

#include <cstdint>
#include <functional>
#include <optional>
#include <string>
#include <vector>

namespace geo {

enum class Kind : uint8_t { circle, polygon };

struct Point {
    float x = 0;
    float y = 0;
};

// Config: a scalar.
class Id {
public:
    explicit Id(uint32_t value) : value(value) {}
    uint32_t value;
};

// Config: a math mirror.
struct Vec2 {
    float v[2];
};

using Visitor = std::function<void(const Point&)>;

class Shape {
public:
    virtual ~Shape() = default;

    virtual Kind kind() const = 0;
    virtual float area() const = 0;

    const std::string& name() const;
    void set_name(const std::string& name);
    Id id() const;
    Vec2 centroid() const;
    void move_by(Vec2 offset);

#ifndef GEO_NO_DEBUG
    void dump() const;
#endif
};

class Circle : public Shape {
public:
    explicit Circle(Point center, float radius = 1.0f);

    Kind kind() const override;
    float area() const override;

    float radius() const;
    void scale(float factor);
    void scale(float x, float y);
    std::optional<Point> intersect(const Circle& other) const;
};

class Polygon : public Shape {
public:
    explicit Polygon(const std::vector<Point>& points);

    Kind kind() const override;
    float area() const override;

    std::vector<Point> points() const;
    void visit(Visitor visitor) const;
    uint64_t hash() const;

    template<typename T>
    T as() const;
};

float distance(const Point& a, const Point& b);
Shape* find(Id id);

} // namespace geo

#endif // GEO_SHAPES_H
