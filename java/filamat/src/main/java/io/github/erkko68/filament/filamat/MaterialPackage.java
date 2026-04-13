package io.github.erkko68.filament.filamat;

import java.nio.ByteBuffer;

public class MaterialPackage {
    private final ByteBuffer mBuffer;
    private final boolean mIsValid;

    MaterialPackage(ByteBuffer buffer, boolean isValid) {
        mBuffer = buffer;
        mIsValid = isValid;
    }

    public ByteBuffer getBuffer() {
        return mBuffer;
    }

    public boolean isValid() {
        return mIsValid;
    }
}
