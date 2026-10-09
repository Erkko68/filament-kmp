package buildlogic.apigen

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Runs the generator on `src/test/fixture`, a small library with none of Jolt's conventions, and compares the result
 * with the committed `c/geo/generated`. After a deliberate change to the generator, `-Papigen.update` rewrites it:
 * review the diff.
 */
class FixtureTest {
    private val config = ApiGenConfig(
        name = "Geo",
        includeDir = "include",
        astFilters = listOf("geo::"),
        prefix = "Geo",
        droppedNamespaces = setOf("geo"),
        getter = "get",
        setter = "set",
        helpers = "geoc",
        scalars = mapOf("geo::Id" to Scalar("uint32_t", "geo::Id({})", "({}).value")),
        mirrors = mapOf("geo::Vec2" to Mirror("float", 2)),
    )

    @Test
    fun generatesTheFixturesCApi() {
        val apiGen = ApiGen(config, File("src/test/fixture"), File("build/tmp/fixtureTest"))
        val files = apiGen.writer().write()
        if (System.getProperty("apigen.update") != null) apiGen.write(files)
        assertEquals(emptySet(), apiGen.stale(files), "generated files differ from the committed ones (-Papigen.update rewrites them)")
        apiGen.checkCompiles(files.keys.map(apiGen.cDir::resolve))
    }
}
