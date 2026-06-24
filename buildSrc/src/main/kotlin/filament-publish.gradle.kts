import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    // Ship an empty -javadoc.jar for the KMP modules. API docs are hosted on GitHub Pages
    // (Dokka), and Maven Central only requires the artifact to exist — not have content. This
    // also avoids generating + bundling ~80MB of duplicated Dokka HTML into every target's jar,
    // which is what the plugin does by default when Dokka is applied. The plain-JVM filament-ffm
    // module isn't multiplatform and keeps the plugin's own default (already an empty javadoc jar).
    if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        configure(KotlinMultiplatform(javadocJar = JavadocJar.Empty()))
    }

    coordinates(project.group.toString(), project.name, project.version.toString())

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
