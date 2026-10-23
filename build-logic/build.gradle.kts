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

    testImplementation(libs.kotlin.testJunit)
}

// The API generator's fixture test runs clang; -Papigen.update rewrites the fixture's expected output.
tasks.test {
    useJUnit()
    inputs.dir("src/test/fixture").withPathSensitivity(PathSensitivity.RELATIVE)
    systemProperty("apigen.update", providers.gradleProperty("apigen.update").isPresent)
}
