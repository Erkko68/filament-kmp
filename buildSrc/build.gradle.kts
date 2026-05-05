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
}
