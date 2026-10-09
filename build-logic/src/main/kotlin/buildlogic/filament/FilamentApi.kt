package buildlogic.filament

import buildlogic.apigen.ApiGenConfig
import buildlogic.apigen.CustomBridges
import buildlogic.apigen.Mirror
import buildlogic.apigen.Scalar
import buildlogic.apigen.c.CBridge
import buildlogic.apigen.c.DirectBridges
import buildlogic.apigen.c.Unsupported
import buildlogic.apigen.c.shape
import buildlogic.apigen.cpp.CppType
import buildlogic.apigen.gaps.ApiGapsTask
import buildlogic.apigen.registerApiGenTasks
import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget
import buildlogic.platform.filamentLibDir
import buildlogic.platform.hostPlatform
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

// Everything the generator (buildlogic.apigen) is told about Filament: its config, the bridges no table expresses,
// and the tasks that need Filament's libraries.

private const val MATH = "filament::math"
private const val BACKEND = "filament::backend"
private const val STEADY = "std::chrono::steady_clock"

private fun math(vararg names: String) = names.map { "$MATH::$it" }
private val CONSTANT_TYPES = listOf("int32_t", "float", "bool")
// MaterialInstance's is_supported_parameter_t; the library exports getParameter for all but the bools.
private val READABLE_PARAMETER_TYPES = listOf("float", "int32_t", "uint32_t") +
    math("int2", "int3", "int4", "uint2", "uint3", "uint4", "float2", "float3", "float4", "mat3f", "mat4f")
private val PARAMETER_TYPES = READABLE_PARAMETER_TYPES + listOf("bool") + math("bool2", "bool3", "bool4")

private fun each(parameter: String, types: List<String>) = types.map { mapOf(parameter to it) }

private val VECTOR_ELEMENTS = mapOf(
    "float" to "float", "double" to "double", "half" to "uint16_t", "int" to "int32_t", "uint" to "uint32_t", "short" to "int16_t",
    "ushort" to "uint16_t", "bool" to "bool", "byte" to "int8_t", "ubyte" to "uint8_t",
)

/** The `filament::math` types by their storage (halves as their 16-bit storage): vectors, matrices and quaternions. */
private val MIRRORS: Map<String, Mirror> =
    VECTOR_ELEMENTS.flatMap { (name, element) -> (2..4).map { "$MATH::$name$it" to Mirror(element, it) } }.toMap() +
        (2..4).flatMap { n -> listOf("$MATH::mat$n" to Mirror("double", n * n), "$MATH::mat${n}f" to Mirror("float", n * n)) } +
        mapOf("$MATH::quatf" to Mirror("float", 4), "$MATH::quat" to Mirror("double", 4), "$MATH::quath" to Mirror("uint16_t", 4))

internal val FILAMENT_API = ApiGenConfig(
    name = "Filament",
    includeDir = "include",
    // No NEON: the API is what every target has (ToneMapper's float32x4_t overloads are ARM-only).
    clangArgs = listOf("-U__ARM_NEON"),
    // Dependencies first: filament uses utils::EntityManager, ktxreader uses image's bundles and filament.
    astFilters = listOf("utils::EntityManager", "image::Ktx", "filament::", "filamat::", "ktxreader::", "IBLPrefilterContext"),
    // Math templates are huge and declare no API of their own.
    skippedNamespaces = setOf("math"),
    prefix = "Fila",
    droppedNamespaces = setOf("filament", "backend", "math"),
    getter = "get",
    setter = "set",
    helpers = "fila",
    bridgeHeader = "../manual/FilaBridge.hpp",
    typedefs = listOf("typedef int32_t FilaEntity;"),
    // Durations and time points are nanoseconds (since the steady clock's epoch); a tribool is 0, 1, or 2 for
    // indeterminate; an Entity is its id.
    scalars = mapOf(
        "std::chrono::nanoseconds" to Scalar("int64_t", "std::chrono::nanoseconds({})", "({}).count()"),
        "$STEADY::time_point" to Scalar(
            "int64_t",
            "$STEADY::time_point(std::chrono::duration_cast<$STEADY::duration>(std::chrono::nanoseconds({})))",
            "std::chrono::duration_cast<std::chrono::nanoseconds>(({}).time_since_epoch()).count()",
        ),
        "utils::Entity::Type" to Scalar("uint32_t", "{}", "{}"),
        "utils::bitset32" to Scalar("uint32_t", "utils::bitset32({})", "({}).getValue()"),
        "utils::tribool" to Scalar(
            "int32_t",
            "utils::tribool(static_cast<utils::tribool::Value>({}))",
            "[](utils::tribool t) { return t.is_indeterminate() ? 2 : int32_t(t.is_true()); }({})",
        ),
        "utils::Entity" to Scalar("FilaEntity", "utils::Entity::import({})", "utils::Entity::smuggle({})", layout = true),
    ),
    mirrors = MIRRORS,
    sequences = setOf("utils::FixedCapacityVector", "utils::Slice"),
    views = setOf("utils::Slice"),
    // Only literals make a StaticString; fila::staticString makes one because everything taking it copies it.
    strings = mapOf("utils::CString" to "utils::CString", "utils::ImmutableCString" to "utils::ImmutableCString", "utils::StaticString" to "fila::staticString"),
    literalOnly = setOf("filament::MaterialInstance::StringLiteral"),
    // The instantiations the libraries export.
    instantiations = mapOf(
        "filament::camutils::Manipulator" to mapOf("FLOAT" to "float"),
        "filament::camutils::Bookmark" to mapOf("FLOAT" to "float"),
    ),
    functionInstantiations = mapOf(
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
    ),
    custom = FilamentBridges,
)

/** Buffers C lends Filament, callbacks with user data, and component instances. */
internal data object FilamentBridges : CustomBridges {
    private const val PIXEL_BUFFER = "$BACKEND::PixelBufferDescriptor"
    private val UPLOADS = setOf("$BACKEND::BufferDescriptor", PIXEL_BUFFER)
    private val PIXEL_LAYOUT = listOf(
        "FilaPixelDataFormat" to "Format", "FilaPixelDataType" to "Type", "uint32_t" to "Alignment",
        "uint32_t" to "Left", "uint32_t" to "Top", "uint32_t" to "Stride",
    )
    // BufferDescriptor::Callback; size_t is exact here, Filament calls it.
    private val BUFFER_CALLBACK = "FilaBufferDescriptorCallback" to "typedef void (*FilaBufferDescriptorCallback)(void* buffer, size_t size, void* user);"
    private val USER_CALLBACK = "FilaCallback" to "typedef void (*FilaCallback)(void* user);"
    private val ARG_CALLBACK = "FilaArgCallback" to "typedef void (*FilaArgCallback)(void* arg, void* user);"
    private val MATH_VECTOR = Regex("$MATH::vec([234])")

    /** A math template's instantiation is one of the mirrored typedefs: vec3<float> is float3. */
    override fun canonical(decl: String, type: CppType, substitute: (String) -> String) =
        MATH_VECTOR.matchEntire(decl)?.let { "$MATH::${substitute(type.args.single().spelling)}${it.groupValues[1]}" } ?: decl

    override fun bridge(decl: String, type: CppType, alias: String?, direct: DirectBridges) = when {
        decl in UPLOADS -> upload(decl, pixels = decl == PIXEL_BUFFER, direct)
        decl == "utils::Invocable" -> invocable(type, direct)
        decl == "utils::EntityInstance" && alias != null -> instance(alias, direct)
        else -> null
    }

    /** A buffer C lends Filament: Kotlin's `Upload` fields, and a pixel buffer's layout between size and callback. */
    private fun upload(decl: String, pixels: Boolean, direct: DirectBridges): CBridge {
        if (direct.result || !(direct.byValue || direct.indirection == "&&")) throw Unsupported("$decl result")
        val layout = if (pixels) PIXEL_LAYOUT else emptyList()
        return CBridge(
            "void*",
            { n ->
                val args = layout.joinToString("") { (c, suffix) -> ", " + if (c.startsWith("Fila")) "static_cast<$BACKEND::${c.removePrefix("Fila")}>($n$suffix)" else "$n$suffix" }
                "$decl($n, ${n}Size$args, ${n}Callback, ${n}User)"
            },
            extra = listOf("uint32_t" to "Size") + layout + listOf(BUFFER_CALLBACK.first to "Callback", "void*" to "User"),
            typedef = BUFFER_CALLBACK,
        )
    }

    /**
     * A C++ callable C passes as a function pointer and its user data: `void (*)(void* user)`, or
     * `void (*)(void* arg, void* user)` for one pointer argument; NULL is an empty one.
     */
    private fun invocable(type: CppType, direct: DirectBridges): CBridge {
        if (direct.result) throw Unsupported("${type.spelling} result")
        val signature = type.args.single()
        val arg = signature.args.drop(1).singleOrNull()
        if (signature.args.first().spelling != "void") throw Unsupported("${type.spelling}: returns a value")
        if (signature.args.size > 2 || (arg != null && shape(arg.spelling).indirection != listOf("*"))) throw Unsupported("${type.spelling}: C callbacks take at most one pointer")
        val callback = if (arg == null) USER_CALLBACK else ARG_CALLBACK
        return CBridge(
            callback.first,
            { n -> "fila::callable($n, " + (if (arg == null) "[=] { $n(${n}User); }" else "[=](auto* arg) { $n((void*) arg, ${n}User); }") + ")" },
            extra = listOf("void*" to "User"),
            typedef = callback,
        )
    }

    /** A component's instance is its index, built as the manager's alias names it. */
    private fun instance(alias: String, direct: DirectBridges): CBridge {
        if (!direct.byValue) throw Unsupported("$alias by pointer")
        return CBridge("uint32_t", if (direct.result) { v -> "$v.asValue()" } else { v -> "$alias($v)" })
    }
}

private val FILAMENT_LIBRARIES = listOf("filament", "gltfio_core", "filamat", "camutils", "geometry", "filament-iblprefilter", "utils")

/** The Kotlin package of each C module's generated externals. */
private val PACKAGES = mapOf(
    "filament" to "io.github.erkko68.filament.capi",
    "filamat" to "io.github.erkko68.filament.filamat.capi",
    "filament-utils" to "io.github.erkko68.filament.utils.capi",
    "gltfio" to "io.github.erkko68.filament.gltfio.capi",
)

/** The generator's tasks for Filament, and `apiGaps`, which reads Filament's libraries. */
fun Project.registerFilamentApiGen() {
    registerApiGenTasks(FILAMENT_API, PACKAGES, wasmRuntimes = mapOf("filamat" to "filamat-kmp"))

    if (hostPlatform() == "windows") return // nm can't read MSVC objects
    val target = FilamentTarget.host()
    // The C API's objects at -O0, so calls to Filament's inline methods stay calls.
    val cBuild = registerCApiBuild("apiGapsCBuild", target) {
        description = "Builds the Fila* C API's objects without inlining, for apiGaps."
        buildType.set("Debug")
        buildDir.set(layout.buildDirectory.dir("cmake/api-gaps"))
        outputDir.set(layout.buildDirectory.dir("filament-c/api-gaps"))
        arguments.add("-DJNI_HOME=${System.getProperty("java.home").replace('\\', '/')}")
        targets.addAll(PACKAGES.keys.map { "fila-$it" })
    }

    tasks.register<ApiGapsTask>("apiGaps") {
        group = "verification"
        description = "Reports the Filament C++ API the Fila* C API doesn't call."
        config.set(FILAMENT_API)
        projectDir.set(layout.projectDirectory)
        filamentLibraries.from(filamentLibDir(target).map { dir -> FILAMENT_LIBRARIES.map { dir.file("lib$it.a") } })
        cApiObjects.from(cBuild.flatMap { it.buildDir }.map { it.asFileTree.matching { include("CMakeFiles/fila-*.dir/**/*.o") } })
        report.set(layout.buildDirectory.file("reports/api-gaps.txt"))
        dependsOn(cBuild)
    }
}
