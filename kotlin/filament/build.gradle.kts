import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish") version "0.30.0"
}

val filaVersion: String by project
val libVersion: String by project
val projectGroup: String by project

group = projectGroup
version = "${filaVersion}-${libVersion}"

val filamentAndroidVersion = filaVersion

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
    iosArm64 {
        binaries.framework {
            baseName = "Filament"
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Filament"
            isStatic = true
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "Filament"
            isStatic = true
            xcf.add(this)
        }
    }

    macosArm64 {
        binaries.framework {
            baseName = "Filament"
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
        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by getting

        val androidMain by getting {
            dependencies {
                implementation("com.google.android.filament:filament-android:$filamentAndroidVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project(":java:filament"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(project(":js"))
            }
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
                    project.file("../../include")
                )
            }
        }
    }

    // --- Automated Native Build & C-Interop Embedding Configuration ---
    targets.withType<KotlinNativeTarget>().configureEach {
        val targetName = name
        val konanTarget = konanTarget

        if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.IOS) {
            binaries.all {
                freeCompilerArgs += listOf("-Xoverride-konan-properties=apple.sdk.min.version=15.0")
            }
        }

        // 1. Setup paths
        val (filaPlatform, filaArch) = when (konanTarget) {
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64 -> "ios" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_SIMULATOR_ARM64 -> "ios-simulator" to "arm64"
            org.jetbrains.kotlin.konan.target.KonanTarget.IOS_X64 -> "ios-simulator" to "x64"
            org.jetbrains.kotlin.konan.target.KonanTarget.MACOS_ARM64 -> "macos" to "arm64"
            else -> "" to ""
        }
        val libPrefix = "lib"
        val libSuffix = ".a"
        val buildDir = project.file("../../c/build/$targetName")
        val filamentPrebuiltDir = "${projectDir}/../../prebuilts/$targetName/lib"


        // 2. Automate the C Wrapper compilation
        if (filaPlatform.isNotEmpty()) {
            val cmakeTaskName = "buildFilamentC_$targetName"
            val buildFilamentC = tasks.register<Exec>(cmakeTaskName) {
                buildDir.mkdirs()
                workingDir(buildDir)
                val cmakePath = "/opt/homebrew/bin/cmake"
                commandLine("sh", "-c", "$cmakePath ../../ -DFILAMENT_PLATFORM=$filaPlatform -DFILAMENT_ARCH=$filaArch -DCMAKE_BUILD_TYPE=Release && $cmakePath --build . --target filament-c")
                doFirst { println("Building Filament C Wrapper for $targetName ($filaPlatform/$filaArch) in $buildDir") }
            }

            // Ensure Kotlin Compilation waits for C build
            compilations.getByName("main").compileTaskProvider.configure {
                dependsOn(buildFilamentC)
            }

            // CRITICAL: Ensure cinterop waits for the C build, because it needs to pack libfilament-c.a!
            project.tasks.matching { it.name == "cinteropFilament${targetName.replaceFirstChar { c -> c.uppercase() }}" }.configureEach {
                dependsOn(buildFilamentC)
            }
        }

        // 3. Configure cinterop to PACK the static libraries into the .klib
        compilations.getByName("main").cinterops {
            // Update the existing filament cinterop (assumes you ran create("filament") earlier)
            getByName("filament") {
                if (filamentPrebuiltDir.isNotEmpty()) {
                    // Tell cinterop where to find the static libraries
                    extraOpts("-libraryPath", filamentPrebuiltDir)
                    extraOpts("-libraryPath", buildDir.absolutePath)

                    // Find all static libraries in the prebuilts folder
                    val prebuiltLibs = if (file(filamentPrebuiltDir).exists()) {
                        fileTree(filamentPrebuiltDir) {
                            include("*.a", "*.lib")
                        }.map { it.name }
                    } else {
                        emptyList<String>()
                    }
                    
                    if (prebuiltLibs.isEmpty() && filaPlatform.isNotEmpty()) {
                        val errorMsg = """
                            |
                            |  MISSING DEPENDENCIES for $targetName:
                            |  Filament prebuilt libraries were not found in: $filamentPrebuiltDir
                            |  Please build Filament for $filaPlatform ($filaArch) and copy the $libSuffix files to that directory.
                            |
                        """.trimMargin()
                        logger.error(errorMsg)
                    }
                    
                    val staticLibs = prebuiltLibs + "${libPrefix}filament-c${libSuffix}"

                    // Instruct cinterop to physically embed each library
                    staticLibs.forEach { lib ->
                        extraOpts("-staticLibrary", lib)
                    }
                }
            }
        }
    }
}

android {
    namespace = "io.github.erkko68.filament.filament"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}

mavenPublishing {
    coordinates(projectGroup, "filament", "${filaVersion}-${libVersion}")

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
