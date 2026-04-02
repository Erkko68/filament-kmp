#include <utils/Panic.h>

#include "../../include/utils/Panic.h"

namespace {

FilaUtilsPanicHandlerCallback gHandler = nullptr;
void* gHandlerUser = nullptr;

void bridgePanicHandler(void* user, utils::Panic const& panic) {
    (void)user;
    if (!gHandler) {
        return;
    }
    gHandler(gHandlerUser, reinterpret_cast<const FilaUtilsPanic*>(&panic));
}

} // namespace

extern "C" {

void FilaUtilsPanic_setHandler(FilaUtilsPanicHandlerCallback callback, void* userData) {
    gHandler = callback;
    gHandlerUser = userData;
    utils::Panic::setPanicHandler(callback ? bridgePanicHandler : nullptr, nullptr);
}

const char* FilaUtilsPanic_getType(const FilaUtilsPanic* panic) {
    if (!panic) {
        return nullptr;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getType();
}

const char* FilaUtilsPanic_getReason(const FilaUtilsPanic* panic) {
    if (!panic) {
        return nullptr;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getReason();
}

const char* FilaUtilsPanic_getReasonLiteral(const FilaUtilsPanic* panic) {
    if (!panic) {
        return nullptr;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getReasonLiteral();
}

const char* FilaUtilsPanic_getFunction(const FilaUtilsPanic* panic) {
    if (!panic) {
        return nullptr;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getFunction();
}

const char* FilaUtilsPanic_getFile(const FilaUtilsPanic* panic) {
    if (!panic) {
        return nullptr;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getFile();
}

int FilaUtilsPanic_getLine(const FilaUtilsPanic* panic) {
    if (!panic) {
        return -1;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getLine();
}

size_t FilaUtilsPanic_getCallStackFrameCount(const FilaUtilsPanic* panic) {
    if (!panic) {
        return 0u;
    }
    auto* cppPanic = reinterpret_cast<const utils::Panic*>(panic);
    return cppPanic->getCallStack().getFrameCount();
}

} // extern "C"


