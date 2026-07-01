@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ComposeViewport
import io.github.erkko68.filament.Filament
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Entry point for Filament-powered Compose web apps.
 *
 * Handles the full lifecycle:
 * 1. Initializes the Filament WASM module and spreads globals
 * 2. Creates a full-viewport root element in the DOM
 * 3. Mounts a [ComposeViewport] and renders [content]
 *
 * Usage:
 * ```kotlin
 * fun main() = FilamentApp { App() }
 * ```
 *
 * The hosting `index.html` only needs to load `filament.js` before the
 * application script — no `<div id="root">` is required.
 */
fun FilamentApp(content: @Composable () -> Unit) {
    Filament.initJs {
        val root = (document.createElement("div") as HTMLElement).apply {
            style.width = "100%"
            style.height = "100%"
            style.position = "relative"
            style.zIndex = "0"
        }
        document.body?.appendChild(root)
        ComposeViewport(root) { content() }
    }
}
