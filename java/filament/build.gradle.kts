plugins {
    `java-library`
}

apply(from = rootProject.file("java/java-cmake.gradle.kts"))

dependencies {
    compileOnly(libs.annotations)
}
