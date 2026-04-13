plugins {
    kotlin("jvm") version "2.1.20" apply false
    kotlin("multiplatform") version "2.1.20" apply false
    id("com.android.library") version "8.13.2" apply false
    id("com.android.application") version "8.13.2" apply false
}

allprojects {
    group = project.findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    version = project.findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"
}
