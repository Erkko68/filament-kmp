#include "filament/View.h"

// Function pointer assignments lock exported C signatures.
static void (*g_view_set_scene)(FilaView*, FilaScene*) = FilaView_setScene;
static FilaScene* (*g_view_get_scene)(FilaView*) = FilaView_getScene;

void fila_view_signature_compile_only(void) {
    (void)g_view_set_scene;
    (void)g_view_get_scene;
}

