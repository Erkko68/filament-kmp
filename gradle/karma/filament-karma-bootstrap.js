// Delays Karma's test start until filament-kmp.wasm (and filamat-kmp.wasm, when included) is
// instantiated, and publishes the instances on globalThis.filamentKmp / filamatKmp, where the
// Kotlin side adopts them. Prepended to the Karma files list by each module's karma.config.d/filament-setup.js.
(function () {
    var originalLoaded = __karma__.loaded.bind(__karma__);
    __karma__.loaded = function () {
        var loads = [createFilamentModule().then(function (m) { globalThis.filamentKmp = m; })];
        // filamat-kmp.js is only included by :kotlin:filamat's Karma config.
        if (typeof createFilamatModule === 'function') {
            loads.push(createFilamatModule().then(function (m) { globalThis.filamatKmp = m; }));
        }
        Promise.all(loads).then(function () { originalLoaded(); });
    };
}());
