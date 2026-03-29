#include "filament/Engine.h"
#include "filament/Stream.h"
#include "filament/Types.h"

// Verifies Stream C API is consumable from C
void fila_stream_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;

    // Test builder creation and configuration
    FilaStreamBuilder* builder = FilaStreamBuilder_create();
    FilaStreamBuilder_width(builder, 1920);
    FilaStreamBuilder_height(builder, 1080);
    FilaStream* stream = FilaStreamBuilder_build(builder, engine);
    FilaStreamBuilder_destroy(builder);

    // Test getters
    (void)FilaStream_getStreamType(stream);
    FilaStream_setDimensions(stream, 640, 480);
    (void)FilaStream_getTimestamp(stream);
}

