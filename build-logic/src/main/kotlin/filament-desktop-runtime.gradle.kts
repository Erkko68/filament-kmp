// A desktop runtime module (:desktop:runtime-<os>-<arch>): a resources-only jar with that platform's
// libfilament-c under natives/<os>-<arch>/ plus a .sha256 (FilamentLoader's cache key), published as
// filament-jni-runtime-<os>-<arch>. The host's library comes from :desktop's CMake build; the others from
// -PcArtifactsDir=<dir> (<dir>/<os>-<arch>/), where the CI publish job gathers every platform's.

import buildlogic.platform.FilamentTarget
import java.security.MessageDigest
import java.util.zip.ZipFile

val target = FilamentTarget.byId(project.name.removePrefix("runtime-"))
// Read by filament-publish, so set before applying it.
extra["maven.artifactId"] = "filament-jni-runtime-${target.id}"
extra["maven.description"] = "Filament KMP desktop runtime: libfilament-c for ${target.id}."
apply(plugin = "java-library")
apply(plugin = "filament-publish")

extensions.configure<JavaPluginExtension> {
    // Resources only, but the jvm-version attribute still has to accept the library's floor.
    val release = JavaVersion.toVersion(the<VersionCatalogsExtension>().named("libs").findVersion("jvm-target").get().requiredVersion)
    sourceCompatibility = release
    targetCompatibility = release
}

val hostNativesDependencies = configurations.dependencyScope("hostNativesDependencies")
val hostNatives = configurations.resolvable("hostNatives") { extendsFrom(hostNativesDependencies.get()) }
dependencies {
    // Standalone: this jar alone pulls the loader, minus the sibling platforms :desktop defaults to.
    // Excluded under both the local project names and the published artifact ids.
    "api"(project(":desktop")) {
        FilamentTarget.desktop.filter { it != target }.forEach { other ->
            exclude(group = project.group.toString(), module = "runtime-${other.id}")
            exclude(group = project.group.toString(), module = "filament-jni-runtime-${other.id}")
        }
    }
    if (target == FilamentTarget.host()) add(hostNativesDependencies.name, project(":desktop", "hostNatives"))
}

val cArtifacts = providers.gradleProperty("cArtifactsDir").orNull?.let { rootProject.file(it).resolve(target.id) }
val stageNatives = tasks.register<Sync>("stageNatives") {
    into(layout.buildDirectory.dir("natives"))
    // The publish host has its own library twice (local build + cArtifactsDir): the local one wins.
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(listOfNotNull(hostNatives, cArtifacts)) {
        include("*.dylib", "*.so", "*.dll")
        into("natives/${target.id}")
    }
    doLast {
        destinationDir.walkTopDown().filter { it.isFile && it.extension in setOf("dylib", "so", "dll") }.forEach { lib ->
            val digest = MessageDigest.getInstance("SHA-256").digest(lib.readBytes())
            File(lib.path + ".sha256").writeText(digest.joinToString("") { "%02x".format(it) })
        }
    }
}
tasks.named<Jar>("jar") { from(stageNatives) }

// Off-host platforms stage nothing without -PcArtifactsDir: fine for a local build, broken on Maven Central.
val jarFile = tasks.named<Jar>("jar").flatMap { it.archiveFile }
tasks.withType<AbstractPublishToMaven>().configureEach {
    val archive = jarFile
    val id = target.id
    doFirst {
        ZipFile(archive.get().asFile).use { zip ->
            check(zip.entries().asSequence().any { it.name.startsWith("natives/$id/") && !it.isDirectory }) {
                "Refusing to publish an empty $id runtime jar: its libfilament-c was not staged (pass -PcArtifactsDir)."
            }
        }
    }
}
