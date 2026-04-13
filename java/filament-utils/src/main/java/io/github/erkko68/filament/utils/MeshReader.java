package io.github.erkko68.filament.utils;

import io.github.erkko68.filament.Engine;
import io.github.erkko68.filament.IndexBuffer;
import io.github.erkko68.filament.MaterialInstance;
import io.github.erkko68.filament.VertexBuffer;
import java.nio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MeshReader {
    public static class Mesh {
        public int renderable;
        public VertexBuffer vertexBuffer;
        public IndexBuffer indexBuffer;
    }

    @Nullable
    public static Mesh loadMeshFromBuffer(@NotNull Engine engine, @NotNull Buffer buffer, @Nullable MaterialInstance defaultMaterial) {
        Mesh mesh = new Mesh();
        long nativeDefaultMaterial = (defaultMaterial != null) ? defaultMaterial.getNativeObject() : 0L;
        long[] outVbIb = new long[2];
        mesh.renderable = nLoadMeshFromBuffer(engine.getNativeObject(), buffer, buffer.remaining(), nativeDefaultMaterial, outVbIb);
        if (mesh.renderable == 0) return null;
        mesh.vertexBuffer = new VertexBuffer(outVbIb[0]);
        mesh.indexBuffer = new IndexBuffer(outVbIb[1]);
        return mesh;
    }

    private static native int nLoadMeshFromBuffer(long nativeEngine, Buffer buffer, int remaining, long nativeDefaultMaterial, long[] outVbIb);
}
