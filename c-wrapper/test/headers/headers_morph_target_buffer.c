#include "filament/Engine.h"
#include "filament/MorphTargetBuffer.h"

// Verifies MorphTargetBuffer C API is consumable from C.
void test_headers_morph_target_buffer(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaMorphTargetBufferBuilder* builder = FilaMorphTargetBufferBuilder_create();
    FilaMorphTargetBufferBuilder_vertexCount(builder, 16u);
    FilaMorphTargetBufferBuilder_count(builder, 2u);
    FilaMorphTargetBufferBuilder_withPositions(builder, true);
    FilaMorphTargetBufferBuilder_withTangents(builder, false);
    FilaMorphTargetBufferBuilder_enableCustomMorphing(builder, false);

    FilaMorphTargetBuffer* morphTargetBuffer = FilaMorphTargetBufferBuilder_build(builder, engine);
    float positions[9] = {
            -1.0f, -1.0f, 0.0f,
             1.0f, -1.0f, 0.0f,
             0.0f,  1.0f, 0.0f,
    };
    int16_t tangents[12] = {
            0, 0, 32767, 0,
            0, 0, 32767, 0,
            0, 0, 32767, 0,
    };
    (void)FilaMorphTargetBuffer_getVertexCount(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_getCount(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_hasPositions(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_hasTangents(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_isCustomMorphingEnabled(morphTargetBuffer);
    FilaMorphTargetBuffer_setPositionsAtFloat3(morphTargetBuffer, engine, 0u, positions, 3u, 0u);
    FilaMorphTargetBuffer_setTangentsAt(morphTargetBuffer, engine, 0u, tangents, 3u, 0u);

    FilaMorphTargetBufferBuilder_destroy(builder);
    FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
}

