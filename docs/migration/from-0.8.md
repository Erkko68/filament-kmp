# Migrating from 0.8

0.9.0 changes how the `filament-compose` camera controllers hold the camera, and moves to Filament 1.79.0, which
needs your materials recompiled. The compiler finds almost every change. The ones it does not find are marked
**silent** below.

## Why

**The camera was in two places.** On 0.8 each controller kept its own copy of the camera inside Filament's camutils
manipulator, and `CameraState` was only written from it. The pose you could save was a native `Bookmark` that had to
be closed, and writing `CameraState` yourself was undone by the next drag. The orbit pivot never reached
`CameraState`: its `target` was a point one unit in front of the eye.

Now `CameraState` is the only copy. The orbit and map controllers work on it directly, and flight carries on from
whatever is written to it. A view to keep is a plain `Viewpoint`, and `CameraState` can fly to one with `animateTo`.

## How

### 1. Bookmarks become viewpoints

```kotlin
// 0.8
val bookmark = orbit.saveBookmark()
orbit.jumpToBookmark(bookmark)
bookmark.close()

// now
val viewpoint = cameraState.viewpoint
cameraState.viewpoint = viewpoint            // or scope.launch { cameraState.animateTo(viewpoint) }
```

A `Viewpoint` needs no closing, and `Viewpoint.Saver` keeps it across configuration changes. `resetToHome()` stays.

### 2. Start poses come from the camera state

**Map (silent).** The map controller no longer places the camera itself. On 0.8 it ignored the state's eye and up and
looked straight down from a height set by `mapWidth`/`mapHeight`, north (-Z) at the top. Now it starts from the state,
so a state left at its defaults gives a side view. Set the top-down pose on the state:

```kotlin
// 0.8
val cam = rememberCameraState(initialTarget = Position(0f))
val map = rememberMapCameraController(cam, mapWidth = 100f, mapHeight = 100f)

// now: about 0.83 × height of ground is in view at the default 45° field of view
val cam = rememberCameraState(
    initialEye = Position(0f, 120f, 0f),
    initialTarget = Position(0f),
    initialUp = Direction(0f, 0f, -1f),               // required: the default +Y up is parallel to the gaze
    initialProjection = Projection.Perspective(far = 1000.0), // past the eye's height
)
val map = rememberMapCameraController(cam)
```

`minDistance` is gone too; it had no effect in camutils. Zoom now stops at the near plane.

**Flight.** `startPitch` and `startYaw` are gone; the camera looks from `initialEye` towards `initialTarget`. With the
default target this is close to 0.8's default heading. If you passed them, aim with `initialTarget` instead. If you
set a non-default `initialTarget` without them, the camera now starts facing it (**silent**).

### 3. `setViewport` is gone

The gesture modifiers always set the viewport, and the manipulator it fed was internal. Delete the call.

### 4. Recompile your materials for Filament 1.79.0

> [!WARNING]
> Filament 1.79.0 raises `MATERIAL_VERSION` to 79, and the engine rejects materials compiled for 78. Rebuild each
> `.mat` with the `matc` from the [1.79.0 release](https://github.com/google/filament/releases/tag/v1.79.0). The
> built-in `StandardMaterial`s ship recompiled.

### 5. Local transform transactions are gone

`TransformManager.openLocalTransformTransaction` and `commitLocalTransformTransaction` are no-ops in Filament 1.79.0,
which deprecates them, so they're removed. There is no replacement because none is needed: `setTransform` now only marks
the node dirty, and Filament computes the world transforms in one pass the next time they're read (by
`getWorldTransform` or the renderer). A transaction used to do that by hand; now it always happens. Delete the calls.

### 6. TAA's `varianceGamma` default (silent)

`TemporalAntiAliasingOptions.varianceGamma` now defaults to 0.7 (was 1.0), as in Filament 1.79.0, which narrows the
history box: less ghosting, a little more jitter. Set it back to 1.0 if you relied on the old look. TAA also gains
motion-adaptive history and depth disocclusion options, all off or neutral by default.

### 7. Behaviour you may have worked around (silent)

These are fixes, but code that compensated for the old behaviour needs a look:

- Under an orbit controller, `cameraState.target` is the orbit pivot. On 0.8 it was a point one unit in front of the eye.
- Writing `eye`, `target` or `viewpoint` while a controller is attached now sticks. On 0.8 the next gesture snapped back.
- Orbit zoom stops at the near plane instead of passing through the target.
- Pans move the point under the pointer at the camera's field of view, so their speed differs from 0.8's.
