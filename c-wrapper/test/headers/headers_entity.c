#include "utils/Entity.h"

void test_headers_entity(void) {
    FilaEntity e = 42;
    (void)FilaEntity_isNull(e);
    (void)FilaEntity_isValid(e);
    (void)FilaEntity_getId(e);
    FilaEntity_clear(&e);
}

