plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

// The raw engine in a plain AWT window: no Compose, only the `filament` artifact.
kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.filament)
        }
    }
}

// ./gradlew :awtApp:run [--args=opengl|vulkan|metal]
tasks.register<JavaExec>("run") {
    group = "application"
    val main = kotlin.jvm().compilations["main"]
    classpath = files(main.output.allOutputs, main.runtimeDependencyFiles)
    mainClass.set("eric.bitria.samples.awt.MainKt")
    // Silence JDK 24+'s "restricted method java.lang.System::load" warning from Filament's native loader.
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
