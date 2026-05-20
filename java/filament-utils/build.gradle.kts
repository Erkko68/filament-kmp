import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    id("filament-publish")
}

// Native code lives in the combined libfilament-jni built by :java:filament.
// This module is pure-Kotlin/Java glue; the dylib reaches it transitively at runtime.

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":java:filament"))
    implementation(libs.kotlin.stdlib)
    compileOnly(libs.annotations)
}
