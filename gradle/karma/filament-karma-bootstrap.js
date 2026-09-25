// Delays Karma's test start until filament-kmp.wasm is instantiated, and publishes the instance
// on globalThis.filamentKmp, where the Kotlin side (io.github.erkko68.filament.wasm.fila) adopts
// it. Prepended to the Karma files list by each module's karma.config.d/filament-setup.js.
(function () {
    var originalLoaded = __karma__.loaded.bind(__karma__);
    __karma__.loaded = function () {
        createFilamentModule().then(function (module) {
            globalThis.filamentKmp = module;
            originalLoaded();
        });
    };
}());
