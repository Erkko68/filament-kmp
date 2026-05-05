plugins {
    `java-library`
}

apply(from = rootProject.file("java-cmake.gradle.kts"))

dependencies {
    implementation(project(":filament"))
    compileOnly(libs.annotations)
}
