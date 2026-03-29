#include <filament/DebugRegistry.h>

#include "../../../include/filament/DebugRegistry.h"

extern "C" {

bool FilaDebugRegistry_hasProperty(const FilaDebugRegistry* debugRegistry, const char* name) {
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->hasProperty(name);
}

void* FilaDebugRegistry_getPropertyAddress(FilaDebugRegistry* debugRegistry, const char* name) {
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->getPropertyAddress(name);
}

}
