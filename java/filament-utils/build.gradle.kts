import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    kotlin("jvm")
    id("filament-publish")
}

apply(from = rootProject.file("java/java-cmake.gradle.kts"))

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":java:filament"))
    implementation(libs.kotlin.stdlib)
}
