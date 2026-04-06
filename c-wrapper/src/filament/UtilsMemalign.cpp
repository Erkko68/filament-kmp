#include <utils/memalign.h>

#include <stdint.h>

#include "../../include/utils/Memalign.h"

extern "C" {

void* FilaUtils_alignedAlloc(size_t size, size_t align) {
    return utils::aligned_alloc(size, align);
}

void FilaUtils_alignedFree(void* ptr) {
    utils::aligned_free(ptr);
}

bool FilaUtils_isAligned(const void* ptr, size_t align) {
    if (!ptr || align == 0u || (align & (align - 1u)) != 0u) {
        return false;
    }
    return (reinterpret_cast<uintptr_t>(ptr) & (align - 1u)) == 0u;
}

} // extern "C"

