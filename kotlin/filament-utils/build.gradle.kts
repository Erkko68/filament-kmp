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

// Additional prebuilts needed by filament-utils-c beyond what :kotlin:filament already embeds.
// (zstd, utils, filaflat, filabridge are covered by the filament module.)
val FILAMENT_UTILS_PREBUILT_LIBS = listOf(
    "libfilament-iblprefilter.a",
    "libcamutils.a",
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

    val xcf = XCFramework("FilamentUtils")
    listOf(iosArm64(), iosSimulatorArm64(), iosX64(), macosArm64()).forEach {
        it.binaries.framework {
            baseName = "FilamentUtils"
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
            api(project(":kotlin:filament"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filament-utils-android:$filaVersion")
        }
        jvmMain.dependencies {
            api(project(":java:filament-utils"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament_utils") {
                defFile(project.file("src/nativeInterop/cinterop/filament-utils.def"))
                includeDirs(
                    project.file("../../c/filament-utils/c"),
                    project.file("../../c/filament/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "filament_utils", "filament-utils-c", FILAMENT_UTILS_PREBUILT_LIBS)
    }
}

android {
    namespace  = "io.github.erkko68.filament.filament.utils"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
}

mavenPublishing {
    coordinates(projectGroup, "filament-utils", libVersion)

    pom {
        name.set(project.property("maven.name").toString())
        description.set(project.property("maven.description").toString())
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
