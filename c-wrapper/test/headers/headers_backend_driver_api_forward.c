#include "backend/DriverApiForward.h"

void test_headers_backend_driver_api_forward(void) {
    FilaBackendDriverApi* driverApi = (FilaBackendDriverApi*)0;
    FilaBackendCommandStream* commandStream = (FilaBackendCommandStream*)0;

    commandStream = FilaBackendDriverApi_asCommandStream(driverApi);
    driverApi = FilaBackendCommandStream_asDriverApi(commandStream);

    {
        const FilaBackendDriverApi* constDriverApi = (const FilaBackendDriverApi*)0;
        const FilaBackendCommandStream* constCommandStream =
            FilaBackendDriverApi_asCommandStreamConst(constDriverApi);
        constDriverApi = FilaBackendCommandStream_asDriverApiConst(constCommandStream);
        (void)constDriverApi;
    }

    (void)driverApi;
    (void)commandStream;
}

