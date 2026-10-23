package buildlogic.apigen

import buildlogic.apigen.bindings.bindingFiles
import buildlogic.apigen.c.CManifest
import buildlogic.apigen.cpp.modelReport
import buildlogic.apigen.report.KotlinDeclarations
import buildlogic.apigen.report.coverage
import buildlogic.apigen.report.defaultsCheck
import buildlogic.apigen.report.enumCheck
import buildlogic.apigen.report.kotlinApiSources
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Runs every stage on the fixture library, a project laid out like this repo, and compares what they write with the
 * fixture's committed output. `./gradlew -p build-logic test -Papigen.update` rewrites it; review the diff.
 */
class FixtureTest {
    @Test
    fun `every stage writes the committed output`() {
        val project = temporaryDir().also { FIXTURE.copyRecursively(it) }
        generate(ApiGen(GEO_API, project, temporaryDir()), project)
        val differences = differences(project, FIXTURE)
        if (differences.isEmpty()) return
        if (System.getProperty("apigen.update") == "true") return update(differences)
        fail("The generator's output isn't the fixture's (-Papigen.update rewrites it):\n" + differences.joinToString("\n") { "  ${it.message}" })
    }

    @Test
    fun `apiVerify finds a Kotlin enum value that isn't C++'s`() {
        val api = ApiGen(GEO_API, FIXTURE, temporaryDir()).api()
        assertEquals(emptyList(), enumCheck(api, KotlinDeclarations(kotlinApiSources(FIXTURE.resolve("kotlin")))).wrong)
        val wrong = KotlinDeclarations("enum class Kind(internal val value: Int) { CIRCLE(0), POLYGON(4) }")
        assertEquals(listOf("geo::Kind::POLYGON is 5 in C++, 4 in Kotlin"), enumCheck(api, wrong).wrong)
    }

    @Test
    fun `apiVerify finds a Kotlin default that isn't C++'s`() {
        val api = ApiGen(GEO_API, FIXTURE, temporaryDir()).api()
        val check = defaultsCheck(api, KotlinDeclarations(kotlinApiSources(FIXTURE.resolve("kotlin"))), GEO_API.naming)
        assertEquals(6 to emptyList(), check.checked to check.wrong)
        val wrong = KotlinDeclarations("class Style(var visible: Boolean = false) {\n var width: Float = 2f\n var kind: Kind = Kind.POLYGON\n var weights = floatArrayOf(0f, 1f)\n}")
        assertEquals(
            listOf(
                "geo::Style::mWidth is 1.5 in C++, 2f in Kotlin",
                "geo::Style::mKind is geo::Kind::CIRCLE in C++, Kind.POLYGON in Kotlin",
                "geo::Style::mWeights is {} in C++, floatArrayOf(0f, 1f) in Kotlin",
                "geo::Style::mVisible is true in C++, false in Kotlin",
            ),
            defaultsCheck(api, wrong, GEO_API.naming).wrong,
        )
    }

    /** What the tasks write, the glue and the model report in place of the build dir. */
    private fun generate(apiGen: ApiGen, project: File) {
        apiGen.generateCApi()
        apiGen.generateKotlinExternals()
        val bindings = project.resolve("bindings")
        GeneratedFiles(bindings, listOf(bindings), bindingFiles(CManifest.parse(apiGen.manifestFile.readText()).headers, GEO_API.kotlin!!, GEO_API.c.prefix)).write()
        val api = apiGen.api()
        apiGen.coverageFile.writeText(coverage(api, apiGen.cApi(api).bindings, GEO_API.c.prefix, kotlinApiSources(apiGen.kotlinDir!!)).text)
        project.resolve("reports/api-model.txt").write(modelReport(apiGen.model()).text)
    }

    private class Difference(val path: String, val actual: File?, val message: String)

    /** The files under [actual] and [expected] that differ, by path. */
    private fun differences(actual: File, expected: File): List<Difference> {
        fun files(root: File) = root.walkTopDown().filter { it.isFile }.associateBy { it.relativeTo(root).invariantSeparatorsPath }
        val (a, e) = files(actual) to files(expected)
        return (a.keys + e.keys).sorted().mapNotNull { path ->
            val (x, y) = a[path] to e[path]
            when {
                x == null -> Difference(path, null, "$path: not written")
                y == null -> Difference(path, x, "$path: written, not in the fixture")
                x.readText() == y.readText() -> null
                else -> {
                    val (xl, yl) = x.readLines() to y.readLines()
                    val line = xl.indices.firstOrNull { it >= yl.size || xl[it] != yl[it] } ?: xl.size
                    Difference(path, x, "$path:${line + 1}: '${xl.getOrNull(line)}', expected '${yl.getOrNull(line)}'")
                }
            }
        }
    }

    private fun update(differences: List<Difference>) = differences.forEach { d ->
        val target = FIXTURE.resolve(d.path)
        if (d.actual == null) target.delete() else target.write(d.actual.readText())
    }

    private fun File.write(text: String) = apply { parentFile.mkdirs() }.writeText(text)

    private fun temporaryDir() = Files.createTempDirectory("apigen").toFile()

    private companion object {
        val FIXTURE = File("src/test/fixture")
    }
}
