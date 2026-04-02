import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "dev.filament.kmp"
version = "0.1.0-SNAPSHOT"

kotlin {
    androidTarget()

    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()
    mingwX64()

    js(IR) {
        browser()
        nodejs()
        binaries.library()
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }

        val iosMain by creating {
            dependsOn(nativeMain)
        }
        val iosTest by creating {
            dependsOn(nativeTest)
        }

        val macosMain by creating {
            dependsOn(nativeMain)
        }
        val macosTest by creating {
            dependsOn(nativeTest)
        }

        val linuxMain by creating {
            dependsOn(nativeMain)
        }
        val linuxTest by creating {
            dependsOn(nativeTest)
        }

        val windowsMain by creating {
            dependsOn(nativeMain)
        }
        val windowsTest by creating {
            dependsOn(nativeTest)
        }

        val webMain by creating {
            dependsOn(commonMain)
        }
        val webTest by creating {
            dependsOn(commonTest)
        }

        val androidMain by getting
        val androidUnitTest by getting

        val jsMain by getting {
            dependsOn(webMain)
        }
        val jsTest by getting {
            dependsOn(webTest)
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }
        val linuxX64Main by getting {
            dependsOn(linuxMain)
        }
        val linuxArm64Main by getting {
            dependsOn(linuxMain)
        }
        val mingwX64Main by getting {
            dependsOn(windowsMain)
        }

        val iosArm64Test by getting {
            dependsOn(iosTest)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
        val macosArm64Test by getting {
            dependsOn(macosTest)
        }
        val linuxX64Test by getting {
            dependsOn(linuxTest)
        }
        val linuxArm64Test by getting {
            dependsOn(linuxTest)
        }
        val mingwX64Test by getting {
            dependsOn(windowsTest)
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.getByName("main").cinterops {
            create("filament") {
                defFile(project.file("src/nativeInterop/cinterop/filament.def"))
                includeDirs(
                    project.file("../../c-wrapper/include"),
                    project.file("../../filament-prebuilts/include")
                )
            }
        }
    }
}

android {
    namespace = "dev.filament.kmp"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
}

