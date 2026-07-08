package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Web coverage for the color-skybox build + `Scene.setSkybox` path, kept **synchronous** on
 * purpose: the identical sequence run from a promise continuation (any async context) corrupts the
 * filament.wasm heap and traps with "memory access out of bounds" in `Scene.setSkybox` — the reason
 * EnvironmentLifecycleTest in filament-compose is `@IgnoreJs`. If that prebuilt bug is fixed, this
 * plus regating those tests is the regression check.
 */
class SkyboxSetTest : FilamentTestFixture() {
    @Test
    fun setColorSkyboxOnScene() {
        val scene = engine.createScene()
        val skybox = Skybox.Builder()
            .showSun(false)
            .intensity(1.0f)
            .priority(0)
            .color(0.1f, 0.2f, 0.3f, 1.0f)
            .build(engine)
        scene.skybox = skybox
        assertNotNull(scene.skybox, "skybox should be attached")
        scene.skybox = null
        assertNull(scene.skybox, "skybox should be cleared")
        engine.destroySkybox(skybox)
        engine.destroyScene(scene)
    }
}
