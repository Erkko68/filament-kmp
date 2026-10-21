#include "../generated/Includes.hpp"
#include "FilaColorGradingManual.h"

#include <algorithm>

using namespace filament;

struct FilaColorGradingLut {
    const void* data;
    uint32_t info[6];
};

namespace {
struct Export {
    FilaArgCallback callback;
    void* user;
};
} // namespace

extern "C" {

void FilaColorGradingBuilder_exportLut(FilaColorGradingBuilder* self, FilaArgCallback callback, void* callbackUser) {
    fila::cpp(self)->exportLut([](void const* data, size_t size, backend::PixelDataFormat format, backend::PixelDataType type,
            uint32_t width, uint32_t height, uint32_t depth, void* user) {
        FilaColorGradingLut lut{ data, { uint32_t(size), uint32_t(format), uint32_t(type), width, height, depth } };
        auto* e = static_cast<Export*>(user);
        e->callback(&lut, e->user);
        delete e;
    }, new Export{ callback, callbackUser });
}

const void* FilaColorGradingLut_getData(const FilaColorGradingLut* lut, uint32_t* outInfo) {
    std::copy(std::begin(lut->info), std::end(lut->info), outInfo);
    return lut->data;
}

} // extern "C"
