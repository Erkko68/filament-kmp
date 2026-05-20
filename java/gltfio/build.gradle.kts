plugins {
    `java-library`
    id("filament-publish")
}

// Native code lives in the combined libfilament-jni built by :java:filament.
// This module is pure-Java glue; the dylib reaches it transitively at runtime.

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":java:filament"))
    compileOnly(libs.annotations)
}
