package buildlogic.apigen.cpp

import groovy.json.JsonSlurper
import org.gradle.process.ExecOperations
import java.io.File

/** clang's JSON AST dump: one top-level document per declaration whose qualified name contains a filter. */
internal class ClangAstDump(private val exec: ExecOperations, private val workDir: File) {
    fun forEachDeclaration(unit: File, includeDir: File, filter: String, skip: Set<String>, action: (Map<*, *>) -> Unit) {
        val dump = workDir.resolve("ast.json")
        dump.outputStream().use { out ->
            exec.exec {
                commandLine(
                    "clang++", "-std=c++20", "-fsyntax-only", "-I", includeDir.path,
                    "-Xclang", "-ast-dump=json", "-Xclang", "-ast-dump-filter=$filter", unit.path,
                )
                standardOutput = out
            }
        }
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
