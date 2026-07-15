// Delays Karma's test start until the Filament WASM module is fully initialised
// and its namespace members are exposed as globals.
//
// Without this the compiled Kotlin code references bare globals like `Engine`,
// `Renderer`, `View`, etc. that don't exist until after Filament.init() fires.
//
// This file is prepended to the Karma files list by filament-setup.js so it
// runs in the browser context before any test code.

(function () {
    var originalLoaded = __karma__.loaded.bind(__karma__);

    __karma__.loaded = function () {
        Filament.init([], function () {
            // Expose Filament namespace members as globals so the external
            // Kotlin declarations can resolve them. Only add missing names —
            // never overwrite (keeps e.g. window.fetch intact). `$`-separated
            // names (Texture$Builder) are referenced as-is via @JsName on the
            // Kotlin externals, so no `_`-alias copies are needed.
            Object.getOwnPropertyNames(Filament).forEach(function (k) {
                if (!(k in globalThis)) globalThis[k] = Filament[k];
            });

            originalLoaded();
        });
    };
}());
