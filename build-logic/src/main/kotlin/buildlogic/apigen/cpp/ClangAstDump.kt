package buildlogic.apigen.cpp

import buildlogic.apigen.ApiGenConfig
import groovy.json.JsonSlurper
import java.io.File

/** Runs clang, its stdout into [stdout] when given; returns its exit code and what it wrote to stderr. */
internal fun clang(command: List<String>, workDir: File, stdout: File? = null): Pair<Int, String> {
    val errors = workDir.resolve("clang.err")
    val process = ProcessBuilder(command)
        .redirectOutput(stdout?.let { ProcessBuilder.Redirect.to(it) } ?: ProcessBuilder.Redirect.DISCARD)
        .redirectError(errors).start()
    return process.waitFor() to errors.readText().also { errors.delete() }
}

/** clang's JSON AST dump: one top-level document per declaration whose qualified name contains a filter. */
internal class ClangAstDump(private val config: ApiGenConfig, private val workDir: File) {
    fun forEachDeclaration(unit: File, includeDir: File, filter: String, skip: Set<String>, action: (Map<*, *>) -> Unit) {
        val dump = workDir.resolve("ast.json")
        val command = listOf("clang++", "-std=${config.std}", "-fsyntax-only") + config.clangArgs +
            listOf("-I", includeDir.path, "-Xclang", "-ast-dump=json", "-Xclang", "-ast-dump-filter=$filter", unit.path)
        val (exit, errors) = clang(command, workDir, dump)
        check(exit == 0) { "clang can't read ${config.name}'s headers:\n$errors" }
        // Documents close with a bare "}"; split there so the multi-MB namespaces in [skip] are never parsed.
        val json = JsonSlurper()
        val doc = StringBuilder()
        dump.forEachLine { line ->
            doc.appendLine(line)
            if (line != "}") return@forEachLine
            val text = doc.toString().also { doc.clear() }
            if (TOP_LEVEL_NAME.find(text)?.groupValues?.get(1) !in skip) action(json.parseText(text) as Map<*, *>)
        }
        dump.delete()
    }

    private companion object {
        val TOP_LEVEL_NAME = Regex("""^ {2}"name": "([^"]*)"""", RegexOption.MULTILINE)
    }
}
