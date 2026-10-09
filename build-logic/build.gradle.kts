plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.compose.compiler.gradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.vanniktech.publish.gradlePlugin)
    implementation(libs.compose.gradlePlugin)
    implementation(libs.dokka.gradlePlugin)
    implementation(libs.kover.gradlePlugin)
    // tar.gz extraction for the Filament prebuilt/header download tasks
    implementation(libs.commons.compress)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    inputs.dir("src/test/fixture")
    providers.gradleProperty("apigen.update").orNull?.let { systemProperty("apigen.update", it) }
}
