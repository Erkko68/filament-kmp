# Migrating from 0.8

The next release changes how the `filament-compose` camera controllers hold the camera. The compiler finds every
change; none of them is silent.

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

- **`rememberMapCameraController`**: `mapWidth`, `mapHeight` and `minDistance` are gone. Put the camera at the height
  you want with `initialEye`; `minDistance` had no effect in camutils.
- **`rememberFlightCameraController`**: `startPitch` and `startYaw` are gone. The camera looks from `initialEye`
  towards `initialTarget`.

### 3. `setViewport` is gone

The gesture modifiers always set the viewport, and the manipulator it fed was internal. Delete the call.
