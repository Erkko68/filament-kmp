#include "backend/BufferDescriptor.h"
#include "backend/CallbackHandler.h"
#include "backend/PixelBufferDescriptor.h"

static void dispatch_cb(void* callbackUser, FilaCallbackHandlerCallback callback, void* handlerUser) {
    (void)handlerUser;
    callback(callbackUser);
}

static void release_cb(void* buffer, size_t size, void* user) {
    (void)buffer;
    (void)size;
    (void)user;
}

void test_headers_backend_descriptor(void) {
    FilaCallbackHandler* handler = FilaCallbackHandler_create(dispatch_cb, (void*)0x9);

    FilaBufferDescriptor* bd = FilaBufferDescriptor_create((const void*)0x1, 16, release_cb, (void*)0x2);
    FilaBufferDescriptor_hasCallback(bd);
    FilaBufferDescriptor_getCallback(bd);
    FilaBufferDescriptor_getUser(bd);
    FilaBufferDescriptor_getHandler(bd);
    FilaBufferDescriptor_setCallback(bd, release_cb, (void*)0x3);
    FilaBufferDescriptor_getBuffer(bd);
    FilaBufferDescriptor_getSize(bd);
    FilaBufferDescriptor_destroy(bd);

    bd = FilaBufferDescriptor_createWithHandler((const void*)0x1, 16, handler, release_cb, (void*)0x2);
    FilaBufferDescriptor_setCallbackWithHandler(bd, handler, release_cb, (void*)0x4);
    FilaBufferDescriptor_destroy(bd);

    FilaPixelBufferDescriptor* pbd = FilaPixelBufferDescriptor_create(
        (const void*)0x4,
        64,
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE,
        1,
        release_cb,
        (void*)0x5);
    FilaPixelBufferDescriptor_getFormat(pbd);
    FilaPixelBufferDescriptor_getType(pbd);
    FilaPixelBufferDescriptor_getAlignment(pbd);
    FilaPixelBufferDescriptor_getLeft(pbd);
    FilaPixelBufferDescriptor_getTop(pbd);
    FilaPixelBufferDescriptor_getStride(pbd);
    FilaPixelBufferDescriptor_computePixelSize(FILA_PIXEL_DATA_FORMAT_RGBA, FILA_PIXEL_DATA_TYPE_UBYTE);
    FilaPixelBufferDescriptor_computeDataSize(FILA_PIXEL_DATA_FORMAT_RGBA, FILA_PIXEL_DATA_TYPE_UBYTE, 4, 4, 1);
    FilaPixelBufferDescriptor_setCallback(pbd, release_cb, (void*)0x6);
    FilaPixelBufferDescriptor_hasCallback(pbd);
    FilaPixelBufferDescriptor_getCallback(pbd);
    FilaPixelBufferDescriptor_getUser(pbd);
    FilaPixelBufferDescriptor_getHandler(pbd);
    FilaPixelBufferDescriptor_destroy(pbd);

    pbd = FilaPixelBufferDescriptor_createWithLayout(
        (const void*)0x4,
        64,
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE,
        1,
        1,
        2,
        4,
        release_cb,
        (void*)0x5);
    FilaPixelBufferDescriptor_destroy(pbd);

    pbd = FilaPixelBufferDescriptor_createWithHandler(
        (const void*)0x4,
        64,
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE,
        1,
        handler,
        release_cb,
        (void*)0x5);
    FilaPixelBufferDescriptor_setCallbackWithHandler(pbd, handler, release_cb, (void*)0x6);
    FilaPixelBufferDescriptor_destroy(pbd);

    FilaPixelBufferDescriptor* cpbd = FilaPixelBufferDescriptor_createCompressed(
        (const void*)0x7,
        128,
        FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_RGB8,
        128,
        release_cb,
        (void*)0x8);
    FilaPixelBufferDescriptor_getImageSize(cpbd);
    FilaPixelBufferDescriptor_getCompressedFormat(cpbd);
    FilaPixelBufferDescriptor_destroy(cpbd);

    cpbd = FilaPixelBufferDescriptor_createCompressedWithHandler(
        (const void*)0x7,
        128,
        FILA_COMPRESSED_PIXEL_DATA_TYPE_ETC2_RGB8,
        128,
        handler,
        release_cb,
        (void*)0x8);
    FilaPixelBufferDescriptor_destroy(cpbd);

    FilaCallbackHandler_destroy(handler);
}

