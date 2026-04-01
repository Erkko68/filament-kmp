#include "../../include/backend/Program.h"

#include <backend/DriverEnums.h>
#include <backend/Program.h>
#include <utils/CString.h>

#include <algorithm>
#include <cstring>

struct FilaBackendProgram {
    filament::backend::Program* impl;
};

namespace {
bool isValidDescriptorSet(FilaBackendDescriptorSet set) {
    return set < filament::backend::MAX_DESCRIPTOR_SET_COUNT;
}

bool isValidShaderStage(FilaBackendShaderStage shaderStage) {
    return static_cast<size_t>(shaderStage) < filament::backend::Program::SHADER_TYPE_COUNT;
}
} // namespace

extern "C" {

FilaBackendProgram* FilaBackendProgram_create(void) {
    auto* program = new FilaBackendProgram;
    program->impl = new filament::backend::Program();
    return program;
}

void FilaBackendProgram_destroy(FilaBackendProgram* program) {
    if (!program) {
        return;
    }
    delete program->impl;
    delete program;
}

void FilaBackendProgram_setPriorityQueue(
        FilaBackendProgram* program, FilaBackendCompilerPriorityQueue priorityQueue) {
    if (!program) {
        return;
    }
    program->impl->priorityQueue(static_cast<filament::backend::CompilerPriorityQueue>(priorityQueue));
}

FilaBackendCompilerPriorityQueue FilaBackendProgram_getPriorityQueue(
        const FilaBackendProgram* program) {
    if (!program) {
        return FILA_BACKEND_COMPILER_PRIORITY_QUEUE_HIGH;
    }
    return static_cast<FilaBackendCompilerPriorityQueue>(program->impl->getPriorityQueue());
}

void FilaBackendProgram_setShaderLanguage(
        FilaBackendProgram* program, FilaBackendShaderLanguage shaderLanguage) {
    if (!program) {
        return;
    }
    program->impl->shaderLanguage(static_cast<filament::backend::ShaderLanguage>(shaderLanguage));
}

FilaBackendShaderLanguage FilaBackendProgram_getShaderLanguage(const FilaBackendProgram* program) {
    if (!program) {
        return FILA_BACKEND_SHADER_LANGUAGE_ESSL3;
    }
    return static_cast<FilaBackendShaderLanguage>(program->impl->getShaderLanguage());
}

void FilaBackendProgram_setShader(
        FilaBackendProgram* program, FilaBackendShaderStage shaderStage, const void* data,
        size_t size) {
    if (!program || !data || size == 0 || !isValidShaderStage(shaderStage)) {
        return;
    }
    program->impl->shader(static_cast<filament::backend::ShaderStage>(shaderStage), data, size);
}

void FilaBackendProgram_clearShader(FilaBackendProgram* program, FilaBackendShaderStage shaderStage) {
    if (!program || !isValidShaderStage(shaderStage)) {
        return;
    }
    auto stage = static_cast<size_t>(shaderStage);
    program->impl->getShadersSource()[stage].clear();
}

void FilaBackendProgram_clearAllShaders(FilaBackendProgram* program) {
    if (!program) {
        return;
    }
    for (size_t i = 0; i < filament::backend::Program::SHADER_TYPE_COUNT; i++) {
        program->impl->getShadersSource()[i].clear();
    }
}

size_t FilaBackendProgram_getShaderSize(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage) {
    if (!program) {
        return 0;
    }
    auto stage = static_cast<size_t>(shaderStage);
    if (stage >= filament::backend::Program::SHADER_TYPE_COUNT) {
        return 0;
    }
    return program->impl->getShadersSource()[stage].size();
}

size_t FilaBackendProgram_copyShader(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, void* outData,
        size_t outDataSize) {
    if (!program || !outData || outDataSize == 0) {
        return 0;
    }
    auto stage = static_cast<size_t>(shaderStage);
    if (stage >= filament::backend::Program::SHADER_TYPE_COUNT) {
        return 0;
    }
    const auto& shader = program->impl->getShadersSource()[stage];
    const size_t shaderSize = static_cast<size_t>(shader.size());
    const size_t copySize = std::min(shaderSize, outDataSize);
    if (copySize == 0) {
        return 0;
    }
    std::memcpy(outData, shader.data(), copySize);
    return copySize;
}

void FilaBackendProgram_setName(FilaBackendProgram* program, const char* name) {
    if (!program) {
        return;
    }
    program->impl->getName() = utils::CString(name ? name : "");
}

size_t FilaBackendProgram_getNameSize(const FilaBackendProgram* program) {
    if (!program) {
        return 0;
    }
    return program->impl->getName().size();
}

size_t FilaBackendProgram_copyName(
        const FilaBackendProgram* program, char* outName, size_t outNameSize) {
    if (!program || !outName || outNameSize == 0) {
        return 0;
    }
    const auto& name = program->impl->getName();
    const size_t nameSize = static_cast<size_t>(name.size());
    const size_t copySize = std::min(nameSize, outNameSize - 1);
    if (copySize > 0) {
        std::memcpy(outName, name.c_str(), copySize);
    }
    outName[copySize] = '\0';
    return copySize;
}

bool FilaBackendProgram_setDescriptorBindings(
        FilaBackendProgram* program, FilaBackendDescriptorSet set,
        const FilaBackendProgramDescriptorBindingEntry* entries, uint32_t count) {
    if (!program || !isValidDescriptorSet(set)) {
        return false;
    }
    if (count > filament::backend::MAX_DESCRIPTOR_COUNT) {
        return false;
    }
    if (count > 0 && !entries) {
        return false;
    }

    auto bindings = filament::backend::Program::DescriptorBindingsInfo::with_capacity(count);
    for (uint32_t i = 0; i < count; i++) {
        if (!entries[i].name) {
            return false;
        }
        bindings.push_back({
            .name = utils::CString(entries[i].name),
            .type = static_cast<filament::backend::DescriptorType>(entries[i].type),
            .binding = static_cast<filament::backend::descriptor_binding_t>(entries[i].binding),
        });
    }
    program->impl->descriptorBindings(set, std::move(bindings));
    return true;
}

bool FilaBackendProgram_clearDescriptorBindings(FilaBackendProgram* program, FilaBackendDescriptorSet set) {
    return FilaBackendProgram_setDescriptorBindings(program, set, nullptr, 0);
}

void FilaBackendProgram_setSingleDescriptorBinding(
        FilaBackendProgram* program, FilaBackendDescriptorSet set, const char* name,
        FilaBackendDescriptorType type, FilaBackendDescriptorBinding binding) {
    FilaBackendProgramDescriptorBindingEntry entry = {
        .name = name,
        .type = type,
        .binding = binding,
    };
    (void)FilaBackendProgram_setDescriptorBindings(program, set, &entry, 1);
}

uint32_t FilaBackendProgram_getDescriptorBindingCount(
        const FilaBackendProgram* program, FilaBackendDescriptorSet set) {
    if (!program || !isValidDescriptorSet(set)) {
        return 0;
    }
    return static_cast<uint32_t>(program->impl->getDescriptorBindings()[set].size());
}

bool FilaBackendProgram_getDescriptorBindingAt(
        const FilaBackendProgram* program, FilaBackendDescriptorSet set, uint32_t index,
        FilaBackendDescriptorType* outType, FilaBackendDescriptorBinding* outBinding) {
    if (!program || !outType || !outBinding || !isValidDescriptorSet(set)) {
        return false;
    }
    const auto& bindings = program->impl->getDescriptorBindings()[set];
    if (index >= bindings.size()) {
        return false;
    }
    const auto& descriptor = bindings[index];
    *outType = static_cast<FilaBackendDescriptorType>(descriptor.type);
    *outBinding = static_cast<FilaBackendDescriptorBinding>(descriptor.binding);
    return true;
}

size_t FilaBackendProgram_getDescriptorBindingNameSizeAt(
        const FilaBackendProgram* program, FilaBackendDescriptorSet set, uint32_t index) {
    if (!program || !isValidDescriptorSet(set)) {
        return 0;
    }
    const auto& bindings = program->impl->getDescriptorBindings()[set];
    if (index >= bindings.size()) {
        return 0;
    }
    return static_cast<size_t>(bindings[index].name.size());
}

size_t FilaBackendProgram_copyDescriptorBindingNameAt(
        const FilaBackendProgram* program, FilaBackendDescriptorSet set, uint32_t index,
        char* outName, size_t outNameSize) {
    if (!program || !outName || outNameSize == 0 || !isValidDescriptorSet(set)) {
        return 0;
    }
    const auto& bindings = program->impl->getDescriptorBindings()[set];
    if (index >= bindings.size()) {
        return 0;
    }
    const auto& name = bindings[index].name;
    const size_t nameSize = static_cast<size_t>(name.size());
    const size_t copySize = std::min(nameSize, outNameSize - 1);
    if (copySize > 0) {
        std::memcpy(outName, name.c_str(), copySize);
    }
    outName[copySize] = '\0';
    return copySize;
}

bool FilaBackendProgram_setPushConstants(
        FilaBackendProgram* program, FilaBackendShaderStage shaderStage,
        const FilaBackendProgramPushConstantEntry* entries, uint32_t count) {
    if (!program || !isValidShaderStage(shaderStage)) {
        return false;
    }
    if (count > filament::backend::MAX_PUSH_CONSTANT_COUNT) {
        return false;
    }
    if (count > 0 && !entries) {
        return false;
    }

    auto pushConstants =
        utils::FixedCapacityVector<filament::backend::Program::PushConstant>::with_capacity(count);
    for (uint32_t i = 0; i < count; i++) {
        if (!entries[i].name) {
            return false;
        }
        pushConstants.push_back({
            .name = utils::CString(entries[i].name),
            .type = static_cast<filament::backend::ConstantType>(entries[i].type),
        });
    }

    program->impl->pushConstants(
        static_cast<filament::backend::ShaderStage>(shaderStage),
        std::move(pushConstants));
    return true;
}

bool FilaBackendProgram_clearPushConstants(
        FilaBackendProgram* program, FilaBackendShaderStage shaderStage) {
    return FilaBackendProgram_setPushConstants(program, shaderStage, nullptr, 0);
}

void FilaBackendProgram_setSinglePushConstant(
        FilaBackendProgram* program, FilaBackendShaderStage shaderStage, const char* name,
        FilaBackendConstantType type) {
    FilaBackendProgramPushConstantEntry entry = {
        .name = name,
        .type = type,
    };
    (void)FilaBackendProgram_setPushConstants(program, shaderStage, &entry, 1);
}

uint32_t FilaBackendProgram_getPushConstantCount(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage) {
    if (!program || !isValidShaderStage(shaderStage)) {
        return 0;
    }
    auto stage = static_cast<filament::backend::ShaderStage>(shaderStage);
    return static_cast<uint32_t>(program->impl->getPushConstants(stage).size());
}

bool FilaBackendProgram_getPushConstantTypeAt(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, uint32_t index,
        FilaBackendConstantType* outType) {
    if (!program || !outType || !isValidShaderStage(shaderStage)) {
        return false;
    }
    auto stage = static_cast<filament::backend::ShaderStage>(shaderStage);
    const auto& constants = program->impl->getPushConstants(stage);
    if (index >= constants.size()) {
        return false;
    }
    *outType = static_cast<FilaBackendConstantType>(constants[index].type);
    return true;
}

size_t FilaBackendProgram_getPushConstantNameSizeAt(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, uint32_t index) {
    if (!program || !isValidShaderStage(shaderStage)) {
        return 0;
    }
    auto stage = static_cast<filament::backend::ShaderStage>(shaderStage);
    const auto& constants = program->impl->getPushConstants(stage);
    if (index >= constants.size()) {
        return 0;
    }
    return static_cast<size_t>(constants[index].name.size());
}

size_t FilaBackendProgram_copyPushConstantNameAt(
        const FilaBackendProgram* program, FilaBackendShaderStage shaderStage, uint32_t index,
        char* outName, size_t outNameSize) {
    if (!program || !outName || outNameSize == 0 || !isValidShaderStage(shaderStage)) {
        return 0;
    }
    auto stage = static_cast<filament::backend::ShaderStage>(shaderStage);
    const auto& constants = program->impl->getPushConstants(stage);
    if (index >= constants.size()) {
        return 0;
    }
    const auto& name = constants[index].name;
    const size_t nameSize = static_cast<size_t>(name.size());
    const size_t copySize = std::min(nameSize, outNameSize - 1);
    if (copySize > 0) {
        std::memcpy(outName, name.c_str(), copySize);
    }
    outName[copySize] = '\0';
    return copySize;
}

void FilaBackendProgram_setCacheId(FilaBackendProgram* program, uint64_t cacheId) {
    if (!program) {
        return;
    }
    program->impl->cacheId(cacheId);
}

uint64_t FilaBackendProgram_getCacheId(const FilaBackendProgram* program) {
    if (!program) {
        return 0;
    }
    return program->impl->getCacheId();
}

void FilaBackendProgram_setMultiview(FilaBackendProgram* program, bool multiview) {
    if (!program) {
        return;
    }
    program->impl->multiview(multiview);
}

bool FilaBackendProgram_isMultiview(const FilaBackendProgram* program) {
    if (!program) {
        return false;
    }
    return program->impl->isMultiview();
}

}

