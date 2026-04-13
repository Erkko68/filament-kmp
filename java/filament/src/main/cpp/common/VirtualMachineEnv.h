#pragma once

#include <jni.h>

namespace filament {

class VirtualMachineEnv {
public:
    static VirtualMachineEnv& get() {
        static VirtualMachineEnv instance;
        return instance;
    }

    void setJvm(JavaVM* jvm) {
        mJvm = jvm;
    }

    JavaVM* getJvm() const {
        return mJvm;
    }

    JNIEnv* getEnvironment() {
        JNIEnv* env = nullptr;
        if (mJvm->GetEnv((void**) &env, JNI_VERSION_1_6) == JNI_EDETACHED) {
            mJvm->AttachCurrentThread((void**) &env, nullptr);
        }
        return env;
    }

private:
    VirtualMachineEnv() = default;
    JavaVM* mJvm = nullptr;
};

} // namespace filament
