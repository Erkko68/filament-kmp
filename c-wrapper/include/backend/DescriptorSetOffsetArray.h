#ifndef FILAMENT_C_BACKEND_DESCRIPTORSETOFFSETARRAY_H
#define FILAMENT_C_BACKEND_DESCRIPTORSETOFFSETARRAY_H

#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendDescriptorSetOffsetArray FilaBackendDescriptorSetOffsetArray;

FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_create(void);
FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_createWithSize(uint32_t size);
FilaBackendDescriptorSetOffsetArray* FilaBackendDescriptorSetOffsetArray_createFromData(
    const uint32_t* offsets, uint32_t size);
void FilaBackendDescriptorSetOffsetArray_destroy(FilaBackendDescriptorSetOffsetArray* array);

bool FilaBackendDescriptorSetOffsetArray_empty(const FilaBackendDescriptorSetOffsetArray* array);
uint32_t FilaBackendDescriptorSetOffsetArray_size(const FilaBackendDescriptorSetOffsetArray* array);

uint32_t* FilaBackendDescriptorSetOffsetArray_data(FilaBackendDescriptorSetOffsetArray* array);
const uint32_t* FilaBackendDescriptorSetOffsetArray_constData(
    const FilaBackendDescriptorSetOffsetArray* array);

bool FilaBackendDescriptorSetOffsetArray_set(
    FilaBackendDescriptorSetOffsetArray* array, uint32_t index, uint32_t value);
bool FilaBackendDescriptorSetOffsetArray_get(
    const FilaBackendDescriptorSetOffsetArray* array, uint32_t index, uint32_t* outValue);

void FilaBackendDescriptorSetOffsetArray_clear(FilaBackendDescriptorSetOffsetArray* array);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_DESCRIPTORSETOFFSETARRAY_H

