package dev.filament.kmp

import java.lang.reflect.Method

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
        private var currentPlatform: Platform? = null

        actual fun isAndroid(): Boolean =
            "The Android Project".equals(System.getProperty("java.vendor"), ignoreCase = true)

        actual fun isWindows(): Boolean =
            System.getProperty("os.name").orEmpty().contains("Windows")

        actual fun isMacOS(): Boolean =
            System.getProperty("os.name").orEmpty().contains("Mac OS X")

        actual fun isLinux(): Boolean =
            System.getProperty("os.name").orEmpty().contains("Linux") && !isAndroid()

        actual fun get(): Platform {
            currentPlatform?.let { return it }
            val resolved = try {
                val className = if (isAndroid()) {
                    "com.google.android.filament.AndroidPlatform"
                } else {
                    "com.google.android.filament.DesktopPlatform"
                }
                Platform(ReflectiveImpl(Class.forName(className).getDeclaredConstructor().newInstance()))
            } catch (_: Exception) {
                Platform(UnknownImpl())
            }
            currentPlatform = resolved
            return resolved
        }
    }

    private interface Impl {
        fun log(message: String)
        fun warn(message: String)
        fun validateStreamSource(obj: Any?): Boolean
        fun validateSurface(obj: Any?): Boolean
        fun validateSharedContext(obj: Any?): Boolean
        fun getSharedContextNativeHandle(sharedContext: Any?): Long
    }

    private class ReflectiveImpl(
        private val delegate: Any,
    ) : Impl {
        override fun log(message: String) {
            invokeVoid("log", message)
        }

        override fun warn(message: String) {
            invokeVoid("warn", message)
        }

        override fun validateStreamSource(obj: Any?): Boolean {
            return invokeBoolean("validateStreamSource", obj)
        }

        override fun validateSurface(obj: Any?): Boolean {
            return invokeBoolean("validateSurface", obj)
        }

        override fun validateSharedContext(obj: Any?): Boolean {
            return invokeBoolean("validateSharedContext", obj)
        }

        override fun getSharedContextNativeHandle(sharedContext: Any?): Long {
            return runCatching {
                (method("getSharedContextNativeHandle").invoke(delegate, sharedContext) as Number).toLong()
            }.getOrDefault(0L)
        }

        private fun invokeVoid(name: String, arg: Any?) {
            runCatching {
                method(name).invoke(delegate, arg)
            }
        }

        private fun invokeBoolean(name: String, arg: Any?): Boolean {
            return runCatching {
                method(name).invoke(delegate, arg) as Boolean
            }.getOrDefault(false)
        }

        private fun method(name: String): Method =
            delegate.javaClass.getDeclaredMethod(name, Any::class.java).apply { isAccessible = true }
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
