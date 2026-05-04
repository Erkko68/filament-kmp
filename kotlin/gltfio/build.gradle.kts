import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish") version "0.30.0"
}

val filaVersion: String by project
val libVersion: String by project
val projectGroup: String by project

group   = projectGroup
version = libVersion

// Additional prebuilts needed by gltfio-c beyond what :kotlin:filament already embeds.
// (filament, backend, utils, filaflat, filabridge, zstd are covered by the filament module.)
val GLTFIO_PREBUILT_LIBS = listOf(
    "libgltfio_core.a",
    "libdracodec.a",
    "libbasis_transcoder.a",  // transitive dep of gltfio_core
    "libmikktspace.a",        // transitive dep of gltfio_core
    "libstb.a",
    "libimage.a",
    "libimageio-lite.a",
    "libktxreader.a",
)

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    androidTarget() {
        publishLibraryVariants("release", "debug")
    }

    val xcf = XCFramework("Gltfio")
    listOf(iosArm64(), iosSimulatorArm64(), iosX64(), macosArm64()).forEach {
        it.binaries.framework {
            baseName = "Gltfio"
            isStatic = true
            xcf.add(this)
        }
    }

    jvm()

    js {
        browser { binaries.executable() }
    }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kotlin:filament"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:gltfio-android:$filaVersion")
        }
        jvmMain.dependencies {
            implementation(project(":java:gltfio"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("gltfio") {
                defFile(project.file("src/nativeInterop/cinterop/gltfio.def"))
                includeDirs(
                    project.file("../../c/gltfio/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "gltfio", "gltfio-c", GLTFIO_PREBUILT_LIBS)
    }
}

android {
    namespace  = "io.github.erkko68.filament.gltfio"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
}

mavenPublishing {
    coordinates(projectGroup, "gltfio", libVersion)

    pom {
        name.set("Filament Gltfio KMP")
        description.set("Filament glTF loader and pipeline for Kotlin Multiplatform")
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
