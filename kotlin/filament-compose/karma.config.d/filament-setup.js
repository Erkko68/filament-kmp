// Karma configuration fragment for Filament WASM tests.
//
// Prepends filament.js (the Emscripten WASM loader), filament.wasm (served as
// a static asset), and the bootstrap script that waits for async WASM init
// before letting Karma start the test suite.
//
// filament.js and filament.wasm are staged from prebuilts/web/ into the jsTest
// processedResources directory by the stageFilamentWebAssetsForJsTest Gradle
// task in kotlin/filament/build.gradle.kts (wired in as an extra jsTest
// resources srcDir).

// Karma's basePath is the test package root; the staged WASM loader, binary,
// and bootstrap end up under `kotlin/` (the JS package output dir).
config.files = [
    // WASM loader — must run before any Kotlin test code.
    { pattern: 'kotlin/filament.js', watched: false, included: true, served: true, nocache: true },
    // WASM binary — served only; filament.js fetches it via XHR/fetch.
    // The `/base/` prefix is karma's served path for files in basePath.
    { pattern: 'kotlin/filament.wasm', watched: false, included: false, served: true, nocache: true },
    // Bootstrap: delays __karma__.loaded until Filament is ready.
    { pattern: 'kotlin/filament-karma-bootstrap.js', watched: false, included: true, served: true, nocache: true },
].concat(config.files || []);

// Use a ChromeHeadless variant that enables software WebGL so the Filament
// WebGL backend can create a valid GL context without a physical GPU.
config.browsers = ['ChromeHeadlessWebGL'];
config.customLaunchers = Object.assign(config.customLaunchers || {}, {
    ChromeHeadlessWebGL: {
        base: 'ChromeHeadless',
        flags: [
            '--use-angle=swiftshader',
            '--enable-webgl',
            '--ignore-gpu-blocklist',
            '--disable-gpu-sandbox',
        ],
    },
});
