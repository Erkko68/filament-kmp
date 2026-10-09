package buildlogic.apigen

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

/** A task driving [ApiGen] for the library [config] describes, under [projectDir]. */
abstract class ApiGenTask : DefaultTask() {
    @get:Input abstract val config: Property<ApiGenConfig>
    @get:Internal abstract val projectDir: DirectoryProperty

    internal fun apiGen() = ApiGen(config.get(), projectDir.get().asFile, temporaryDir)
}
