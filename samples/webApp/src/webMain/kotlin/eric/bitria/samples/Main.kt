package eric.bitria.samples

import io.github.erkko68.filament.compose.FilamentApp
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.filamat.initJs

// ponytail: loads filamat-kmp.wasm (~6 MB) up front for the Runtime Material scene; lazy-load it in real apps.
fun main() = MaterialBuilder.initJs { FilamentApp { App() } }

