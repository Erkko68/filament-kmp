plugins {
    `java-library`
}

apply(from = rootProject.file("java-cmake.gradle.kts"))

dependencies {
    compileOnly(libs.annotations)
}
