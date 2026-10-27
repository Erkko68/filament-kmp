package buildlogic.apigen

import buildlogic.apigen.c.CBridge
import buildlogic.apigen.c.DirectBridges
import buildlogic.apigen.c.Unsupported
import buildlogic.apigen.cpp.CppType

/** The fixture library's config: conventions unlike Filament's, and every table Filament leaves empty. */
val GEO_API = ApiGenConfig(
    name = "Geo",
    sources = Sources(includeDir = "include", astFilters = listOf("geo::"), skippedNamespaces = setOf("math")),
    c = CBindings(
        prefix = "Geo",
        helpers = "geo_c",
        droppedNamespaces = setOf("geo", "detail"),
        bridgeHeader = "../manual/GeoBridge.hpp",
        typedefs = listOf("typedef uint32_t GeoHandle;"),
    ),
    naming = Naming(memberPrefix = "^m(?=[A-Z])"),
    types = TypeTables(
        scalars = mapOf(
            "geo::Handle" to Scalar("GeoHandle", "geo::Handle::fromId({})", "({}).getId()", layout = true),
            "std::chrono::nanoseconds" to Scalar("int64_t", "std::chrono::nanoseconds({})", "({}).count()"),
        ),
        mirrors = mapOf(
            "geo::math::float2" to Mirror("float", 2),
            "geo::math::float3" to Mirror("float", 3),
            "geo::math::float4" to Mirror("float", 4, aligned = true),
        ),
        sequences = setOf("geo::Span"),
        views = setOf("geo::Span"),
        refs = RefCounting(pointers = mapOf("geo::Ref" to false), targets = setOf("geo::RefTarget"), get = "get", addRef = "addRef", release = "release"),
        results = setOf("geo::Result"),
        custom = GeoBridges,
    ),
    templates = Instantiations(
        classes = mapOf("geo::Box" to mapOf("T" to "float")),
        functions = mapOf(
            "geo::Canvas::SetValue" to listOf(mapOf("T" to "float"), mapOf("T" to "int32_t")),
            "geo::Canvas::GetValue" to listOf(mapOf("T" to "float"), mapOf("T" to "int32_t")),
        ),
    ),
    kotlin = KotlinBindings(
        packages = mapOf("geo" to "geo.capi", "geo-io" to "geo.io.capi"),
        interop = "geo.interop",
        wasmRuntime = "geo",
        wasmPackage = "geo.wasm",
        wasmRuntimes = mapOf("geo-io" to "geo-io"),
    ),
)

/** A buffer C lends the library, with the callback that hands it back. */
data object GeoBridges : CustomBridges {
    private val CALLBACK = "GeoBlobCallback" to "typedef void (*GeoBlobCallback)(void* buffer, size_t size, void* user);"

    override fun bridge(decl: String, type: CppType, alias: String?, direct: DirectBridges): CBridge? {
        if (decl != "geo::Blob") return null
        if (direct.result) throw Unsupported("$decl result")
        return CBridge(
            "void*",
            { n -> "geo::Blob($n, ${n}Size, ${n}Callback, ${n}User)" },
            extra = listOf("uint32_t" to "Size", CALLBACK.first to "Callback", "void*" to "User"),
            typedef = CALLBACK,
        )
    }
}
