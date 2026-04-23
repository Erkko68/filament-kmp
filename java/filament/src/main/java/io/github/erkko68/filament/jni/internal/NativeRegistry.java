package io.github.erkko68.filament.jni.internal;

import java.lang.ref.Cleaner;

/**
 * Internal utility to manage native resource cleanup using java.lang.ref.Cleaner.
 */
public final class NativeRegistry {
    private static final Cleaner CLEANER = Cleaner.create();

    private NativeRegistry() {
    }

    /**
     * Registers an object for native cleanup.
     *
     * @param obj         The Java object to monitor.
     * @param cleanupTask A Runnable that performs the native cleanup (must NOT contain a reference to 'obj').
     * @return A Cleanable instance that can be used to trigger cleanup explicitly.
     */
    public static Cleaner.Cleanable registerForCleanup(Object obj, Runnable cleanupTask) {
        return CLEANER.register(obj, cleanupTask);
    }
}
