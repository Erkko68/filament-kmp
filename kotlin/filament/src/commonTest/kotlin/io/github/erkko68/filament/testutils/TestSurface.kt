package io.github.erkko68.filament.testutils

import io.github.erkko68.filament.NativeSurface

/**
 * Creates a platform-appropriate dummy NativeSurface for testing purposes.
 */
expect fun createTestSurface(): NativeSurface
