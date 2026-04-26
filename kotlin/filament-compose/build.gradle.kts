import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish") version "0.30.0"
}

val filaVersion: String by project
val libVersion: String by project
val projectGroup: String by project

group = projectGroup
version = "${filaVersion}-${libVersion}"

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

kotlin {
    androidTarget() {
        publishLibraryVariants("release", "debug")
    }

    val xcf = XCFramework("FilamentCompose")
    iosArm64 {
        binaries.framework {
            baseName = "FilamentCompose"
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "FilamentCompose"
            isStatic = true
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "FilamentCompose"
            isStatic = true
            xcf.add(this)
        }
    }

    macosArm64 {
        binaries.framework {
            baseName = "FilamentCompose"
            isStatic = true
            xcf.add(this)
        }
    }

    jvm()

    js {
        browser {
            binaries.executable()
        }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":kotlin:filament"))
                api(project(":kotlin:filament-utils"))
                api(project(":kotlin:gltfio"))
                api(project(":kotlin:filamat"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.IOS) {
            binaries.all {
                freeCompilerArgs += listOf("-Xoverride-konan-properties=apple.sdk.min.version=15.0")
            }
        }
    }
}

android {
    namespace = "io.github.erkko68.filament.compose"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

mavenPublishing {
    coordinates(projectGroup, "filament-compose", "${filaVersion}-${libVersion}")

    pom {
        name.set("Filament Compose KMP")
        description.set("Compose Multiplatform UI layer for the Filament KMP rendering engine")
        url.set(project.property("maven.url").toString())
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
            developerConnection.set(project.property("maven.scm").toString())
            url.set(project.property("maven.url").toString())
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}
