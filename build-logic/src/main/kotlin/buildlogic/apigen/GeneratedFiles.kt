package buildlogic.apigen

import java.io.File

/** A stage's output: text by path under [root], replacing whatever is under [dirs]. */
internal class GeneratedFiles(private val root: File, private val dirs: List<File>, val files: Map<String, String>) {
    fun write() {
        dirs.forEach { it.deleteRecursively() }
        files.forEach { (path, text) -> root.resolve(path).apply { parentFile.mkdirs() }.writeText(text) }
    }

    /** The paths under [root] where the disk isn't [files]: changed, missing or left over. */
    fun stale(): Set<String> {
        val onDisk = dirs.flatMap { dir -> dir.walkTopDown().filter { it.isFile }.map { it.relativeTo(root).invariantSeparatorsPath } }
        return files.filter { (path, text) -> root.resolve(path).let { !it.isFile || it.readText() != text } }.keys + (onDisk - files.keys)
    }
}
