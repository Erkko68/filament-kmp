#include "geometry/Transcoder.h"

void test_headers_geometry_transcoder(void) {
    const FilaGeometryTranscoderConfig config = {
            FILA_GEOMETRY_COMPONENT_TYPE_FLOAT,
            false,
            3u,
            0u,
    };
    FilaGeometryTranscoder* transcoder = FilaGeometryTranscoder_create(config);
    (void)FilaGeometryTranscoder_transcode((const FilaGeometryTranscoder*)0, (float*)0, (const void*)0, 0u);
    FilaGeometryTranscoder_destroy(transcoder);
}

