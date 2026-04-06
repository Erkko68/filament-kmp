#ifndef FILAMENT_C_UTILS_IMMUTABLE_CSTRING_H
#define FILAMENT_C_UTILS_IMMUTABLE_CSTRING_H

#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaUtilsImmutableCString FilaUtilsImmutableCString;

FilaUtilsImmutableCString* FilaUtilsImmutableCString_create(void);
FilaUtilsImmutableCString* FilaUtilsImmutableCString_createFromUtf8(const char* text);
FilaUtilsImmutableCString* FilaUtilsImmutableCString_createFromData(const char* data, size_t length);
void FilaUtilsImmutableCString_destroy(FilaUtilsImmutableCString* cstring);

size_t FilaUtilsImmutableCString_size(const FilaUtilsImmutableCString* cstring);
bool FilaUtilsImmutableCString_empty(const FilaUtilsImmutableCString* cstring);
bool FilaUtilsImmutableCString_isStatic(const FilaUtilsImmutableCString* cstring);

// Returns required bytes excluding null terminator.
size_t FilaUtilsImmutableCString_copyUtf8(
        const FilaUtilsImmutableCString* cstring,
        char* outText,
        size_t outTextSize);

bool FilaUtilsImmutableCString_equals(
        const FilaUtilsImmutableCString* lhs,
        const FilaUtilsImmutableCString* rhs);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_IMMUTABLE_CSTRING_H

