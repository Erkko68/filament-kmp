package buildlogic.apigen

import buildlogic.apigen.c.CManifest
import buildlogic.apigen.externals.bindingFiles
import buildlogic.apigen.gaps.coverage
import buildlogic.apigen.kotlin.kotlinExternals
import buildlogic.apigen.kotlin.packageDir
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.fail

/**
 * Runs every stage on the fixture library and compares what they write with the fixture's committed output.
 * `./gradlew -p build-logic test -Papigen.update` rewrites it; review the diff.
 */
class FixtureTest {
    @Test
    fun `every stage writes the committed output`() {
        val project = Files.createTempDirectory("apigen-fixture").toFile()
        FIXTURE.copyRecursively(project)
        generate(project, Files.createTempDirectory("apigen-work").toFile())
        val differences = differences(project, FIXTURE)
        if (differences.isEmpty()) return
        if (System.getProperty("apigen.update") == "true") return update(project, differences)
        fail("The generator's output isn't the fixture's (-Papigen.update rewrites it):\n" + differences.joinToString("\n") { "  ${it.message}" })
    }

    private fun generate(project: File, work: File) {
        val apiGen = ApiGen(GEO_API, project, work)
        val writer = apiGen.writer()
        val files = writer.write()
        apiGen.write(files)
        apiGen.checkCompiles(files.keys.map(apiGen.cDir::resolve))
        val manifest = apiGen.manifest()
        apiGen.manifestFile.writeText(manifest.json())

        val kotlin = apiGen.kotlinDir!!
        val bindings = GEO_API.kotlin!!
        bindings.packages.forEach { (module, pkg) -> kotlin.resolve(packageDir(module, pkg)).deleteRecursively() }
        kotlinExternals(CManifest.parse(apiGen.manifestFile.readText()), GEO_API).forEach { (path, text) -> kotlin.resolve(path).write(text) }
        project.resolve("bindings").deleteRecursively()
        bindingFiles(manifest.headers, bindings, GEO_API.prefix).forEach { (path, text) -> project.resolve("bindings/$path").write(text) }
        apiGen.cDir.resolve("api-coverage.txt").writeText(coverage(writer, apiGen.headers, GEO_API, kotlin).text)
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

    private fun update(project: File, differences: List<Difference>) = differences.forEach { d ->
        val target = FIXTURE.resolve(d.path)
        if (d.actual == null) target.delete() else target.write(d.actual.readText())
    }

    private fun File.write(text: String) = apply { parentFile.mkdirs() }.writeText(text)

    private companion object {
        val FIXTURE = File("src/test/fixture")
    }
}
