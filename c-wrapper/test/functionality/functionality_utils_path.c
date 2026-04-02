 #include <stdio.h>

#include "utils/Path.h"

int main(void) {
    printf("Running functionality_utils_path...\n");

    FilaUtilsPath* root = FilaUtilsPath_createFromUtf8("/tmp");
    FilaUtilsPath* leaf = FilaUtilsPath_createFromUtf8("filament_kmp_path_test.txt");
    if (!root || !leaf) {
        printf("Path creation failed\n");
        FilaUtilsPath_destroy(leaf);
        FilaUtilsPath_destroy(root);
        return 1;
    }

    FilaUtilsPath* joined = FilaUtilsPath_concat(root, leaf);
    if (!joined || !FilaUtilsPath_isAbsolute(joined)) {
        printf("Path concat/absolute mismatch\n");
        FilaUtilsPath_destroy(joined);
        FilaUtilsPath_destroy(leaf);
        FilaUtilsPath_destroy(root);
        return 1;
    }

    char pathText[256] = {0};
    char nameText[128] = {0};
    char extText[64] = {0};
    (void)FilaUtilsPath_copyPath(joined, pathText, sizeof(pathText));
    (void)FilaUtilsPath_copyName(joined, nameText, sizeof(nameText));
    (void)FilaUtilsPath_copyNameWithoutExtension(joined, nameText, sizeof(nameText));
    (void)FilaUtilsPath_copyExtension(joined, extText, sizeof(extText));

    FilaUtilsPath* parent = FilaUtilsPath_getParent(joined);
    FilaUtilsPath* absolute = FilaUtilsPath_getAbsolutePath(leaf);
    FilaUtilsPath* cwd = FilaUtilsPath_getCurrentDirectory();
    if (!parent || !absolute || !cwd) {
        printf("Path derived-path query failed\n");
        FilaUtilsPath_destroy(cwd);
        FilaUtilsPath_destroy(absolute);
        FilaUtilsPath_destroy(parent);
        FilaUtilsPath_destroy(joined);
        FilaUtilsPath_destroy(leaf);
        FilaUtilsPath_destroy(root);
        return 1;
    }

    (void)FilaUtilsPath_exists(cwd);
    (void)FilaUtilsPath_isDirectory(cwd);
    (void)FilaUtilsPath_getCurrentExecutable();
    (void)FilaUtilsPath_getTemporaryDirectory();
    (void)FilaUtilsPath_getUserSettingsDirectory();

    if (!FilaUtilsPath_concatToSelf(root, leaf) ||
            !FilaUtilsPath_equals(root, joined) ||
            FilaUtilsPath_concatToSelf((FilaUtilsPath*)0, leaf)) {
        printf("Path concat/equality/null-safety mismatch\n");
        FilaUtilsPath_destroy(cwd);
        FilaUtilsPath_destroy(absolute);
        FilaUtilsPath_destroy(parent);
        FilaUtilsPath_destroy(joined);
        FilaUtilsPath_destroy(leaf);
        FilaUtilsPath_destroy(root);
        return 1;
    }

    // Non-destructive no-op checks for current test environment.
    (void)FilaUtilsPath_mkdir((const FilaUtilsPath*)0);
    (void)FilaUtilsPath_mkdirRecursive((const FilaUtilsPath*)0);
    (void)FilaUtilsPath_unlinkFile((FilaUtilsPath*)0);

    FilaUtilsPath_destroy(cwd);
    FilaUtilsPath_destroy(absolute);
    FilaUtilsPath_destroy(parent);
    FilaUtilsPath_destroy(joined);
    FilaUtilsPath_destroy(leaf);
    FilaUtilsPath_destroy(root);

    printf("functionality_utils_path completed\n");
    return 0;
}

