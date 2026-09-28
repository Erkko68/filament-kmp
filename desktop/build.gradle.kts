import buildlogic.cmake.registerCApiBuild
import buildlogic.platform.FilamentTarget
import org.gradle.nativeplatform.MachineArchitecture
import org.gradle.nativeplatform.OperatingSystemFamily

plugins {
    `java-library`
    id("filament-publish")
}

// Desktop runtime for :jni: builds libfilament-c (c/ + jni/, the JNI image) for the host and ships
// FilamentLoader, which extracts it from a runtime jar and loads it. Each platform's library is published
// in its own :desktop:runtime-<os>-<arch> jar; this module's metadata picks them (see the variants below).

val jvmRelease = libs.versions.jvm.target.get().toInt()
java {
    sourceCompatibility = JavaVersion.toVersion(jvmRelease)
    targetCompatibility = JavaVersion.toVersion(jvmRelease)
}
tasks.withType<JavaCompile>().configureEach { options.release.set(jvmRelease) }

dependencies {
    api(project(":jni"))
    testImplementation(libs.junit)
}
tasks.named<Test>("test") { useJUnit() }

val cmakeBuild = registerCApiBuild("cmakeBuild", FilamentTarget.host()) {
    // jni.h from the JDK running the build.
    arguments.add("-DJNI_HOME=${System.getProperty("java.home").replace('\\', '/')}")
    targets.add("filament-c-jni")
}

// The host's libfilament-c, for its runtime-<os>-<arch> jar.
configurations.consumable("hostNatives")
artifacts.add("hostNatives", cmakeBuild.flatMap { it.outputDir }) { builtBy(cmakeBuild) }

// ── Runtime selection (published on this module's metadata) ──────────────────────────────────
// Plain consumers (no attributes, or Maven) get every platform's runtime jar: zero-config. Gradle consumers
// declaring OperatingSystemFamily + MachineArchitecture match a per-platform variant instead and download
// one platform's library.

// Kept out of implementation/runtimeOnly so the per-platform variants (which extend those) don't inherit it.
val allRuntimes = configurations.dependencyScope("allRuntimes")
configurations.named("runtimeElements") { extendsFrom(allRuntimes.get()) }
dependencies {
    FilamentTarget.desktop.forEach { allRuntimes(project(":desktop:runtime-${it.id}")) }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
FilamentTarget.desktop.forEach { target ->
    val runtime = configurations.dependencyScope("runtime-${target.id}Dependencies")
    dependencies.add(runtime.name, project(":desktop:runtime-${target.id}"))
    val variant = configurations.consumable("runtime-${target.id}") {
        extendsFrom(configurations["implementation"], configurations["runtimeOnly"], runtime.get())
        attributes {
            attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named(target.osFamily))
            attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named(target.machineArchitecture))
        }
    }
    artifacts.add(variant.name, tasks.named("jar"))
    javaComponent.addVariantsFromConfiguration(variant.get()) { mapToMavenScope("runtime") }
}

// A variant's attributes must be runtimeElements' full set plus os/arch: plain consumers then settle on
// runtimeElements (the strict subset), attributed ones on the platform variant. Copied rather than listed,
// since plugins keep adding attributes to runtimeElements.
afterEvaluate {
    val source = configurations["runtimeElements"].attributes
    FilamentTarget.desktop.forEach { target ->
        configurations["runtime-${target.id}"].attributes {
            source.keySet().forEach { key ->
                @Suppress("UNCHECKED_CAST")
                val k = key as Attribute<Any>
                if (getAttribute(k) == null) attribute(k, source.getAttribute(k)!!)
            }
        }
    }
}

// Each runtime reaches the POM twice (allRuntimes and its variant's scope mapping): dedupe by artifactId.
publishing {
    publications.withType<MavenPublication>().configureEach {
        pom.withXml {
            val deps = asElement().getElementsByTagName("dependency")
            val seen = mutableSetOf<String>()
            (0 until deps.length).map { deps.item(it) }.filter { node ->
                val children = node.childNodes
                val artifactId = (0 until children.length).map { children.item(it) }
                    .firstOrNull { it.nodeName == "artifactId" }?.textContent ?: return@filter false
                !seen.add(artifactId)
            }.forEach { it.parentNode.removeChild(it) }
        }
    }
}
