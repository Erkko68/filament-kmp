plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Needed to access KotlinNativeTarget and KonanTarget in convention helpers
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.10")
}
