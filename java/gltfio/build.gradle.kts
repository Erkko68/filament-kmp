plugins {
    `java-library`
}

apply(from = rootProject.file("java/java-cmake.gradle.kts"))

dependencies {
    implementation(project(":java:filament"))
    compileOnly(libs.annotations)
}
