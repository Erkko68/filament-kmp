#include <utils/CallStack.h>

#include <cstring>
#include <new>

#include "../../include/utils/CallStack.h"

namespace {

size_t copyCString(const char* text, char* outText, size_t outTextSize) {
    if (!text) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }
    const size_t length = std::strlen(text);
    if (!outText || outTextSize == 0u) {
        return length;
    }
    const size_t written = (length < (outTextSize - 1u)) ? length : (outTextSize - 1u);
    std::memcpy(outText, text, written);
    outText[written] = '\0';
    return length;
}

} // namespace

extern "C" {

FilaUtilsCallStack* FilaUtilsCallStack_create(void) {
    auto* callStack = new (std::nothrow) utils::CallStack();
    return reinterpret_cast<FilaUtilsCallStack*>(callStack);
}

void FilaUtilsCallStack_destroy(FilaUtilsCallStack* callStack) {
    if (!callStack) {
        return;
    }
    auto* cppCallStack = reinterpret_cast<utils::CallStack*>(callStack);
    delete cppCallStack;
}

void FilaUtilsCallStack_update(FilaUtilsCallStack* callStack, size_t ignoreFrames) {
    if (!callStack) {
        return;
    }
    auto* cppCallStack = reinterpret_cast<utils::CallStack*>(callStack);
    cppCallStack->update(ignoreFrames);
}

FilaUtilsCallStack* FilaUtilsCallStack_unwind(size_t ignoreFrames) {
    auto* callStack = new (std::nothrow) utils::CallStack(utils::CallStack::unwind(ignoreFrames));
    return reinterpret_cast<FilaUtilsCallStack*>(callStack);
}

size_t FilaUtilsCallStack_getFrameCount(const FilaUtilsCallStack* callStack) {
    if (!callStack) {
        return 0u;
    }
    auto* cppCallStack = reinterpret_cast<const utils::CallStack*>(callStack);
    return cppCallStack->getFrameCount();
}

bool FilaUtilsCallStack_getFramePc(const FilaUtilsCallStack* callStack, size_t index, intptr_t* outPc) {
    if (!callStack || !outPc) {
        return false;
    }
    auto* cppCallStack = reinterpret_cast<const utils::CallStack*>(callStack);
    if (index >= cppCallStack->getFrameCount()) {
        return false;
    }
    try {
        *outPc = (*cppCallStack)[index];
        return true;
    } catch (...) {
        return false;
    }
}

size_t FilaUtilsCallStack_demangleTypeName(const char* mangled, char* outText, size_t outTextSize) {
    if (!mangled) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }
    const utils::CString demangled = utils::CallStack::demangleTypeName(mangled);
    return copyCString(demangled.c_str_safe(), outText, outTextSize);
}

} // extern "C"

