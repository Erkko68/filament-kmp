package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.DialogWindow
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.testsupport.TestEnv
import java.awt.Frame
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.fail

private fun <T> ui(block: () -> T): T {
    var result: Result<T>? = null
    SwingUtilities.invokeAndWait { result = runCatching(block) }
    return result!!.getOrThrow()
}

private fun window(content: @Composable () -> Unit): ComposeWindow = ui {
    ComposeWindow().apply {
        setSize(480, 360)
        setContent { content() }
        isVisible = true
    }
}

private fun settle(ms: Long = 400) = Thread.sleep(ms)

@Composable
private fun World(transparent: Boolean = false, renderingEnabled: Boolean = true) =
    FilamentSceneView(Modifier.fillMaxSize(), transparent = transparent, renderingEnabled = renderingEnabled) {
        DirectionalLight()
        Cube(material = rememberColorMaterialInstance(LinearColor(1f, 0f, 0f)))
        GltfInstance(rememberGltfAsset { TestGlb.getDuckGlbBytes() })
    }

// What a desktop window does to a view on its own: none of it goes through the composition.
private val scenarios: Map<String, () -> Unit> = mapOf(
    "closedWhileRendering" to {
        repeat(4) {
            val w = window { World() }
            settle(if (it % 2 == 0) 500 else 30) // also mid-load, before the first frame
            ui { w.dispose() }
        }
    },
    "resizedInAStorm" to {
        val w = window { World() }
        settle()
        repeat(60) { i -> ui { w.setSize(200 + (i * 37) % 500, 150 + (i * 53) % 400) }; Thread.sleep(if (i % 10 == 0) 200 else 8) }
        ui { w.setSize(0, 0) }
        settle()
        ui { w.setSize(300, 300) }
        settle()
        ui { w.dispose() }
    },
    "minimisedAndHidden" to {
        val w = window { World() }
        settle()
        repeat(3) {
            ui { w.extendedState = Frame.ICONIFIED }
            settle()
            ui { w.extendedState = Frame.NORMAL }
            settle()
            ui { w.isVisible = false }
            settle()
            ui { w.setSize(400 + 40 * it, 300) } // resized while hidden
            ui { w.isVisible = true }
            settle()
        }
        ui { w.extendedState = Frame.ICONIFIED }
        settle()
        ui { w.dispose() } // closed from the dock while minimised
    },
    "pausedAcrossWindowEvents" to {
        var paused by mutableStateOf(true)
        val w = window { World(renderingEnabled = !paused) }
        settle()
        repeat(4) {
            ui { w.setSize(300 + 50 * it, 300) }
            settle(250)
            ui { w.extendedState = Frame.ICONIFIED }
            paused = !paused
            settle(250)
            ui { w.extendedState = Frame.NORMAL }
            settle(250)
        }
        ui { w.dispose() }
    },
    "aDialogOnTheWindowsEngine" to {
        var dialogs by mutableIntStateOf(0)
        val w = window {
            val engine = rememberFilamentEngine()
            val scene = rememberFilamentScene(engine) {
                DirectionalLight()
                Cube(material = rememberColorMaterialInstance(LinearColor(0f, 1f, 0f)))
            }
            Row { FilamentView(scene, Modifier.fillMaxSize()) }
            // The same scene in a second window, with its own Skia context.
            if (dialogs % 2 == 1) DialogWindow(onCloseRequest = {}) { FilamentView(scene, Modifier.fillMaxSize()) }
        }
        settle()
        repeat(6) { dialogs++; settle(350) }
        dialogs = 1
        settle()
        ui { w.dispose() } // the owner goes with its dialog still open
    },
    "transparentToggledWhileMinimised" to {
        var transparent by mutableStateOf(false)
        val w = window { World(transparent = transparent) }
        settle()
        repeat(4) {
            ui { w.extendedState = Frame.ICONIFIED }
            transparent = !transparent
            settle(250)
            ui { w.extendedState = Frame.NORMAL }
            settle(250)
        }
        ui { w.dispose() }
    },
)

/** Child-JVM entry: a crash here is a dead process, which the test reads as its exit code. */
object WindowLifecycleMain {
    @OptIn(ExperimentalGpuToGpuFrameSharing::class)
    @JvmStatic
    fun main(args: Array<String>) {
        FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = args[1] == "gpu"
        Thread.setDefaultUncaughtExceptionHandler { _, e -> e.printStackTrace(); Runtime.getRuntime().halt(3) }
        scenarios.getValue(args[0])()
        settle()
        Runtime.getRuntime().halt(0)
    }
}

class WindowLifecycleTest {
    @Test
    fun aViewSurvivesWhatItsWindowDoes() {
        if (!TestEnv.gpuBackendAvailable || java.awt.GraphicsEnvironment.isHeadless()) return
        val java = ProcessHandle.current().info().command().get()
        val failures = scenarios.keys.flatMap { s -> listOf("readback", "gpu").map { s to it } }.mapNotNull { (scenario, mode) ->
            val p = ProcessBuilder(
                java, "--enable-native-access=ALL-UNNAMED", "-Djava.awt.headless=false",
                "-cp", System.getProperty("java.class.path"), WindowLifecycleMain::class.java.name, scenario, mode,
            ).redirectErrorStream(true).start()
            val out = p.inputStream.bufferedReader().readText()
            val code = p.waitFor()
            // A GPU-sharing report means the fast path broke and fell back: not a crash, still a bug.
            if (code == 0 && "frame sharing failed" !in out && "Exception" !in out) null else "── $scenario ($mode) exit $code\n${out.takeLast(6000)}"
        }
        if (failures.isNotEmpty()) fail("Window events a view didn't survive:\n${failures.joinToString("\n")}")
    }
}
