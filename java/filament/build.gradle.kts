plugins {
    `java-library`
    id("filament-publish")
}

apply(from = rootProject.file("java/java-cmake.gradle.kts"))

dependencies {
    compileOnly(libs.annotations)
}
