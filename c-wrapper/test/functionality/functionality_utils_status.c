#include <stdio.h>
#include <string.h>

#include "utils/Status.h"

int main(void) {
    printf("Running utils status functionality program...\n");

    FilaStatus* invalid = FilaStatus_createInvalidArgument("bad input");
    if (!invalid) {
        printf("Failed to create invalid status\n");
        return 1;
    }

    if (FilaStatus_isOk(invalid)) {
        printf("Invalid status unexpectedly reports OK\n");
        FilaStatus_destroy(invalid);
        return 1;
    }

    if (FilaStatus_getCode(invalid) != FILA_STATUS_CODE_INVALID_ARGUMENT) {
        printf("Unexpected status code\n");
        FilaStatus_destroy(invalid);
        return 1;
    }

    char buffer[32];
    size_t copied = FilaStatus_copyMessage(invalid, buffer, sizeof(buffer));
    if (copied != strlen("bad input") || strcmp(buffer, "bad input") != 0) {
        printf("Status message copy mismatch\n");
        FilaStatus_destroy(invalid);
        return 1;
    }

    FilaStatus* clone = FilaStatus_clone(invalid);
    if (!clone || !FilaStatus_equals(invalid, clone)) {
        printf("Status clone/equality mismatch\n");
        FilaStatus_destroy(invalid);
        FilaStatus_destroy(clone);
        return 1;
    }

    FilaStatus* ok = FilaStatus_createOk();
    if (!ok || !FilaStatus_isOk(ok) || FilaStatus_getCode(ok) != FILA_STATUS_CODE_OK) {
        printf("OK status mismatch\n");
        FilaStatus_destroy(invalid);
        FilaStatus_destroy(clone);
        FilaStatus_destroy(ok);
        return 1;
    }

    FilaStatus_destroy(invalid);
    FilaStatus_destroy(clone);
    FilaStatus_destroy(ok);

    printf("Utils status functionality program completed\n");
    return 0;
}

