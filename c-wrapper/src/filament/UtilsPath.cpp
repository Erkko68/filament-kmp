#include <utils/Path.h>

#include <new>
#include <string>

#include "../../include/utils/Path.h"

struct FilaUtilsPath {
    utils::Path value;
};

namespace {
size_t copyText(const std::string& text, char* outText, size_t outTextSize) {
    const size_t len = text.size();
    if (!outText || outTextSize == 0u) {
        return len;
    }
    const size_t copied = len < (outTextSize - 1u) ? len : (outTextSize - 1u);
    for (size_t i = 0u; i < copied; ++i) {
        outText[i] = text[i];
    }
    outText[copied] = '\0';
    return len;
}

FilaUtilsPath* createFromPath(const utils::Path& path) {
    auto* out = new (std::nothrow) FilaUtilsPath();
    if (!out) {
        return nullptr;
    }
    out->value = path;
    return out;
}
} // namespace

extern "C" {

FilaUtilsPath* FilaUtilsPath_create(void) {
    return new (std::nothrow) FilaUtilsPath();
}

FilaUtilsPath* FilaUtilsPath_createFromUtf8(const char* pathname) {
    auto* out = new (std::nothrow) FilaUtilsPath();
    if (!out) {
        return nullptr;
    }
    if (pathname) {
        out->value = utils::Path(pathname);
    }
    return out;
}

void FilaUtilsPath_destroy(FilaUtilsPath* path) {
    delete path;
}

bool FilaUtilsPath_exists(const FilaUtilsPath* path) {
    return path ? path->value.exists() : false;
}

bool FilaUtilsPath_isFile(const FilaUtilsPath* path) {
    return path ? path->value.isFile() : false;
}

bool FilaUtilsPath_isDirectory(const FilaUtilsPath* path) {
    return path ? path->value.isDirectory() : false;
}

bool FilaUtilsPath_isEmpty(const FilaUtilsPath* path) {
    return !path || path->value.isEmpty();
}

bool FilaUtilsPath_isAbsolute(const FilaUtilsPath* path) {
    return path ? path->value.isAbsolute() : false;
}

void FilaUtilsPath_setPath(FilaUtilsPath* path, const char* pathname) {
    if (!path || !pathname) {
        return;
    }
    path->value.setPath(pathname);
}

size_t FilaUtilsPath_copyPath(const FilaUtilsPath* path, char* outPath, size_t outPathSize) {
    if (!path) {
        if (outPath && outPathSize > 0u) {
            outPath[0] = '\0';
        }
        return 0u;
    }
    return copyText(path->value.getPath(), outPath, outPathSize);
}

size_t FilaUtilsPath_copyName(const FilaUtilsPath* path, char* outName, size_t outNameSize) {
    if (!path) {
        if (outName && outNameSize > 0u) {
            outName[0] = '\0';
        }
        return 0u;
    }
    return copyText(path->value.getName(), outName, outNameSize);
}

size_t FilaUtilsPath_copyNameWithoutExtension(
        const FilaUtilsPath* path,
        char* outName,
        size_t outNameSize) {
    if (!path) {
        if (outName && outNameSize > 0u) {
            outName[0] = '\0';
        }
        return 0u;
    }
    return copyText(path->value.getNameWithoutExtension(), outName, outNameSize);
}

size_t FilaUtilsPath_copyExtension(const FilaUtilsPath* path, char* outExt, size_t outExtSize) {
    if (!path) {
        if (outExt && outExtSize > 0u) {
            outExt[0] = '\0';
        }
        return 0u;
    }
    return copyText(path->value.getExtension(), outExt, outExtSize);
}

FilaUtilsPath* FilaUtilsPath_getParent(const FilaUtilsPath* path) {
    return path ? createFromPath(path->value.getParent()) : nullptr;
}

FilaUtilsPath* FilaUtilsPath_getAncestor(const FilaUtilsPath* path, int n) {
    return path ? createFromPath(path->value.getAncestor(n)) : nullptr;
}

FilaUtilsPath* FilaUtilsPath_getAbsolutePath(const FilaUtilsPath* path) {
    return path ? createFromPath(path->value.getAbsolutePath()) : nullptr;
}

FilaUtilsPath* FilaUtilsPath_concat(const FilaUtilsPath* root, const FilaUtilsPath* leaf) {
    if (!root || !leaf) {
        return nullptr;
    }
    return createFromPath(root->value.concat(leaf->value));
}

bool FilaUtilsPath_concatToSelf(FilaUtilsPath* root, const FilaUtilsPath* leaf) {
    if (!root || !leaf) {
        return false;
    }
    root->value.concatToSelf(leaf->value);
    return true;
}

bool FilaUtilsPath_equals(const FilaUtilsPath* lhs, const FilaUtilsPath* rhs) {
    if (!lhs || !rhs) {
        return false;
    }
    return lhs->value == rhs->value;
}

FilaUtilsPath* FilaUtilsPath_getCurrentDirectory(void) {
    return createFromPath(utils::Path::getCurrentDirectory());
}

FilaUtilsPath* FilaUtilsPath_getCurrentExecutable(void) {
    return createFromPath(utils::Path::getCurrentExecutable());
}

FilaUtilsPath* FilaUtilsPath_getTemporaryDirectory(void) {
    return createFromPath(utils::Path::getTemporaryDirectory());
}

FilaUtilsPath* FilaUtilsPath_getUserSettingsDirectory(void) {
    return createFromPath(utils::Path::getUserSettingsDirectory());
}

bool FilaUtilsPath_mkdir(const FilaUtilsPath* path) {
    return path ? path->value.mkdir() : false;
}

bool FilaUtilsPath_mkdirRecursive(const FilaUtilsPath* path) {
    return path ? path->value.mkdirRecursive() : false;
}

bool FilaUtilsPath_unlinkFile(FilaUtilsPath* path) {
    return path ? path->value.unlinkFile() : false;
}

} // extern "C"

