package dev.filament.kmp

internal actual class Platform private constructor(
    private val impl: Impl,
) {
    actual fun log(message: String) {
        impl.log(message)
    }

    actual fun warn(message: String) {
        impl.warn(message)
    }

    actual fun validateStreamSource(obj: Any?): Boolean = impl.validateStreamSource(obj)

    actual fun validateSurface(obj: Any?): Boolean = impl.validateSurface(obj)

    actual fun validateSharedContext(obj: Any?): Boolean = impl.validateSharedContext(obj)

    actual fun getSharedContextNativeHandle(sharedContext: Any?): Long = impl.getSharedContextNativeHandle(sharedContext)

    actual companion object {
        private val instance = Platform(UnknownImpl())

        actual fun isAndroid(): Boolean = false

        actual fun isWindows(): Boolean = false

        actual fun isMacOS(): Boolean = false

        actual fun isLinux(): Boolean = false

        actual fun get(): Platform = instance
    }

    private interface Impl {
        fun log(message: String)
        fun warn(message: String)
        fun validateStreamSource(obj: Any?): Boolean
        fun validateSurface(obj: Any?): Boolean
        fun validateSharedContext(obj: Any?): Boolean
        fun getSharedContextNativeHandle(sharedContext: Any?): Long
    }

    private class UnknownImpl : Impl {
        override fun log(message: String) {
            println(message)
        }

        override fun warn(message: String) {
            println(message)
        }

        override fun validateStreamSource(obj: Any?): Boolean = false

        override fun validateSurface(obj: Any?): Boolean = false

        override fun validateSharedContext(obj: Any?): Boolean = false

        override fun getSharedContextNativeHandle(sharedContext: Any?): Long = 0L
    }
}
