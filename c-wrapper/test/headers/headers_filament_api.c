#include "filament/FilamentAPI.h"

void test_headers_filament_api(void) {
#if !defined(FILA_FILAMENT_API_AVAILABLE)
#error FILA_FILAMENT_API_AVAILABLE must be defined
#endif
    (void)FILA_FILAMENT_API_AVAILABLE;
}

