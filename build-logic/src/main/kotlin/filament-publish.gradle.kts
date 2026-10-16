import buildlogic.jvm.BytecodeLevelTask
import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    // Artifact id defaults to the project name; a module can override it via the
    // `maven.artifactId` project property (set in its own gradle.properties). This must be
    // applied before configure() below, which finalizes the coordinates.
    val artifactId = (project.findProperty("maven.artifactId") as String?) ?: project.name
    coordinates(project.group.toString(), artifactId, project.version.toString())

    // Ship an empty -javadoc.jar for every module. API docs are hosted on GitHub Pages
    // (Dokka), and Maven Central only requires the artifact to exist — not have content. This
    // avoids bundling duplicated Dokka HTML into the jars: ~80MB across the KMP targets. Both branches must opt in explicitly — the vanniktech default is
    // JavadocJar.Dokka whenever the Dokka plugin is present.
    when {
        pluginManager.hasPlugin("com.android.library") ->
            configure(AndroidSingleVariantLibrary(JavadocJar.Empty(), SourcesJar.Sources(), "release"))
        pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform") ->
            configure(KotlinMultiplatform(javadocJar = JavadocJar.Empty()))
        pluginManager.hasPlugin("org.jetbrains.kotlin.jvm") ->
            configure(KotlinJvm(javadocJar = JavadocJar.Empty(), sourcesJar = SourcesJar.Sources()))
        // Plain jars (:desktop and its runtime-<os>-<arch> jars).
        pluginManager.hasPlugin("java-library") ->
            configure(JavaLibrary(javadocJar = JavadocJar.Empty(), sourcesJar = SourcesJar.Sources()))
    }

    pom {
        name.set(project.property("maven.name").toString())
        description.set(project.property("maven.description").toString())
        url.set(project.property("maven.url").toString())
        inceptionYear.set(project.property("maven.inceptionYear").toString())
        licenses {
            license {
                name.set(project.property("maven.license.name").toString())
                url.set(project.property("maven.license.url").toString())
            }
        }
        developers {
            developer {
                id.set(project.property("maven.developer.id").toString())
                name.set(project.property("maven.developer.name").toString())
                email.set(project.property("maven.developer.email").toString())
            }
        }
        scm {
            connection.set(project.property("maven.scm").toString())
            developerConnection.set(project.property("maven.scm.dev").toString())
            url.set(project.property("maven.url").toString())
        }
        issueManagement {
            system.set("GitHub")
            url.set(project.property("maven.url").toString() + "/issues")
        }
    }

    publishToMavenCentral()
    if (project.hasProperty("signingInMemoryKeyId")) {
        signAllPublications()
    }
}

// A published JVM jar and everything it pulls at runtime must load on the floor we declare
// (a dependency shipping newer bytecode would silently raise consumers' required JDK).
val checkJvmBytecode = tasks.register<BytecodeLevelTask>("checkJvmBytecode") {
    report.set(layout.buildDirectory.file("reports/jvmBytecode.txt"))
}
pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    val kotlin = the<KotlinMultiplatformExtension>()
    checkJvmBytecode.configure {
        val jvm = kotlin.targets.withType<KotlinJvmTarget>().singleOrNull()
        enabled = jvm != null // :android has no jvm() target
        if (jvm != null) {
            classpath.from(configurations.named("jvmRuntimeClasspath"), tasks.named("jvmJar"))
            release.set(jvm.compilerOptions.jvmTarget.map { it.target.toInt() })
        }
    }
}
pluginManager.withPlugin("java") {
    val java = the<JavaPluginExtension>()
    checkJvmBytecode.configure {
        classpath.from(configurations.named("runtimeClasspath"), tasks.named("jar"))
        release.set(provider { java.targetCompatibility.majorVersion.toInt() })
    }
}
tasks.matching { it.name == "check" }.configureEach { dependsOn(checkJvmBytecode) }
tasks.withType<AbstractPublishToMaven>().configureEach { dependsOn(checkJvmBytecode) }
