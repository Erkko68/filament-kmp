#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Scene.h"
#include "filament/View.h"

int main(void) {
    printf("Running engine+scene+view smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView* view = FilaEngine_createView(engine);
    if (!view) {
        printf("View creation failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setScene(view, scene);
    if (FilaView_getScene(view) != scene) {
        printf("View scene mismatch\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);

    printf("Engine+scene+view smoke program completed\n");
    return 0;
}

