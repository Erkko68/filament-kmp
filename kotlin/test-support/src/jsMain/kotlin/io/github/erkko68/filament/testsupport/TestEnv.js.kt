package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.JS
    // WebGL engine creation works; per-feature web gaps are handled with @IgnoreJs.
    actual val gpuBackendAvailable: Boolean = true
}

actual typealias IgnoreJs = kotlin.test.Ignore
