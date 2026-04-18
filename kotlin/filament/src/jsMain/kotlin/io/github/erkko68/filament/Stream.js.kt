package io.github.erkko68.filament

import io.github.erkko68.filament.js.Stream as JSStream
import io.github.erkko68.filament.js.`Stream_Builder` as JSStreamBuilder

actual class Stream(internal val jsStream: JSStream) {
    actual fun getStreamType(): StreamType {
        return StreamType.NATIVE
    }

    actual fun setDimensions(width: Int, height: Int) {
        jsStream.setDimensions(width, height)
    }

    actual fun getTimestamp(): Long {
        return jsStream.getTimestamp().toLong()
    }

    actual enum class StreamType { NATIVE, ACQUIRED }
    actual class Builder {
        private val jsBuilder = JSStreamBuilder()

        actual fun stream(streamSource: Any): Builder {
            // streamSource is usually a HTMLVideoElement or similar in JS
            jsBuilder.stream(streamSource)
            return this
        }

        actual fun width(width: Int): Builder {
            jsBuilder.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            jsBuilder.height(height)
            return this
        }

        actual fun build(engine: Engine): Stream {
            return Stream(jsBuilder.build(engine.jsEngine))
        }
    }
}