// Delays Karma's test start until the Filament WASM module is fully initialised
// and all its classes have been spread into the global (window) scope.
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
            // Spread all Filament properties onto window so external Kotlin
            // declarations (which expect top-level globals) can resolve them.
            var nativeFetch = window.fetch;
            Object.assign(window, Filament);
            window.fetch = nativeFetch;

            // Filament uses '$' as a separator (e.g. Camera$Fov) but the
            // Kotlin externals use '_' (Camera_Fov); create aliases for both.
            Object.getOwnPropertyNames(Filament).forEach(function (k) {
                if (k.indexOf('$') !== -1) {
                    window[k.replace(/\$/g, '_')] = Filament[k];
                }
            });

            originalLoaded();
        });
    };
}());
