// Karma configuration fragment for Filament WASM tests.
//
// Prepends filament-kmp.js (the Emscripten loader, built by :wasm), filament-kmp.wasm (served as a
// static asset), and the bootstrap that waits for the module before Karma starts the suite. They
// are staged into the test resources by stageFilamentWebAssetsForJsTest (filament-kmp-module).

// Karma's basePath is the test package root; the staged WASM loader, binary,
// and bootstrap end up under `kotlin/` (the JS package output dir).
config.files = [
    // WASM loader — must run before any Kotlin test code.
    { pattern: 'kotlin/filament-kmp.js', watched: false, included: true, served: true, nocache: true },
    // WASM binary — served only; filament-kmp.js fetches it next to itself.
    // The `/base/` prefix is karma's served path for files in basePath.
    { pattern: 'kotlin/filament-kmp.wasm', watched: false, included: false, served: true, nocache: true },
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

// Filament's WebGL init + first frame under software rendering (SwiftShader on CI)
// is far slower than on a real GPU, so scene tests that await graphics readiness can
// exceed Mocha's 2s default. Raise the in-browser async timeout (js finishes well under
// it on a real runtime; this only rescues the slow CI path). Applies to js + wasm alike.
config.client = config.client || {};
config.client.mocha = Object.assign({}, config.client.mocha, { timeout: 30000 });

// The Mocha timeout above only bounds an individual test's async wait — it does
// nothing during the pre-test bootstrap window, where the browser has loaded but
// hasn't sent Karma a single message yet. On wasmJs that window is heavy:
// instantiating the (large) Kotlin/Wasm test binary + skiko's wasm + Filament.init,
// all before __karma__.loaded() releases the suite. Under CI's software renderer
// that can exceed Karma's 30s browserNoActivityTimeout, which then kills the browser
// with "Disconnected, because no message in 30000 ms" and 0 tests run. (js is far
// lighter and stays under the default, so it never tripped this.) Raise the Karma
// transport-level timeouts so slow wasm init on CI isn't mistaken for a hung browser.
config.browserNoActivityTimeout = 5 * 60 * 1000; // 5 min: covers wasm+skiko+Filament init
config.browserDisconnectTimeout = 60 * 1000;
config.browserDisconnectTolerance = 2;
config.captureTimeout = 5 * 60 * 1000;
config.pingTimeout = 60 * 1000;
