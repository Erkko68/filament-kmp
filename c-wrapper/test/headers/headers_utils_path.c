#include "utils/Path.h"

void test_headers_utils_path(void) {
    FilaUtilsPath* root = FilaUtilsPath_createFromUtf8("/tmp");
    FilaUtilsPath* leaf = FilaUtilsPath_createFromUtf8("demo.txt");
    FilaUtilsPath* joined = FilaUtilsPath_concat(root, leaf);
    FilaUtilsPath* parent = FilaUtilsPath_getParent(joined);
    FilaUtilsPath* ancestor = FilaUtilsPath_getAncestor(joined, 0);
    FilaUtilsPath* absolute = FilaUtilsPath_getAbsolutePath(joined);
    char text[256];

    (void)FilaUtilsPath_exists(joined);
    (void)FilaUtilsPath_isFile(joined);
    (void)FilaUtilsPath_isDirectory(joined);
    (void)FilaUtilsPath_isEmpty(joined);
    (void)FilaUtilsPath_isAbsolute(joined);
    (void)FilaUtilsPath_copyPath(joined, text, sizeof(text));
    (void)FilaUtilsPath_copyName(joined, text, sizeof(text));
    (void)FilaUtilsPath_copyNameWithoutExtension(joined, text, sizeof(text));
    (void)FilaUtilsPath_copyExtension(joined, text, sizeof(text));
    (void)FilaUtilsPath_concatToSelf(root, leaf);
    (void)FilaUtilsPath_equals(root, joined);
    (void)FilaUtilsPath_getCurrentDirectory();
    (void)FilaUtilsPath_getCurrentExecutable();
    (void)FilaUtilsPath_getTemporaryDirectory();
    (void)FilaUtilsPath_getUserSettingsDirectory();
    (void)FilaUtilsPath_mkdir(root);
    (void)FilaUtilsPath_mkdirRecursive(root);
    (void)FilaUtilsPath_unlinkFile(joined);

    FilaUtilsPath_destroy(absolute);
    FilaUtilsPath_destroy(ancestor);
    FilaUtilsPath_destroy(parent);
    FilaUtilsPath_destroy(joined);
    FilaUtilsPath_destroy(leaf);
    FilaUtilsPath_destroy(root);
}

