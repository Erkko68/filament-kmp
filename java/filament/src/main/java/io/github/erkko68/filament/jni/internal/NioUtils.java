package io.github.erkko68.filament.jni.internal;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Helper class for Nio Buffer operations in JNI.
 */
public final class NioUtils {

    public enum BufferType {
        BYTE,
        CHAR,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE
    }

    private NioUtils() {
    }

    static long getBasePointer(Buffer b, long address, int sizeShift) {
        return address != 0 ? address + (b.position() << sizeShift) : 0;
    }

    static Object getBaseArray(Buffer b) {
        return b.hasArray() ? b.array() : null;
    }

    static int getBaseArrayOffset(Buffer b, int sizeShift) {
        return b.hasArray() ? ((b.arrayOffset() + b.position()) << sizeShift) : 0;
    }

    static int getBufferType(Buffer b) {
        if (b instanceof ByteBuffer) {
            return BufferType.BYTE.ordinal();
        } else if (b instanceof CharBuffer) {
            return BufferType.CHAR.ordinal();
        } else if (b instanceof ShortBuffer) {
            return BufferType.SHORT.ordinal();
        } else if (b instanceof IntBuffer) {
            return BufferType.INT.ordinal();
        } else if (b instanceof LongBuffer) {
            return BufferType.LONG.ordinal();
        } else if (b instanceof FloatBuffer) {
            return BufferType.FLOAT.ordinal();
        } else if (b instanceof DoubleBuffer) {
            return BufferType.DOUBLE.ordinal();
        }
        return BufferType.BYTE.ordinal();
    }
}
