package dev.filament.kmp

/**
 * A cross-platform equivalent to java.nio.Buffer.
 * This class provides a common API for handling memory buffers across Android and Native.
 */
expect abstract class Buffer {
    /**
     * Returns this buffer's capacity.
     */
    fun capacity(): Int

    /**
     * Returns this buffer's limit.
     */
    fun limit(): Int

    /**
     * Sets this buffer's limit.
     */
    fun limit(newLimit: Int): Buffer

    /**
     * Returns this buffer's position.
     */
    fun position(): Int

    /**
     * Sets this buffer's position.
     */
    fun position(newPosition: Int): Buffer

    /**
     * Returns the number of elements between the current position and the limit.
     */
    fun remaining(): Int

    /**
     * Tells whether there are any elements between the current position and the limit.
     */
    fun hasRemaining(): Boolean

    /**
     * Clears this buffer.
     */
    fun clear(): Buffer

    /**
     * Flips this buffer.
     */
    fun flip(): Buffer

    /**
     * Rewinds this buffer.
     */
    fun rewind(): Buffer
}

expect class ByteBuffer : Buffer {
    companion object {
        /**
         * Allocates a new direct byte buffer.
         */
        fun allocateDirect(capacity: Int): ByteBuffer
    }
}

expect class ShortBuffer : Buffer
expect class IntBuffer : Buffer
expect class FloatBuffer : Buffer
