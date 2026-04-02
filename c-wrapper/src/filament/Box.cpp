#include <filament/Box.h>

#include "../../include/filament/Box.h"

namespace {

filament::Box toBox(const FilaBox* box) {
    filament::Box out;
    if (!box) {
        return out;
    }
    out.center = {box->center[0], box->center[1], box->center[2]};
    out.halfExtent = {box->halfExtent[0], box->halfExtent[1], box->halfExtent[2]};
    return out;
}

void fromBox(const filament::Box& box, FilaBox* outBox) {
    if (!outBox) {
        return;
    }
    outBox->center[0] = box.center.x;
    outBox->center[1] = box.center.y;
    outBox->center[2] = box.center.z;
    outBox->halfExtent[0] = box.halfExtent.x;
    outBox->halfExtent[1] = box.halfExtent.y;
    outBox->halfExtent[2] = box.halfExtent.z;
}

filament::Aabb toAabb(const FilaAabb* aabb) {
    filament::Aabb out;
    if (!aabb) {
        return out;
    }
    out.min = {aabb->min[0], aabb->min[1], aabb->min[2]};
    out.max = {aabb->max[0], aabb->max[1], aabb->max[2]};
    return out;
}

} // namespace

extern "C" {

bool FilaBox_isEmpty(const FilaBox* box) {
    if (!box) {
        return true;
    }
    return toBox(box).isEmpty();
}

bool FilaBox_set(FilaBox* inOutBox, const float min3[3], const float max3[3]) {
    if (!inOutBox || !min3 || !max3) {
        return false;
    }
    filament::Box box;
    box.set({min3[0], min3[1], min3[2]}, {max3[0], max3[1], max3[2]});
    fromBox(box, inOutBox);
    return true;
}

bool FilaBox_getMin(const FilaBox* box, float outMin3[3]) {
    if (!box || !outMin3) {
        return false;
    }
    const auto min = toBox(box).getMin();
    outMin3[0] = min.x;
    outMin3[1] = min.y;
    outMin3[2] = min.z;
    return true;
}

bool FilaBox_getMax(const FilaBox* box, float outMax3[3]) {
    if (!box || !outMax3) {
        return false;
    }
    const auto max = toBox(box).getMax();
    outMax3[0] = max.x;
    outMax3[1] = max.y;
    outMax3[2] = max.z;
    return true;
}

bool FilaBox_translateTo(const FilaBox* box, const float center3[3], FilaBox* outBox) {
    if (!box || !center3 || !outBox) {
        return false;
    }
    const auto translated = toBox(box).translateTo({center3[0], center3[1], center3[2]});
    fromBox(translated, outBox);
    return true;
}

bool FilaBox_getBoundingSphere(const FilaBox* box, float outCenterRadius4[4]) {
    if (!box || !outCenterRadius4) {
        return false;
    }
    const auto sphere = toBox(box).getBoundingSphere();
    outCenterRadius4[0] = sphere.x;
    outCenterRadius4[1] = sphere.y;
    outCenterRadius4[2] = sphere.z;
    outCenterRadius4[3] = sphere.w;
    return true;
}

bool FilaAabb_isEmpty(const FilaAabb* aabb) {
    if (!aabb) {
        return true;
    }
    return toAabb(aabb).isEmpty();
}

bool FilaAabb_center(const FilaAabb* aabb, float outCenter3[3]) {
    if (!aabb || !outCenter3) {
        return false;
    }
    const auto c = toAabb(aabb).center();
    outCenter3[0] = c.x;
    outCenter3[1] = c.y;
    outCenter3[2] = c.z;
    return true;
}

bool FilaAabb_extent(const FilaAabb* aabb, float outExtent3[3]) {
    if (!aabb || !outExtent3) {
        return false;
    }
    const auto e = toAabb(aabb).extent();
    outExtent3[0] = e.x;
    outExtent3[1] = e.y;
    outExtent3[2] = e.z;
    return true;
}

float FilaAabb_contains(const FilaAabb* aabb, const float point3[3]) {
    if (!aabb || !point3) {
        return 0.0f;
    }
    return toAabb(aabb).contains({point3[0], point3[1], point3[2]});
}

} // extern "C"

