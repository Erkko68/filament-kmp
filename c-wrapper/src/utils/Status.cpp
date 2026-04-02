#include <utils/Status.h>

#include <string_view>

#include "../../include/utils/Status.h"

using NativeStatus = utils::Status;

static_assert(static_cast<int>(utils::StatusCode::OK) == FILA_STATUS_CODE_OK,
        "StatusCode::OK mismatch");
static_assert(static_cast<int>(utils::StatusCode::INVALID_ARGUMENT) == FILA_STATUS_CODE_INVALID_ARGUMENT,
        "StatusCode::INVALID_ARGUMENT mismatch");
static_assert(static_cast<int>(utils::StatusCode::INTERNAL) == FILA_STATUS_CODE_INTERNAL,
        "StatusCode::INTERNAL mismatch");
static_assert(static_cast<int>(utils::StatusCode::UNSUPPORTED) == FILA_STATUS_CODE_UNSUPPORTED,
        "StatusCode::UNSUPPORTED mismatch");

namespace {

utils::StatusCode toNativeCode(FilaStatusCode code) {
    switch (code) {
        case FILA_STATUS_CODE_OK: return utils::StatusCode::OK;
        case FILA_STATUS_CODE_INVALID_ARGUMENT: return utils::StatusCode::INVALID_ARGUMENT;
        case FILA_STATUS_CODE_INTERNAL: return utils::StatusCode::INTERNAL;
        case FILA_STATUS_CODE_UNSUPPORTED: return utils::StatusCode::UNSUPPORTED;
    }
    return utils::StatusCode::INTERNAL;
}

FilaStatusCode fromNativeCode(utils::StatusCode code) {
    switch (code) {
        case utils::StatusCode::OK: return FILA_STATUS_CODE_OK;
        case utils::StatusCode::INVALID_ARGUMENT: return FILA_STATUS_CODE_INVALID_ARGUMENT;
        case utils::StatusCode::INTERNAL: return FILA_STATUS_CODE_INTERNAL;
        case utils::StatusCode::UNSUPPORTED: return FILA_STATUS_CODE_UNSUPPORTED;
    }
    return FILA_STATUS_CODE_INTERNAL;
}

std::string_view asView(const char* message) {
    return message ? std::string_view(message) : std::string_view();
}

} // namespace

extern "C" {

FilaStatus* FilaStatus_create(FilaStatusCode code, const char* message) {
    return reinterpret_cast<FilaStatus*>(new NativeStatus(toNativeCode(code), asView(message)));
}

FilaStatus* FilaStatus_createOk(void) {
    return reinterpret_cast<FilaStatus*>(new NativeStatus(NativeStatus::ok()));
}

FilaStatus* FilaStatus_createInvalidArgument(const char* message) {
    return reinterpret_cast<FilaStatus*>(new NativeStatus(NativeStatus::invalidArgument(asView(message))));
}

FilaStatus* FilaStatus_createInternal(const char* message) {
    return reinterpret_cast<FilaStatus*>(new NativeStatus(NativeStatus::internal(asView(message))));
}

FilaStatus* FilaStatus_createUnsupported(const char* message) {
    return reinterpret_cast<FilaStatus*>(new NativeStatus(NativeStatus::unsupported(asView(message))));
}

FilaStatus* FilaStatus_clone(const FilaStatus* status) {
    if (!status) {
        return reinterpret_cast<FilaStatus*>(new NativeStatus(NativeStatus::internal("null status")));
    }
    const auto* native = reinterpret_cast<const NativeStatus*>(status);
    return reinterpret_cast<FilaStatus*>(new NativeStatus(*native));
}

void FilaStatus_destroy(FilaStatus* status) {
    delete reinterpret_cast<NativeStatus*>(status);
}

bool FilaStatus_isOk(const FilaStatus* status) {
    if (!status) {
        return false;
    }
    const auto* native = reinterpret_cast<const NativeStatus*>(status);
    return native->isOk();
}

FilaStatusCode FilaStatus_getCode(const FilaStatus* status) {
    if (!status) {
        return FILA_STATUS_CODE_INTERNAL;
    }
    const auto* native = reinterpret_cast<const NativeStatus*>(status);
    return fromNativeCode(native->getCode());
}

size_t FilaStatus_getMessageLength(const FilaStatus* status) {
    if (!status) {
        return 0u;
    }
    const auto* native = reinterpret_cast<const NativeStatus*>(status);
    return native->getMessage().size();
}

size_t FilaStatus_copyMessage(const FilaStatus* status, char* outBuffer, size_t outBufferSize) {
    if (outBuffer && outBufferSize > 0u) {
        outBuffer[0] = '\0';
    }
    if (!status || !outBuffer || outBufferSize == 0u) {
        return 0u;
    }

    const auto* native = reinterpret_cast<const NativeStatus*>(status);
    const std::string_view message = native->getMessage();
    size_t count = message.size();
    if (count >= outBufferSize) {
        count = outBufferSize - 1u;
    }

    for (size_t i = 0u; i < count; ++i) {
        outBuffer[i] = message[i];
    }
    outBuffer[count] = '\0';
    return count;
}

bool FilaStatus_equals(const FilaStatus* lhs, const FilaStatus* rhs) {
    if (lhs == rhs) {
        return true;
    }
    if (!lhs || !rhs) {
        return false;
    }
    const auto* left = reinterpret_cast<const NativeStatus*>(lhs);
    const auto* right = reinterpret_cast<const NativeStatus*>(rhs);
    return *left == *right;
}

} // extern "C"


