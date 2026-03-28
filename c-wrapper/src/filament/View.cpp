#include <filament/Scene.h>
#include <filament/View.h>

#include "filament/Types.h"

extern "C" {

void FilaView_setScene(FilaView* view, FilaScene* scene) {
    if (!view) {
        return;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppView->setScene(cppScene);
}

FilaScene* FilaView_getScene(FilaView* view) {
    if (!view) {
        return nullptr;
    }
    auto cppView = reinterpret_cast<filament::View*>(view);
    return reinterpret_cast<FilaScene*>(cppView->getScene());
}

} // extern "C"

