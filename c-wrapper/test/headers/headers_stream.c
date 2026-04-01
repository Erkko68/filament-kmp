#include "filament/Engine.h"
#include "filament/Stream.h"
#include "filament/Types.h"

static void noop_stream_callback(void* image, void* userData) {
    (void)image;
    (void)userData;
}

// Verifies Stream C API is consumable from C
void test_headers_stream(void) {
    FilaEngine* engine = (FilaEngine*)0;

    // Test builder creation and configuration
    FilaStreamBuilder* builder = FilaStreamBuilder_create();
    FilaStreamBuilder_width(builder, 1920);
    FilaStreamBuilder_height(builder, 1080);
    FilaStream* stream = FilaStreamBuilder_build(builder, engine);
    FilaStreamBuilder_destroy(builder);

    // Test getters
    (void)FilaStream_getStreamType(stream);
    FilaStream_setAcquiredImage(stream, (void*)0, noop_stream_callback, (void*)0, (const float*)0);
    FilaStream_setAcquiredImageWithHandler(stream, (void*)0, (FilaCallbackHandler*)0, noop_stream_callback, (void*)0, (const float*)0);
    FilaStream_setDimensions(stream, 640, 480);
    (void)FilaStream_getTimestamp(stream);
}

