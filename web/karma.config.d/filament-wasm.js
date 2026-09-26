// Loads filament-kmp.js (defines the global createFilamentModule) before the tests; the
// .wasm is only served, the loader fetches it next to the script.
config.files = [
    { pattern: 'kotlin/filament-kmp.js', watched: false, included: true, served: true, nocache: true },
    { pattern: 'kotlin/filament-kmp.wasm', watched: false, included: false, served: true, nocache: true },
].concat(config.files || []);

// Software WebGL so the context exists without a GPU (same launcher as kotlin/filament).
config.browsers = ['ChromeHeadlessWebGL'];
config.customLaunchers = Object.assign(config.customLaunchers || {}, {
    ChromeHeadlessWebGL: {
        base: 'ChromeHeadless',
        flags: ['--use-angle=swiftshader', '--enable-webgl', '--ignore-gpu-blocklist', '--disable-gpu-sandbox'],
    },
});
config.client = config.client || {};
config.client.mocha = Object.assign({}, config.client.mocha, { timeout: 30000 });
config.browserNoActivityTimeout = 5 * 60 * 1000;
