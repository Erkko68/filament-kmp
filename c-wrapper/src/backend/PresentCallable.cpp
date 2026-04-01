#include "../../include/backend/PresentCallable.h"

#include <backend/PresentCallable.h>

struct FilaBackendPresentCallable {
    filament::backend::PresentCallable impl;
    FilaBackendPresentFn fn;
    void* user;

    FilaBackendPresentCallable(FilaBackendPresentFn callback, void* callbackUser)
            : impl(callback ? callback : filament::backend::PresentCallable::noopPresent, callbackUser),
              fn(callback),
              user(callbackUser) {
    }
};

extern "C" {

FilaBackendPresentCallable* FilaBackendPresentCallable_create(FilaBackendPresentFn fn, void* user) {
    return new FilaBackendPresentCallable(fn, user);
}

void FilaBackendPresentCallable_destroy(FilaBackendPresentCallable* callable) {
    delete callable;
}

void FilaBackendPresentCallable_invoke(FilaBackendPresentCallable* callable, bool presentFrame) {
    if (!callable) {
        return;
    }
    callable->impl(presentFrame);
}

bool FilaBackendPresentCallable_isValid(const FilaBackendPresentCallable* callable) {
    return callable != nullptr;
}

void* FilaBackendPresentCallable_getUser(const FilaBackendPresentCallable* callable) {
    if (!callable) {
        return nullptr;
    }
    return callable->user;
}

FilaBackendPresentFn FilaBackendPresentCallable_getFunction(const FilaBackendPresentCallable* callable) {
    if (!callable) {
        return nullptr;
    }
    return callable->fn;
}

}

