package buildlogic.apigen.c

// The tables CBridges reads, kept by hand: what to touch when a Filament bump adds a scalar, template or string type.

/** A C++ type C holds as the scalar [c]: [toCpp] builds it from a C value, [toC] reads one back. */
internal class Scalar(val c: String, val toCpp: (String) -> String, val toC: (String) -> String)

private const val STEADY = "std::chrono::steady_clock"

/** Durations and time points are nanoseconds (since the steady clock's epoch); a tribool is 0, 1, or 2 for indeterminate. */
internal val SCALARS = mapOf(
    "std::chrono::nanoseconds" to Scalar("int64_t", { "std::chrono::nanoseconds($it)" }, { "($it).count()" }),
    "$STEADY::time_point" to Scalar(
        "int64_t",
        { "$STEADY::time_point(std::chrono::duration_cast<$STEADY::duration>(std::chrono::nanoseconds($it)))" },
        { "std::chrono::duration_cast<std::chrono::nanoseconds>(($it).time_since_epoch()).count()" },
    ),
    "utils::Entity::Type" to Scalar("uint32_t", { it }, { it }),
    "utils::bitset32" to Scalar("uint32_t", { "utils::bitset32($it)" }, { "($it).getValue()" }),
    "utils::tribool" to Scalar(
        "int32_t",
        { "utils::tribool(static_cast<utils::tribool::Value>($it))" },
        { "[](utils::tribool t) { return t.is_indeterminate() ? 2 : int32_t(t.is_true()); }($it)" },
    ),
)

/** The class templates' instantiations the libraries export, by template: C binds these, named without the arguments. */
internal val INSTANTIATIONS = mapOf(
    "filament::camutils::Manipulator" to mapOf("FLOAT" to "float"),
    "filament::camutils::Bookmark" to mapOf("FLOAT" to "float"),
)
private fun math(vararg names: String) = names.map { "filament::math::$it" }
private val CONSTANT_TYPES = listOf("int32_t", "float", "bool")
// MaterialInstance's is_supported_parameter_t; the library exports getParameter for all but the bools.
private val READABLE_PARAMETER_TYPES = listOf("float", "int32_t", "uint32_t") +
    math("int2", "int3", "int4", "uint2", "uint3", "uint4", "float2", "float3", "float4", "mat3f", "mat4f")
private val PARAMETER_TYPES = READABLE_PARAMETER_TYPES + listOf("bool") + math("bool2", "bool3", "bool4")

private fun each(parameter: String, types: List<String>) = types.map { mapOf(parameter to it) }

/**
 * The function templates' instantiations C binds, by template: each maps template parameters to arguments; ones
 * left out take their defaults. C names them by the types that tell them apart, like overloads.
 */
internal val FUNCTION_INSTANTIATIONS = mapOf(
    "filamat::MaterialBuilder::constant" to each("T", CONSTANT_TYPES),
    "filament::Material::Builder::constant" to each("T", CONSTANT_TYPES),
    "filament::Material::setDefaultParameter" to each("T", PARAMETER_TYPES),
    "filament::MaterialInstance::setParameter" to each("T", PARAMETER_TYPES),
    "filament::MaterialInstance::getParameter" to each("T", READABLE_PARAMETER_TYPES),
    "filament::MaterialInstance::setConstant" to each("T", CONSTANT_TYPES),
    "filament::MaterialInstance::getConstant" to each("T", CONSTANT_TYPES),
    "filament::RenderableManager::computeAABB" to math("float4", "half4", "float3", "half3")
        .flatMap { v -> listOf("uint16_t", "uint32_t").map { mapOf("VECTOR" to v, "INDEX" to it) } },
    "filament::geometry::TangentSpaceMesh::getAux" to each("T", math("float2", "float3", "float4", "ushort3", "ushort4")),
    // ColorConversion::ACCURATE, the default.
    "filament::Color::toLinear" to listOf(emptyMap()),
    "filament::Color::toSRGB" to listOf(emptyMap()),
)

internal const val BACKEND = "filament::backend"
internal const val PIXEL_BUFFER = "$BACKEND::PixelBufferDescriptor"
internal val UPLOADS = setOf("$BACKEND::BufferDescriptor", PIXEL_BUFFER)
internal val PIXEL_LAYOUT = listOf(
    "FilaPixelDataFormat" to "Format", "FilaPixelDataType" to "Type", "uint32_t" to "Alignment",
    "uint32_t" to "Left", "uint32_t" to "Top", "uint32_t" to "Stride",
)
// BufferDescriptor::Callback; size_t is exact here, Filament calls it.
internal val BUFFER_CALLBACK = "FilaBufferDescriptorCallback" to "typedef void (*FilaBufferDescriptorCallback)(void* buffer, size_t size, void* user);"
internal val USER_CALLBACK = "FilaCallback" to "typedef void (*FilaCallback)(void* user);"
internal val ARG_CALLBACK = "FilaArgCallback" to "typedef void (*FilaArgCallback)(void* arg, void* user);"

internal const val STATIC_STRING = "utils::StaticString"
private const val VECTOR = "utils::FixedCapacityVector"
internal const val SLICE = "utils::Slice"
internal val SEQUENCES = setOf(VECTOR, SLICE, "std::array", "std::vector")

internal val STRINGS = setOf("std::string_view", "std::string", "utils::CString", "utils::ImmutableCString", STATIC_STRING)

/** String types only literals convert to; overloads taking `const char*` cover them. */
internal val LITERAL_ONLY = setOf("filament::MaterialInstance::StringLiteral")
