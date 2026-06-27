package io.github.erkko68.filament.compose.testutils

/**
 * Platform graphics-readiness handle: actualized to `Unit` on JVM/iOS (so a `@BeforeTest` returning
 * it stays JUnit-`void`) and to a thenable `Promise` on JS (which kotlin.test awaits).
 */
expect class GraphicsReady

/**
 * Blocks the compose UI-test harness until the platform's Skia/graphics runtime is ready.
 *
 * On JVM/iOS skiko is loaded synchronously, so this is a no-op. On Kotlin/JS skiko's WASM module
 * loads **asynchronously**, but `runComposeUiTest` creates its Skia raster surface synchronously in
 * its constructor — calling it before the WASM finishes throws
 * `org_jetbrains_skia_Surface__1nMakeRasterN32Premul is not defined`. The JS actual returns skiko's
 * readiness promise so kotlin.test waits for it before running the test body.
 */
expect fun awaitGraphicsReady(): GraphicsReady
