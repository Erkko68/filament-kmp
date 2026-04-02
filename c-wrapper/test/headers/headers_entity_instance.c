#include "utils/EntityInstance.h"

void test_headers_entity_instance(void) {
    FilaEntityInstance instance = 3u;
    (void)FilaEntityInstance_isValid(instance);
    (void)FilaEntityInstance_asValue(instance);
    FilaEntityInstance_clear(&instance);
}

