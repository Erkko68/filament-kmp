# Compose Integration: Web (JS) Approach

Integrating Filament with Compose Multiplatform on the Web target requires a different strategy than the pixel readback used on other platforms. This is due to how the browser handles WebGL canvases and how Compose Web (Skia/Canvas-based) interacts with the DOM.

## The "Hole Punching" Strategy

On the Web, `filament-compose` uses a **DOM Overlay** (or "Hole Punching") approach. Instead of trying to draw Filament's output into the Compose canvas, we place the actual Filament `<canvas>` *behind* the Compose canvas and make the Compose layer transparent where the 3D content should appear.

### 1. Canvas Ownership
The `FilamentEngine` in the JS target maintains a reference to a `jsCanvas` (an `HTMLCanvasElement`). This canvas is where the Filament WebGL backend actually renders.

### 2. `WebElementView` Integration
We use Compose's `WebElementView` (part of `androidx.compose.ui.viewinterop`) to inject the Filament canvas into the DOM tree.
- `FilamentSurface` creates a container `div`.
- The `jsCanvas` is appended as a child of this container.

### 3. Layering (The Z-Index Hack)
To ensure the 3D content is correctly layered:
- The container `div` is assigned a `zIndex = "-1"` via a `requestAnimationFrame` callback. This pushes the 3D content behind the main Compose layer.
- The `WebElementView` itself is styled to fill the available space.

### 4. Hole Punching with `BlendMode.Clear`
To make the 3D content visible through the Compose UI:
- We use a `drawBehind` modifier on the `WebElementView`.
- We draw a rectangle with `Color.Transparent` and `BlendMode.Clear`. This "punches a hole" in the Compose canvas, allowing the underlying DOM elements (our Filament canvas) to show through.

## Benefits and Limitations

### Advantages
- **Full Performance**: Filament renders directly to its own WebGL canvas at full speed without any CPU readback or extra copies.
- **Native Browser Features**: Since it's a real DOM element, browser dev tools and standard CSS can interact with the container.

### Limitations
- **Z-Index Complexity**: Managing the stacking order between multiple 3D views and other DOM-based Compose components can be tricky.
- **Hole Punching Constraints**: The `BlendMode.Clear` trick requires the Compose canvas to have a transparent background or be configured correctly to allow seeing through to the DOM.
- **Event Handling**: Interaction events (clicks, scrolls) might need careful propagation if you want to interact with both the 3D scene and Compose elements in the same area.

## Known Issues & Workarounds

The "Hole Punching" approach is necessary because of current limitations in how Compose Multiplatform Web handles interop with standard DOM elements.

### 1. Stacking and Interop (CMP-8521)
Currently, there are challenges in managing the stacking order and event propagation when mixing the Compose Web canvas with external DOM elements like our Filament `<canvas>`.
- **See**: [CMP-8521: Improve interop with DOM elements](https://youtrack.jetbrains.com/issue/CMP-8521)

### 2. Z-Index Layering (CMP-6858)
To allow Filament to render "behind" the Compose UI, we use a `zIndex` workaround. This requires the Compose HTML host to support transparency so the 3D content can be seen through the "punched hole".
- **See**: [CMP-6858: Support for transparent background in Compose HTML](https://youtrack.jetbrains.com/issue/CMP-6858)

## Implementation Details
The core logic, including these workarounds, can be found in [FilamentSurface.js.kt](file:///Users/eric/IdeaProjects/filament-kmp/kotlin/filament-compose/src/jsMain/kotlin/io/github/erkko68/filament/compose/internal/FilamentSurface.js.kt).
