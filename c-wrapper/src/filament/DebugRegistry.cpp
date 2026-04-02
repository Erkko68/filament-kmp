#include <filament/DebugRegistry.h>

#include <math/vec2.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include "../../include/filament/DebugRegistry.h"

extern "C" {

bool FilaDebugRegistry_hasProperty(const FilaDebugRegistry* debugRegistry, const char* name) {
    if (!debugRegistry || !name) {
        return false;
    }
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->hasProperty(name);
}

void* FilaDebugRegistry_getPropertyAddress(FilaDebugRegistry* debugRegistry, const char* name) {
    if (!debugRegistry || !name) {
        return nullptr;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->getPropertyAddress(name);
}

const void* FilaDebugRegistry_getPropertyAddressConst(const FilaDebugRegistry* debugRegistry, const char* name) {
    if (!debugRegistry || !name) {
        return nullptr;
    }
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getPropertyAddress(name);
}

bool FilaDebugRegistry_setPropertyBool(FilaDebugRegistry* debugRegistry, const char* name, bool value) {
    if (!debugRegistry || !name) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(name, value);
}

bool FilaDebugRegistry_setPropertyInt(FilaDebugRegistry* debugRegistry, const char* name, int value) {
    if (!debugRegistry || !name) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(name, value);
}

bool FilaDebugRegistry_setPropertyFloat(FilaDebugRegistry* debugRegistry, const char* name, float value) {
    if (!debugRegistry || !name) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(name, value);
}

bool FilaDebugRegistry_setPropertyFloat2(FilaDebugRegistry* debugRegistry, const char* name, const float value[2]) {
    if (!debugRegistry || !name || !value) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(
            name, filament::math::float2{value[0], value[1]});
}

bool FilaDebugRegistry_setPropertyFloat3(FilaDebugRegistry* debugRegistry, const char* name, const float value[3]) {
    if (!debugRegistry || !name || !value) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(
            name, filament::math::float3{value[0], value[1], value[2]});
}

bool FilaDebugRegistry_setPropertyFloat4(FilaDebugRegistry* debugRegistry, const char* name, const float value[4]) {
    if (!debugRegistry || !name || !value) {
        return false;
    }
    return reinterpret_cast<filament::DebugRegistry*>(debugRegistry)->setProperty(
            name, filament::math::float4{value[0], value[1], value[2], value[3]});
}

bool FilaDebugRegistry_getPropertyBool(const FilaDebugRegistry* debugRegistry, const char* name, bool* outValue) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, outValue);
}

bool FilaDebugRegistry_getPropertyInt(const FilaDebugRegistry* debugRegistry, const char* name, int* outValue) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, outValue);
}

bool FilaDebugRegistry_getPropertyFloat(const FilaDebugRegistry* debugRegistry, const char* name, float* outValue) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    return reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, outValue);
}

bool FilaDebugRegistry_getPropertyFloat2(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[2]) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    filament::math::float2 value{};
    if (!reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, &value)) {
        return false;
    }
    outValue[0] = value.x;
    outValue[1] = value.y;
    return true;
}

bool FilaDebugRegistry_getPropertyFloat3(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[3]) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    filament::math::float3 value{};
    if (!reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, &value)) {
        return false;
    }
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    return true;
}

bool FilaDebugRegistry_getPropertyFloat4(const FilaDebugRegistry* debugRegistry, const char* name, float outValue[4]) {
    if (!debugRegistry || !name || !outValue) {
        return false;
    }
    filament::math::float4 value{};
    if (!reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getProperty(name, &value)) {
        return false;
    }
    outValue[0] = value.x;
    outValue[1] = value.y;
    outValue[2] = value.z;
    outValue[3] = value.w;
    return true;
}

bool FilaDebugRegistry_getDataSource(
        const FilaDebugRegistry* debugRegistry,
        const char* name,
        const void** outData,
        size_t* outCount) {
    if (!debugRegistry || !name || !outData || !outCount) {
        return false;
    }
    const auto source = reinterpret_cast<const filament::DebugRegistry*>(debugRegistry)->getDataSource(name);
    *outData = source.data;
    *outCount = source.count;
    return source.data != nullptr;
}

}
