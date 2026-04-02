#include "filament/DebugRegistry.h"

void test_headers_debug_registry(void) {
    FilaDebugRegistry* registry = (FilaDebugRegistry*)0;
    const FilaDebugRegistry* constRegistry = (const FilaDebugRegistry*)0;
    const char* name = "debug.property";
    bool b = false;
    int i = 0;
    float f = 0.0f;
    float f2[2] = {0.0f, 0.0f};
    float f3[3] = {0.0f, 0.0f, 0.0f};
    float f4[4] = {0.0f, 0.0f, 0.0f, 0.0f};
    const void* data = (const void*)0;
    size_t count = 0u;

    (void)FilaDebugRegistry_hasProperty(constRegistry, name);
    (void)FilaDebugRegistry_getPropertyAddress(registry, name);
    (void)FilaDebugRegistry_getPropertyAddressConst(constRegistry, name);

    (void)FilaDebugRegistry_setPropertyBool(registry, name, b);
    (void)FilaDebugRegistry_setPropertyInt(registry, name, i);
    (void)FilaDebugRegistry_setPropertyFloat(registry, name, f);
    (void)FilaDebugRegistry_setPropertyFloat2(registry, name, f2);
    (void)FilaDebugRegistry_setPropertyFloat3(registry, name, f3);
    (void)FilaDebugRegistry_setPropertyFloat4(registry, name, f4);

    (void)FilaDebugRegistry_getPropertyBool(constRegistry, name, &b);
    (void)FilaDebugRegistry_getPropertyInt(constRegistry, name, &i);
    (void)FilaDebugRegistry_getPropertyFloat(constRegistry, name, &f);
    (void)FilaDebugRegistry_getPropertyFloat2(constRegistry, name, f2);
    (void)FilaDebugRegistry_getPropertyFloat3(constRegistry, name, f3);
    (void)FilaDebugRegistry_getPropertyFloat4(constRegistry, name, f4);

    (void)FilaDebugRegistry_getDataSource(constRegistry, name, &data, &count);
}

