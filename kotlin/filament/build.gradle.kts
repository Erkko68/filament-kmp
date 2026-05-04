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

// Libraries that filament-c links against from the prebuilts directory.
// These are the direct dependencies of the C wrapper plus known transitive
// dependencies of libfilament.a that the linker must resolve.
val FILAMENT_PREBUILT_LIBS = listOf(
    "libfilament.a",
    "libbackend.a",
    "libutils.a",
    "libgeometry.a",
    "libibl-lite.a",       // CMakeLists: ibl-lite (NOT libibl.a)
    "libfilaflat.a",
    "libfilabridge.a",
    "libsmol-v.a",
    "libbluegl.a",
    "libbluevk.a",
    "libzstd.a",
    "libmeshoptimizer.a",  // transitive dep of libfilament.a
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

    val xcf = XCFramework("Filament")
    listOf(iosArm64(), iosSimulatorArm64(), iosX64(), macosArm64()).forEach {
        it.binaries.framework {
            baseName = "Filament"
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
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation("com.google.android.filament:filament-android:$filaVersion")
        }
        jvmMain.dependencies {
            api(project(":java:filament"))
        }
        jsMain.dependencies {
            implementation(project(":js"))
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament") {
                defFile(project.file("src/nativeInterop/cinterop/filament.def"))
                includeDirs(
                    project.file("../../c/filament/c"),
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament-utils/c"),
                    project.file("../../include"),
                )
            }
        }
        applyFilamentNative(project, "filament", "filament-c", FILAMENT_PREBUILT_LIBS)
    }
}

android {
    namespace  = "io.github.erkko68.filament.filament"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
}

mavenPublishing {
    coordinates(projectGroup, "filament", libVersion)

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
