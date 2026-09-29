package buildlogic.apigen.gaps

import buildlogic.apigen.cpp.CppApi

/**
 * The exported classes' qualified names (templates without their arguments, `camutils::Manipulator`), and the
 * mangled names of their public methods, header-inline ones included. [declared] adds their non-public ones:
 * libraries export those too.
 */
internal class HeaderApi(val publicClasses: Set<String>, val methods: Set<String>, val declared: Set<String>)

internal fun CppApi.headerApi(): HeaderApi {
    // The backend's own exported classes (Platform, BufferDescriptor) sit outside the public headers' API.
    val exported = records.values.filter { it.exported && !it.name.startsWith("filament::backend::") }
    val methods = exported.flatMap { it.methods }
    return HeaderApi(
        exported.mapTo(HashSet()) { it.name },
        methods.filter { it.isPublic && it.isApi }.mapNotNullTo(HashSet()) { it.mangled },
        methods.mapNotNullTo(HashSet()) { it.mangled },
    )
}
