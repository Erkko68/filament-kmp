#ifndef UTILS_TRIBOOL_H
#define UTILS_TRIBOOL_H

#include <stdint.h>

namespace utils {
enum class tribool : int8_t {
    FALSE = 0,
    TRUE = 1,
    INDETERMINATE = -1
};
}

#endif // UTILS_TRIBOOL_H
