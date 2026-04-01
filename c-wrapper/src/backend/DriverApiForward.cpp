#include "../../include/backend/DriverApiForward.h"

#include <backend/DriverApiForward.h>

#include <type_traits>

static_assert(std::is_same_v<filament::backend::DriverApi, filament::backend::CommandStream>,
    "filament::backend::DriverApi must remain an alias of CommandStream");

extern "C" {

FilaBackendCommandStream* FilaBackendDriverApi_asCommandStream(FilaBackendDriverApi* driverApi) {
    return reinterpret_cast<FilaBackendCommandStream*>(driverApi);
}

const FilaBackendCommandStream* FilaBackendDriverApi_asCommandStreamConst(
        const FilaBackendDriverApi* driverApi) {
    return reinterpret_cast<const FilaBackendCommandStream*>(driverApi);
}

FilaBackendDriverApi* FilaBackendCommandStream_asDriverApi(FilaBackendCommandStream* commandStream) {
    return reinterpret_cast<FilaBackendDriverApi*>(commandStream);
}

const FilaBackendDriverApi* FilaBackendCommandStream_asDriverApiConst(
        const FilaBackendCommandStream* commandStream) {
    return reinterpret_cast<const FilaBackendDriverApi*>(commandStream);
}

}

