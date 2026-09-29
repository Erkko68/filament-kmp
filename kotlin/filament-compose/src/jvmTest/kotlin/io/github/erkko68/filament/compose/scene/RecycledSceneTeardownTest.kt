package io.github.erkko68.filament.compose.scene

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberFilamentScene
import io.github.erkko68.filament.testsupport.TestEnv
import kotlin.test.Test

/**
 * Regression: scenes in the items of a scrolled LazyColumn, each item with its own engine and the scene in a
 * SubcomposeLayout. Recycling an item used to destroy its scene's standard material while an instance created a frame
 * later was still alive, and Filament aborted the JVM: `destroying material "StandardLit" but 1 instances still alive`.
 */
class RecycledSceneTeardownTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun scrollingRecycledScenesTearsDownCleanly() {
        if (!TestEnv.gpuBackendAvailable) return
        recycleScenes()
    }

    @OptIn(ExperimentalTestApi::class)
    private fun recycleScenes() = runComposeUiTest {
        mainClock.autoAdvance = false
        val list = LazyListState()
        setContent {
            LazyColumn(state = list, modifier = Modifier.height(400.dp)) {
                items(50) {
                    val engine = rememberFilamentEngine()
                    BoxWithConstraints(Modifier.height(200.dp)) {
                        rememberFilamentScene(engine) {
                            // Created a frame after the item, like a material waiting on a texture.
                            var ready by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { withFrameNanos { }; ready = true }
                            if (ready) rememberColorMaterialInstance(LinearColor(1f, 0f, 0f))
                        }
                    }
                }
            }
        }
        // Scroll down and back up, a few pixels per frame, at several speeds: items are recycled as they leave.
        for (step in listOf(37f, 90f, 173f, 260f)) repeat(2) {
            for (delta in listOf(step, -step)) repeat((9000 / step).toInt()) {
                runOnUiThread { list.dispatchRawDelta(delta) }
                mainClock.advanceTimeByFrame()
            }
        }
    }
}
