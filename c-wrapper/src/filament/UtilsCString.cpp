#include <utils/CString.h>

#include <new>
#include <string_view>

#include "../../include/utils/CString.h"

struct FilaUtilsCString {
    utils::CString value;
};

namespace {
size_t copyText(std::string_view text, char* outText, size_t outTextSize) {
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
} // namespace

extern "C" {

FilaUtilsCString* FilaUtilsCString_create(void) {
    return new (std::nothrow) FilaUtilsCString();
}

FilaUtilsCString* FilaUtilsCString_createFromUtf8(const char* text) {
    auto* out = new (std::nothrow) FilaUtilsCString();
    if (!out) {
        return nullptr;
    }
    if (text) {
        out->value = utils::CString(text);
    }
    return out;
}

FilaUtilsCString* FilaUtilsCString_createFromData(const char* data, size_t length) {
    auto* out = new (std::nothrow) FilaUtilsCString();
    if (!out) {
        return nullptr;
    }
    if (data && length > 0u) {
        out->value = utils::CString(data, length);
    }
    return out;
}

void FilaUtilsCString_destroy(FilaUtilsCString* cstring) {
    delete cstring;
}

size_t FilaUtilsCString_size(const FilaUtilsCString* cstring) {
    return cstring ? cstring->value.size() : 0u;
}

bool FilaUtilsCString_empty(const FilaUtilsCString* cstring) {
    return !cstring || cstring->value.empty();
}

size_t FilaUtilsCString_copyUtf8(const FilaUtilsCString* cstring, char* outText, size_t outTextSize) {
    if (!cstring) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }
    return copyText(std::string_view(cstring->value.data(), cstring->value.size()), outText, outTextSize);
}

bool FilaUtilsCString_appendUtf8(FilaUtilsCString* cstring, const char* text) {
    if (!cstring || !text) {
        return false;
    }
    cstring->value += text;
    return true;
}

bool FilaUtilsCString_append(FilaUtilsCString* cstring, const FilaUtilsCString* suffix) {
    if (!cstring || !suffix) {
        return false;
    }
    cstring->value += suffix->value;
    return true;
}

void FilaUtilsCString_clear(FilaUtilsCString* cstring) {
    if (!cstring) {
        return;
    }
    cstring->value = utils::CString();
}

bool FilaUtilsCString_equals(const FilaUtilsCString* lhs, const FilaUtilsCString* rhs) {
    if (!lhs || !rhs) {
        return false;
    }
    return lhs->value == rhs->value;
}

} // extern "C"

