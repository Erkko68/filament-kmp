package buildlogic.apigen.cpp

import buildlogic.apigen.relativeHeaders
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import javax.inject.Inject

/** Writes the [CppApi] surface to [report], then the types it couldn't resolve and the defaults it couldn't fold. */
@DisableCachingByDefault(because = "A local report")
abstract class ApiModelTask @Inject constructor(private val exec: ExecOperations) : DefaultTask() {
    @get:InputFiles @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val publicHeaders: ConfigurableFileCollection

    @get:Internal abstract val includeDir: DirectoryProperty

    @get:OutputFile abstract val report: RegularFileProperty

    @TaskAction
    fun run() {
        val include = includeDir.get().asFile
        val headers = relativeHeaders(publicHeaders.files, include)
        val api = CppApiReader(exec, temporaryDir).read(include, headers)
        val surface = api.surface(headers)
        val unresolved = sortedSetOf<String>()
        val unsupported = sortedSetOf<String>()
        fun type(t: CppType, where: String) = t.also { if (it.kind == CppType.Kind.UNRESOLVED) unresolved += "${it.spelling}  in $where" }
        fun value(v: CppValue?, where: String) = v.also { if (it is CppValue.Unsupported) unsupported += "$it  in $where" }

        val text = StringBuilder()
        for (name in surface) {
            api.enums[name]?.let { e ->
                text.appendLine("enum $name${e.underlying?.let { " : $it" } ?: ""} { ${e.constants.joinToString { (n, v) -> "$n = $v" }} }")
            }
            api.aliases[name]?.let { text.appendLine("alias $name = ${type(it, name)}") }
            val record = api.records[name] ?: continue
            text.appendLine("${if (record.exported) "class" else "struct"} $name")
            record.fields.filter { it.isPublic }.forEach { f ->
                text.appendLine("  field ${type(f.type, "$name.${f.name}")} ${f.name}" + (value(f.default, "$name.${f.name}")?.let { " = $it" } ?: ""))
            }
            record.methods.filter { it.isPublic && it.isApi }.forEach { m ->
                val where = "$name::${m.name}"
                type(m.returns, where)
                m.params.forEach { type(it.type, where); value(it.default, where) }
                text.appendLine("  $m")
            }
        }
        api.apiFunctions(headers).forEach { f ->
            f.params.forEach { type(it.type, "${f.owner}::${f.name}"); value(it.default, "${f.owner}::${f.name}") }
            text.appendLine("function $f")
        }
        text.appendLine("\n## Unresolved types").append(unresolved.joinToString("") { "$it\n" })
        text.appendLine("\n## Unsupported defaults").append(unsupported.joinToString("") { "$it\n" })
        val file = report.get().asFile
        file.writeText(text.toString())
        logger.lifecycle("${surface.size} declarations in the API surface, ${unresolved.size} unresolved types, ${unsupported.size} unsupported defaults")
        logger.lifecycle("Report: ${file.toURI()}")
    }
}
