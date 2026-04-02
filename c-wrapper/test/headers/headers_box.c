#include "filament/Box.h"

void test_headers_box(void) {
    FilaBox box = {{0.0f, 0.0f, 0.0f}, {1.0f, 2.0f, 3.0f}};
    FilaBox translated;
    FilaAabb aabb = {{-1.0f, -2.0f, -3.0f}, {1.0f, 2.0f, 3.0f}};
    float min3[3] = {-1.0f, -1.0f, -1.0f};
    float max3[3] = {1.0f, 1.0f, 1.0f};
    float out3[3];
    float sphere[4];
    float center3[3] = {2.0f, 3.0f, 4.0f};

    (void)FilaBox_isEmpty(&box);
    (void)FilaBox_set(&box, min3, max3);
    (void)FilaBox_getMin(&box, out3);
    (void)FilaBox_getMax(&box, out3);
    (void)FilaBox_translateTo(&box, center3, &translated);
    (void)FilaBox_getBoundingSphere(&box, sphere);

    (void)FilaAabb_isEmpty(&aabb);
    (void)FilaAabb_center(&aabb, out3);
    (void)FilaAabb_extent(&aabb, out3);
    (void)FilaAabb_contains(&aabb, center3);
}

