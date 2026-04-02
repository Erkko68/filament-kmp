#include "utils/Status.h"

void test_headers_status(void) {
    FilaStatus* ok = FilaStatus_createOk();
    FilaStatus* invalid = FilaStatus_createInvalidArgument("bad input");
    FilaStatus* internal = FilaStatus_createInternal("internal");
    FilaStatus* unsupported = FilaStatus_createUnsupported("unsupported");
    FilaStatus* custom = FilaStatus_create(FILA_STATUS_CODE_OK, "ok");
    FilaStatus* copy = FilaStatus_clone(custom);

    char message[32];
    (void)FilaStatus_isOk(ok);
    (void)FilaStatus_getCode(invalid);
    (void)FilaStatus_getMessageLength(internal);
    (void)FilaStatus_copyMessage(unsupported, message, sizeof(message));
    (void)FilaStatus_equals(custom, copy);

    FilaStatus_destroy(ok);
    FilaStatus_destroy(invalid);
    FilaStatus_destroy(internal);
    FilaStatus_destroy(unsupported);
    FilaStatus_destroy(custom);
    FilaStatus_destroy(copy);
}

