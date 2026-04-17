import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.konan.target.Family

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

    val xcf = XCFramework("Filamat")
    iosArm64 {
        binaries.framework {
            baseName = "Filamat"
            isStatic = true
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "Filamat"
            isStatic = true
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "Filamat"
            isStatic = true
            xcf.add(this)
        }
    }

    macosArm64 {
        binaries.framework {
            baseName = "Filamat"
            isStatic = true
            xcf.add(this)
        }
    }

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
            }
        }
        val nativeMain by getting
        val androidMain by getting {
            dependencies {
                implementation("com.google.android.filament:filamat-android:$filamentAndroidVersion")
            }
        }

        val jsMain by getting
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filamat") {
                defFile(project.file("src/nativeInterop/cinterop/filamat.def"))
                includeDirs(
                    project.file("../../c/filamat/c"),
                    project.file("../../c/filament/c"),
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
            val cmakeTaskName = "buildFilamatC_$targetName"
            val buildFilamatC = tasks.register<Exec>(cmakeTaskName) {
                buildDir.mkdirs()
                workingDir(buildDir)
                val cmakePath = "/opt/homebrew/bin/cmake"
                commandLine("sh", "-c", "$cmakePath ../../ -DFILAMENT_PLATFORM=$filaPlatform -DFILAMENT_ARCH=$filaArch -DCMAKE_BUILD_TYPE=Release && $cmakePath --build . --target filamat-c")
                doFirst { println("Building Filamat C Wrapper for $targetName ($filaPlatform/$filaArch) in $buildDir") }
            }

            compilations.getByName("main").compileTaskProvider.configure {
                dependsOn(buildFilamatC)
            }

            project.tasks.matching { it.name == "cinteropFilamat${targetName.replaceFirstChar { c -> c.uppercase() }}" }.configureEach {
                dependsOn(buildFilamatC)
            }
        }

        // 3. Configure cinterop to PACK the static libraries into the .klib
        compilations.getByName("main").cinterops {
            getByName("filamat") {
                if (filamentPrebuiltDir.isNotEmpty()) {
                    extraOpts("-libraryPath", filamentPrebuiltDir)
                    extraOpts("-libraryPath", buildDir.absolutePath)

                    val prebuiltLibs = if (file(filamentPrebuiltDir).exists()) {
                        fileTree(filamentPrebuiltDir) {
                            include("*.a", "*.lib")
                        }.map { it.name }
                    } else {
                        emptyList<String>()
                    }

                    val staticLibs = prebuiltLibs + "${libPrefix}filamat-c${libSuffix}"

                    staticLibs.forEach { lib ->
                        extraOpts("-staticLibrary", lib)
                    }
                }
            }
        }
    }
}

android {
    namespace = "io.github.erkko68.filament.filament.filamat"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
}

mavenPublishing {
    coordinates(projectGroup, "filamat", "${filaVersion}-${libVersion}")

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
