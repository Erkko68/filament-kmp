#include <utils/ImmutableCString.h>

#include <new>
#include <string_view>

#include "../../include/utils/ImmutableCString.h"

struct FilaUtilsImmutableCString {
    utils::ImmutableCString value;
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

FilaUtilsImmutableCString* FilaUtilsImmutableCString_create(void) {
    return new (std::nothrow) FilaUtilsImmutableCString();
}

FilaUtilsImmutableCString* FilaUtilsImmutableCString_createFromUtf8(const char* text) {
    auto* out = new (std::nothrow) FilaUtilsImmutableCString();
    if (!out) {
        return nullptr;
    }
    if (text) {
        out->value = utils::ImmutableCString(text);
    }
    return out;
}

FilaUtilsImmutableCString* FilaUtilsImmutableCString_createFromData(const char* data, size_t length) {
    auto* out = new (std::nothrow) FilaUtilsImmutableCString();
    if (!out) {
        return nullptr;
    }
    if (data && length > 0u) {
        out->value = utils::ImmutableCString(data, length);
    }
    return out;
}

void FilaUtilsImmutableCString_destroy(FilaUtilsImmutableCString* cstring) {
    delete cstring;
}

size_t FilaUtilsImmutableCString_size(const FilaUtilsImmutableCString* cstring) {
    return cstring ? cstring->value.size() : 0u;
}

bool FilaUtilsImmutableCString_empty(const FilaUtilsImmutableCString* cstring) {
    return !cstring || cstring->value.empty();
}

bool FilaUtilsImmutableCString_isStatic(const FilaUtilsImmutableCString* cstring) {
    return cstring ? cstring->value.isStatic() : true;
}

size_t FilaUtilsImmutableCString_copyUtf8(
        const FilaUtilsImmutableCString* cstring,
        char* outText,
        size_t outTextSize) {
    if (!cstring) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }
    return copyText(std::string_view(cstring->value.data(), cstring->value.size()), outText, outTextSize);
}

bool FilaUtilsImmutableCString_equals(
        const FilaUtilsImmutableCString* lhs,
        const FilaUtilsImmutableCString* rhs) {
    if (!lhs || !rhs) {
        return false;
    }
    return lhs->value == rhs->value;
}

} // extern "C"

