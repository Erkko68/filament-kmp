package io.github.erkko68.filament.web

external class GpuContextPriority : JsAny {
    companion object {
        val DEFAULT: GpuContextPriority
        val LOW: GpuContextPriority
        val MEDIUM: GpuContextPriority
        val HIGH: GpuContextPriority
        val REALTIME: GpuContextPriority
    }
}
