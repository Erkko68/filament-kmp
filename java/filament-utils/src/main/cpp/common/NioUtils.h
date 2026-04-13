#pragma once

#include <cstdint>
#include <jni.h>

class AutoBuffer {
public:
    enum class BufferType : uint8_t {
        BYTE,
        CHAR,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE
    };

    AutoBuffer(JNIEnv* env, jobject buffer, jint size, bool commit = false) noexcept;
    AutoBuffer(AutoBuffer&& rhs) noexcept;
    ~AutoBuffer() noexcept;

    void attachToJniThread(JNIEnv* env) noexcept {
        mEnv = env;
    }

    void* getData() const noexcept {
        return mUserData;
    }

    size_t getSize() const noexcept {
        return mSize;
    }

    size_t getShift() const noexcept {
        return mShift;
    }

    size_t countToByte(size_t count) const noexcept {
        return count << mShift;
    }

private:
    void* mUserData = nullptr;
    size_t mSize = 0;
    BufferType mType = BufferType::BYTE;
    uint8_t mShift = 0;

    JNIEnv* mEnv;
    void* mData = nullptr;
    jobject mBuffer = nullptr;
    jarray mBaseArray = nullptr;
    bool mDoCommit = false;
};
