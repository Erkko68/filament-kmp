@file:OptIn(kotlin.js.ExperimentalJsExport::class, androidx.compose.ui.ExperimentalComposeUiApi::class)

package eric.bitria.samples

import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    window.asDynamic()._filamentReady = ::startApp

    // Spread Filament into window so Kotlin externals (which expect top-level globals) can find them.
    // Filament uses '$' as separator (Camera$Fov) but the Kotlin externals use '_' (Camera_Fov),
    // so we also create '_'-aliased names for every '$'-named property.
    js("""
        Filament.init([], function() {
            var nativeFetch = window.fetch;
            Object.assign(window, Filament);
            window.fetch = nativeFetch;
            Object.getOwnPropertyNames(Filament).forEach(function(k) {
                if (k.indexOf('${'$'}') !== -1) window[k.replace(/\${'$'}/g, '_')] = Filament[k];
            });
            window._filamentReady();
        });
    """)
}

@JsExport
@Suppress("unused")
fun startApp() {
    val container = document.getElementById("root")
        ?: error("No #root element found")
    ComposeViewport(container) {
        App()
    }
}
