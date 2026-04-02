#include <stdio.h>

#include "geometry/Transcoder.h"

int main(void) {
    printf("Running functionality_geometry_transcoder_null_safety...\n");

    const float src[3] = {1.0f, 2.0f, 3.0f};
    if (FilaGeometryTranscoder_transcode((const FilaGeometryTranscoder*)0, (float*)0, src, 1u) != 0u) {
        printf("GeometryTranscoder null transcoder mismatch\n");
        return 1;
    }

    const FilaGeometryTranscoderConfig config = {
            FILA_GEOMETRY_COMPONENT_TYPE_FLOAT,
            false,
            3u,
            0u,
    };
    FilaGeometryTranscoder* transcoder = FilaGeometryTranscoder_create(config);
    if (!transcoder) {
        printf("GeometryTranscoder creation failed\n");
        return 1;
    }

    const size_t required = FilaGeometryTranscoder_transcode(transcoder, (float*)0, src, 1u);
    float out[3] = {0.0f, 0.0f, 0.0f};
    const size_t written = FilaGeometryTranscoder_transcode(transcoder, out, src, 1u);
    if (required == 0u || written != required || out[0] != 1.0f || out[1] != 2.0f || out[2] != 3.0f) {
        printf("GeometryTranscoder transcode mismatch\n");
        FilaGeometryTranscoder_destroy(transcoder);
        return 1;
    }

    FilaGeometryTranscoder_destroy(transcoder);
    printf("functionality_geometry_transcoder_null_safety completed\n");
    return 0;
}

