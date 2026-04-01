#include "filament/MaterialChunkType.h"

void test_headers_material_chunk_type(void) {
    FilaMaterialChunkType chunk = FILA_MATERIAL_CHUNK_TYPE_MATERIAL_PROPERTIES;
    chunk = FILA_MATERIAL_CHUNK_TYPE_MATERIAL_SHADER_MODELS;
    chunk = FILA_MATERIAL_CHUNK_TYPE_DICTIONARY_TEXT;
    chunk = FILA_MATERIAL_CHUNK_TYPE_MATERIAL_CRC32;
    (void)chunk;

    const uint64_t packed = FILA_MATERIAL_CHUNK8('T', 'E', 'S', 'T', 'C', 'H', 'N', 'K');
    (void)packed;
}

