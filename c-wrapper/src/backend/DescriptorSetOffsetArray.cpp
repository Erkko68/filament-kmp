#include "../../include/backend/DescriptorSetOffsetArray.h"

#include <algorithm>
#include <new>

struct FilaBackendDescriptorSetOffsetArray {
    uint32_t* offsets;
    uint32_t size;
};

namespace {
FilaBackendDescriptorSetOffsetArray* createArray(uint32_t size) {
    auto* array = new (std::nothrow) FilaBackendDescriptorSetOffsetArray;
    if (!array) {
        return nullptr;
    }
    array->offsets = nullptr;
    array->size = 0;

    if (size == 0) {
        return array;
    }

    array->offsets = new (std::nothrow) uint32_t[size]();
    if (!array->offsets) {
        delete array;
        return nullptr;
    }
    array->size = size;
    return array;
}
} // namespace

extern "C" {

FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_create(void) {
    return createArray(0);
}

FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_createWithSize(uint32_t size) {
    return createArray(size);
}

FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_createFromData(
        const uint32_t* offsets, uint32_t size) {
    auto* array = createArray(size);
    if (!array) {
        return nullptr;
    }
    if (size > 0 && offsets) {
        std::copy(offsets, offsets + size, array->offsets);
    }
    return array;
}

void FilaBackendDescriptorSetOffsetArray_destroy(FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return;
    }
    delete[] array->offsets;
    delete array;
}

bool FilaBackendDescriptorSetOffsetArray_empty(const FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return true;
    }
    return array->offsets == nullptr;
}

uint32_t FilaBackendDescriptorSetOffsetArray_size(const FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return 0;
    }
    return array->size;
}

uint32_t* FilaBackendDescriptorSetOffsetArray_data(FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return nullptr;
    }
    return array->offsets;
}

const uint32_t* FilaBackendDescriptorSetOffsetArray_constData(
        const FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return nullptr;
    }
    return array->offsets;
}

bool FilaBackendDescriptorSetOffsetArray_set(
        FilaBackendDescriptorSetOffsetArray* array, uint32_t index, uint32_t value) {
    if (!array || !array->offsets || index >= array->size) {
        return false;
    }
    array->offsets[index] = value;
    return true;
}

bool FilaBackendDescriptorSetOffsetArray_get(
        const FilaBackendDescriptorSetOffsetArray* array, uint32_t index, uint32_t* outValue) {
    if (!array || !array->offsets || !outValue || index >= array->size) {
        return false;
    }
    *outValue = array->offsets[index];
    return true;
}

void FilaBackendDescriptorSetOffsetArray_clear(FilaBackendDescriptorSetOffsetArray* array) {
    if (!array) {
        return;
    }
    delete[] array->offsets;
    array->offsets = nullptr;
    array->size = 0;
}

}

