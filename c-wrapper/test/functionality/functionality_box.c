#include <math.h>
#include <stdio.h>

#include "filament/Box.h"

static int nearly_equal(float a, float b) {
    return fabsf(a - b) < 1e-4f;
}

int main(void) {
    printf("Running functionality_box...\n");

    FilaBox box = {{0.0f, 0.0f, 0.0f}, {1.0f, 2.0f, 3.0f}};
    FilaBox moved;
    FilaBox merged;
    FilaBox transformed;
    FilaAabb aabb = {{-1.0f, -2.0f, -3.0f}, {1.0f, 2.0f, 3.0f}};
    FilaAabb transformedAabb;

    float min3[3] = {0.0f, 0.0f, 0.0f};
    float max3[3] = {0.0f, 0.0f, 0.0f};
    float sphere4[4] = {0.0f, 0.0f, 0.0f, 0.0f};
    float corners[24] = {0.0f};

    if (!FilaBox_getMin(&box, min3) || !FilaBox_getMax(&box, max3)) {
        printf("Box min/max query failed\n");
        return 1;
    }
    if (!nearly_equal(min3[0], -1.0f) || !nearly_equal(max3[2], 3.0f)) {
        printf("Box min/max values unexpected\n");
        return 1;
    }

    if (!FilaBox_translateTo(&box, (const float[3]){5.0f, 6.0f, 7.0f}, &moved)) {
        printf("Box translation failed\n");
        return 1;
    }
    if (!nearly_equal(moved.center[0], 5.0f) || !nearly_equal(moved.center[2], 7.0f)) {
        printf("Translated box center mismatch\n");
        return 1;
    }

    if (!FilaBox_union(&box, &moved, &merged)) {
        printf("Box union failed\n");
        return 1;
    }
    if (FilaBox_isEmpty(&merged)) {
        printf("Merged box should not be empty\n");
        return 1;
    }

    if (!FilaBox_getBoundingSphere(&box, sphere4) || sphere4[3] <= 0.0f) {
        printf("Bounding sphere query failed\n");
        return 1;
    }

    const float identity3x3[9] = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f};
    const float translation[3] = {2.0f, 3.0f, 4.0f};

    if (!FilaBox_transformAffine(identity3x3, translation, &box, &transformed)) {
        printf("Box affine transform failed\n");
        return 1;
    }
    if (!nearly_equal(transformed.center[0], 2.0f) || !nearly_equal(transformed.center[1], 3.0f)) {
        printf("Box affine transform mismatch\n");
        return 1;
    }

    if (!FilaAabb_getCorners(&aabb, corners)) {
        printf("Aabb corners query failed\n");
        return 1;
    }
    if (!nearly_equal(corners[0], -1.0f) || !nearly_equal(corners[23], 3.0f)) {
        printf("Aabb corners values unexpected\n");
        return 1;
    }

    if (!FilaAabb_transformAffine(identity3x3, translation, &aabb, &transformedAabb)) {
        printf("Aabb affine transform failed\n");
        return 1;
    }
    if (!nearly_equal(transformedAabb.min[0], 1.0f) || !nearly_equal(transformedAabb.max[2], 7.0f)) {
        printf("Aabb affine transform mismatch\n");
        return 1;
    }

    if (FilaBox_union((const FilaBox*)0, &box, &merged) ||
            FilaBox_transformAffine(identity3x3, translation, (const FilaBox*)0, &transformed) ||
            FilaAabb_getCorners((const FilaAabb*)0, corners) ||
            FilaAabb_transformAffine(identity3x3, translation, (const FilaAabb*)0, &transformedAabb)) {
        printf("Null-safety behavior mismatch\n");
        return 1;
    }

    printf("functionality_box completed\n");
    return 0;
}

