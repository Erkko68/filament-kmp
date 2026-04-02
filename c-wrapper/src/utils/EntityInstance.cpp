#include "../../include/utils/EntityInstance.h"

extern "C" {

bool FilaEntityInstance_isValid(FilaEntityInstance instance) {
    return instance != 0u;
}

uint32_t FilaEntityInstance_asValue(FilaEntityInstance instance) {
    return instance;
}

void FilaEntityInstance_clear(FilaEntityInstance* inOutInstance) {
    if (!inOutInstance) {
        return;
    }
    *inOutInstance = 0u;
}

} // extern "C"


