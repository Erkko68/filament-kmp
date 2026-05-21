plugins {
    kotlin("multiplatform")
    id("filament-publish")
}

version = findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"

kotlin {
    js {
        browser()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
    }
}
