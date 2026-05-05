plugins {
    kotlin("jvm") version "2.3.21" apply false
}

allprojects {
    val base = findProperty("projectGroup") as? String ?: "io.github.erkko68.filament"
    group   = "$base.java"
    version = findProperty("libVersion") as? String ?: "0.1.0-SNAPSHOT"
}
