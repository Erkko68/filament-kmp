#ifndef UBERARCHIVE_H_
#define UBERARCHIVE_H_

#include <stdint.h>

#if defined(__APPLE__)
#include <TargetConditionals.h>
#endif

extern "C" {
    extern const uint8_t UBERARCHIVE_PACKAGE[];
}

#define UBERARCHIVE_DEFAULT_OFFSET 0
#if defined(__APPLE__) && TARGET_OS_IPHONE
#define UBERARCHIVE_DEFAULT_SIZE 11373379
#elif defined(__APPLE__)
#define UBERARCHIVE_DEFAULT_SIZE 906614
#else
#define UBERARCHIVE_DEFAULT_SIZE 245805
#endif
#define UBERARCHIVE_DEFAULT_DATA (UBERARCHIVE_PACKAGE + UBERARCHIVE_DEFAULT_OFFSET)

#endif
