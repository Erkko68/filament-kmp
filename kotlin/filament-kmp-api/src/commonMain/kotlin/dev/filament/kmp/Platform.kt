package dev.filament.kmp

internal expect class Platform {
    fun log(message: String)

    fun warn(message: String)

    fun validateStreamSource(obj: Any?): Boolean

    fun validateSurface(obj: Any?): Boolean

    fun validateSharedContext(obj: Any?): Boolean

    fun getSharedContextNativeHandle(sharedContext: Any?): Long

    companion object {
        fun isAndroid(): Boolean

        fun isWindows(): Boolean

        fun isMacOS(): Boolean

        fun isLinux(): Boolean

        fun get(): Platform
    }
}

