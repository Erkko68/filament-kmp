#ifndef FILAMENT_C_BACKEND_DRIVERAPIFORWARD_H
#define FILAMENT_C_BACKEND_DRIVERAPIFORWARD_H

#ifdef __cplusplus
extern "C" {
#endif

typedef struct FilaBackendCommandStream FilaBackendCommandStream;
typedef struct FilaBackendDriverApi FilaBackendDriverApi;

FilaBackendCommandStream* FilaBackendDriverApi_asCommandStream(FilaBackendDriverApi* driverApi);
const FilaBackendCommandStream* FilaBackendDriverApi_asCommandStreamConst(
    const FilaBackendDriverApi* driverApi);

FilaBackendDriverApi* FilaBackendCommandStream_asDriverApi(FilaBackendCommandStream* commandStream);
const FilaBackendDriverApi* FilaBackendCommandStream_asDriverApiConst(
    const FilaBackendCommandStream* commandStream);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_BACKEND_DRIVERAPIFORWARD_H

