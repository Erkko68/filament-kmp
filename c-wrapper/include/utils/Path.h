#ifndef FILAMENT_C_UTILS_PATH_H
#define FILAMENT_C_UTILS_PATH_H

#include <stdbool.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaUtilsPath FilaUtilsPath;

FilaUtilsPath* FilaUtilsPath_create(void);
FilaUtilsPath* FilaUtilsPath_createFromUtf8(const char* pathname);
void FilaUtilsPath_destroy(FilaUtilsPath* path);

bool FilaUtilsPath_exists(const FilaUtilsPath* path);
bool FilaUtilsPath_isFile(const FilaUtilsPath* path);
bool FilaUtilsPath_isDirectory(const FilaUtilsPath* path);
bool FilaUtilsPath_isEmpty(const FilaUtilsPath* path);
bool FilaUtilsPath_isAbsolute(const FilaUtilsPath* path);

void FilaUtilsPath_setPath(FilaUtilsPath* path, const char* pathname);

// Returns required bytes excluding null terminator.
size_t FilaUtilsPath_copyPath(const FilaUtilsPath* path, char* outPath, size_t outPathSize);
size_t FilaUtilsPath_copyCanonicalPath(const char* pathname, char* outPath, size_t outPathSize);
size_t FilaUtilsPath_copyName(const FilaUtilsPath* path, char* outName, size_t outNameSize);
size_t FilaUtilsPath_copyNameWithoutExtension(
        const FilaUtilsPath* path,
        char* outName,
        size_t outNameSize);
size_t FilaUtilsPath_copyExtension(const FilaUtilsPath* path, char* outExt, size_t outExtSize);

FilaUtilsPath* FilaUtilsPath_getParent(const FilaUtilsPath* path);
FilaUtilsPath* FilaUtilsPath_getAncestor(const FilaUtilsPath* path, int n);
FilaUtilsPath* FilaUtilsPath_getAbsolutePath(const FilaUtilsPath* path);
FilaUtilsPath* FilaUtilsPath_concat(const FilaUtilsPath* root, const FilaUtilsPath* leaf);
bool FilaUtilsPath_concatToSelf(FilaUtilsPath* root, const FilaUtilsPath* leaf);
bool FilaUtilsPath_equals(const FilaUtilsPath* lhs, const FilaUtilsPath* rhs);

size_t FilaUtilsPath_getSegmentCount(const FilaUtilsPath* path);
size_t FilaUtilsPath_copySegmentAt(const FilaUtilsPath* path, size_t segmentIndex, char* outSegment, size_t outSegmentSize);

size_t FilaUtilsPath_getListContentsCount(const FilaUtilsPath* path);
FilaUtilsPath* FilaUtilsPath_getListContentAt(const FilaUtilsPath* path, size_t entryIndex);

FilaUtilsPath* FilaUtilsPath_getCurrentDirectory(void);
FilaUtilsPath* FilaUtilsPath_getCurrentExecutable(void);
FilaUtilsPath* FilaUtilsPath_getTemporaryDirectory(void);
FilaUtilsPath* FilaUtilsPath_getUserSettingsDirectory(void);

bool FilaUtilsPath_mkdir(const FilaUtilsPath* path);
bool FilaUtilsPath_mkdirRecursive(const FilaUtilsPath* path);
bool FilaUtilsPath_unlinkFile(FilaUtilsPath* path);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_UTILS_PATH_H

