#include <stdio.h>

#include "filament/Color.h"
#include "filament/Material.h"

int main(void) {
    printf("Running functionality_color_options_material_metadata...\n");

    // Exercise APIs that should be safe on null handles.
    const char* name = FilaMaterial_getName((const FilaMaterial*)0);
    if (name != NULL) {
        printf("Expected null material name for null material handle\n");
        return 1;
    }

    if (FilaMaterial_hasParameter((const FilaMaterial*)0, "baseColor")) {
        printf("Unexpected parameter presence on null material\n");
        return 1;
    }

    if (FilaMaterial_isSampler((const FilaMaterial*)0, "baseColor")) {
        printf("Unexpected sampler classification on null material\n");
        return 1;
    }

    if (FilaMaterial_getParameterCount((const FilaMaterial*)0) != 0) {
        printf("Unexpected parameter count on null material\n");
        return 1;
    }

    printf("functionality_color_options_material_metadata completed\n");
    return 0;
}

