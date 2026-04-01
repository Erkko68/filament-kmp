#include "backend/DescriptorSetOffsetArray.h"

void backend_descriptor_set_offset_array_test(void) {
    FilaBackendDescriptorSetOffsetArray* array = FilaBackendDescriptorSetOffsetArray_create();
    FilaBackendDescriptorSetOffsetArray_empty(array);
    FilaBackendDescriptorSetOffsetArray_size(array);
    FilaBackendDescriptorSetOffsetArray_data(array);
    FilaBackendDescriptorSetOffsetArray_constData(array);
    FilaBackendDescriptorSetOffsetArray_clear(array);
    FilaBackendDescriptorSetOffsetArray_destroy(array);

    array = FilaBackendDescriptorSetOffsetArray_createWithSize(4);
    FilaBackendDescriptorSetOffsetArray_set(array, 0, 7u);
    {
        uint32_t out = 0;
        FilaBackendDescriptorSetOffsetArray_get(array, 0, &out);
    }
    FilaBackendDescriptorSetOffsetArray_destroy(array);

    {
        const uint32_t init[3] = { 1u, 2u, 3u };
        array = FilaBackendDescriptorSetOffsetArray_createFromData(init, 3);
        FilaBackendDescriptorSetOffsetArray_destroy(array);
    }
}

