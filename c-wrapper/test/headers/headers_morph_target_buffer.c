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
    (void)FilaMorphTargetBuffer_getVertexCount(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_getCount(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_hasPositions(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_hasTangents(morphTargetBuffer);
    (void)FilaMorphTargetBuffer_isCustomMorphingEnabled(morphTargetBuffer);

    FilaMorphTargetBufferBuilder_destroy(builder);
    FilaEngine_destroyMorphTargetBuffer(engine, morphTargetBuffer);
}

