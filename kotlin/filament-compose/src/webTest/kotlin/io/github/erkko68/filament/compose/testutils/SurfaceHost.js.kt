package io.github.erkko68.filament.compose.testutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.w3c.dom.HTMLElement
import kotlin.time.Duration.Companion.seconds

private var run by mutableIntStateOf(0)
private var slot by mutableStateOf<@Composable () -> Unit>({})

/** The element the tests' one viewport lives in. */
internal val pageContainer: HTMLElement by lazy {
    val container = document.createElement("div") as HTMLElement
    container.style.width = "400px"
    container.style.height = "600px"
    document.body!!.appendChild(container)
    container
}

// One viewport for every test: a viewport is never disposed, and each holds a WebGL context.
@OptIn(ExperimentalComposeUiApi::class)
private val viewport by lazy { ComposeViewport(pageContainer) { key(run) { slot() } } }

/** Shows [content] in the page's viewport while [body] runs. */
internal suspend fun onPage(content: @Composable () -> Unit, body: suspend () -> Unit) {
    viewport
    run++
    slot = content
    try {
        body()
    } finally {
        slot = {}
    }
}

// A real viewport on the page: Compose's test host has no DOM container, so an HtmlElementView cannot be
// composed in it.
actual fun surfaceTest(frames: Int, content: @Composable (frame: Int) -> Unit): TestResult = runTest(timeout = 30.seconds) {
    val done = CompletableDeferred<Unit>()
    onPage({
        var frame by remember { mutableIntStateOf(0) }
        LaunchedEffect(Unit) {
            while (true) withFrameNanos { if (++frame > frames + 2) done.complete(Unit) }
        }
        if (frame <= frames) content(frame)
    }) { done.await() }
}
