package io.github.erkko68.filament;

public class Box {
    public float[] center = new float[3];
    public float[] halfExtent = new float[3];

    public Box() {}

    public Box(float cx, float cy, float cz, float ex, float ey, float ez) {
        center[0] = cx;
        center[1] = cy;
        center[2] = cz;
        halfExtent[0] = ex;
        halfExtent[1] = ey;
        halfExtent[2] = ez;
    }
}
