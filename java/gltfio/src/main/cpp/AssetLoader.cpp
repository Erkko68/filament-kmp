#include <jni.h>
#include <vector>

#include <filament/Engine.h>
#include <utils/EntityManager.h>
#include <utils/NameComponentManager.h>
#include <gltfio/AssetLoader.h>
#include <gltfio/MaterialProvider.h>

#include "common/NioUtils.h"
#include "MaterialKey.h"

using namespace filament;
using namespace filament::gltfio;
using namespace utils;

class JavaMaterialProvider : public MaterialProvider {
    JNIEnv* const mEnv;
    const jobject mJavaProvider;
    mutable Material** mPreviousMaterials = nullptr;
    jclass mMaterialKeyClass;
    jmethodID mMaterialKeyConstructor, mCreateMaterialInstance, mGetMaterial, mGetMaterials,
              mNeedsDummyData, mDestroyMaterials, mMaterialInstanceGetNativeObject,
              mMaterialGetNativeObject;

public:
    JavaMaterialProvider(JNIEnv* env, jobject provider) : mEnv(env),
            mJavaProvider(env->NewGlobalRef(provider)) {
        mMaterialKeyClass = (jclass) env->NewGlobalRef(env->FindClass(JAVA_MATERIAL_KEY));
        mMaterialKeyConstructor = env->GetMethodID(mMaterialKeyClass, "<init>", "()V");
        mMaterialInstanceGetNativeObject = env->GetMethodID(env->FindClass("io/github/erkko68/filament/MaterialInstance"), "getNativeObject", "()J");
        mMaterialGetNativeObject = env->GetMethodID(env->FindClass("io/github/erkko68/filament/Material"), "getNativeObject", "()J");
        jclass providerClass = env->GetObjectClass(provider);
        mCreateMaterialInstance = env->GetMethodID(providerClass, "createMaterialInstance", "(L" JAVA_MATERIAL_KEY ";[ILjava/lang/String;Ljava/lang/String;)Lio/github/erkko68/filament/MaterialInstance;");
        mGetMaterial = env->GetMethodID(providerClass, "getMaterial", "(L" JAVA_MATERIAL_KEY ";[ILjava/lang/String;)Lio/github/erkko68/filament/Material;");
        mGetMaterials = env->GetMethodID(providerClass, "getMaterials", "()[Lio/github/erkko68/filament/Material;");
        mNeedsDummyData = env->GetMethodID(providerClass, "needsDummyData","(I)Z");
        mDestroyMaterials = env->GetMethodID(providerClass, "destroyMaterials", "()V");
    }

    ~JavaMaterialProvider() override {
        mEnv->DeleteGlobalRef(mMaterialKeyClass);
        mEnv->DeleteGlobalRef(mJavaProvider);
        delete[] mPreviousMaterials;
    }

    MaterialInstance* createMaterialInstance(MaterialKey* config, UvMap* uvmap, const char* label, const char* extras) override {
        jobject javaKey = mEnv->NewObject(mMaterialKeyClass, mMaterialKeyConstructor);
        MaterialKeyHelper::get().copy(mEnv, javaKey, *config);
        jintArray uvmapArray = mEnv->NewIntArray(uvmap->size());
        jstring stringLabel = label ? mEnv->NewStringUTF(label) : nullptr;
        jstring stringExtras = extras ? mEnv->NewStringUTF(extras) : nullptr;
        jobject mi = mEnv->CallObjectMethod(mJavaProvider, mCreateMaterialInstance, javaKey, uvmapArray, stringLabel, stringExtras);
        if (uvmap) {
            jint* elements = mEnv->GetIntArrayElements(uvmapArray, nullptr);
            for (size_t i = 0; i < uvmap->size(); i++) (*uvmap)[i] = (UvSet) elements[i];
            mEnv->ReleaseIntArrayElements(uvmapArray, elements, JNI_ABORT);
        }
        MaterialKeyHelper::get().copy(mEnv, *config, javaKey);
        mEnv->DeleteLocalRef(javaKey); mEnv->DeleteLocalRef(uvmapArray);
        if (stringLabel) mEnv->DeleteLocalRef(stringLabel); if (stringExtras) mEnv->DeleteLocalRef(stringExtras);
        return mi ? (MaterialInstance*) mEnv->CallLongMethod(mi, mMaterialInstanceGetNativeObject) : nullptr;
    }

    Material* getMaterial(MaterialKey* config, UvMap* uvmap, const char* label) override {
        jobject javaKey = mEnv->NewObject(mMaterialKeyClass, mMaterialKeyConstructor);
        MaterialKeyHelper::get().copy(mEnv, javaKey, *config);
        jintArray uvmapArray = mEnv->NewIntArray(uvmap->size());
        jstring stringLabel = label ? mEnv->NewStringUTF(label) : nullptr;
        jobject m = mEnv->CallObjectMethod(mJavaProvider, mGetMaterial, javaKey, uvmapArray, stringLabel);
        if (uvmap) {
            jint* elements = mEnv->GetIntArrayElements(uvmapArray, nullptr);
            for (size_t i = 0; i < uvmap->size(); i++) (*uvmap)[i] = (UvSet) elements[i];
            mEnv->ReleaseIntArrayElements(uvmapArray, elements, JNI_ABORT);
        }
        MaterialKeyHelper::get().copy(mEnv, *config, javaKey);
        mEnv->DeleteLocalRef(javaKey); mEnv->DeleteLocalRef(uvmapArray);
        if (stringLabel) mEnv->DeleteLocalRef(stringLabel);
        return m ? (Material*) mEnv->CallLongMethod(m, mMaterialGetNativeObject) : nullptr;
    }

    const Material* const* getMaterials() const noexcept override {
        jobjectArray array = (jobjectArray) mEnv->CallObjectMethod(mJavaProvider, mGetMaterials);
        size_t count = mEnv->GetArrayLength(array);
        delete[] mPreviousMaterials;
        mPreviousMaterials = new Material*[count];
        for (size_t i = 0; i < count; ++i) {
            jobject m = mEnv->GetObjectArrayElement(array, i);
            mPreviousMaterials[i] = (Material*) mEnv->CallLongMethod(m, mMaterialGetNativeObject);
            mEnv->DeleteLocalRef(m);
        }
        mEnv->DeleteLocalRef(array);
        return mPreviousMaterials;
    }

    size_t getMaterialsCount() const noexcept override {
        jobjectArray array = (jobjectArray) mEnv->CallObjectMethod(mJavaProvider, mGetMaterials);
        size_t count = mEnv->GetArrayLength(array);
        mEnv->DeleteLocalRef(array);
        return count;
    }

    void destroyMaterials() override { mEnv->CallObjectMethod(mJavaProvider, mDestroyMaterials); }
    bool needsDummyData(VertexAttribute attrib) const noexcept override {
        return mEnv->CallBooleanMethod(mJavaProvider, mNeedsDummyData, (int) attrib);
    }
};

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nCreateAssetLoader(JNIEnv* env, jclass, jlong nativeEngine, jobject provider, jlong nativeEntities) {
    MaterialProvider* mp = nullptr;
    jmethodID getNativeObject = env->GetMethodID(env->GetObjectClass(provider), "getNativeObject", "()J");
    if (getNativeObject) mp = (MaterialProvider*) env->CallLongMethod(provider, getNativeObject);
    else env->ExceptionClear();
    if (mp == nullptr) mp = new JavaMaterialProvider(env, provider);
    EntityManager* em = (EntityManager*) nativeEntities;
    return (jlong) AssetLoader::create({(Engine*)nativeEngine, mp, new NameComponentManager(*em), em});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nDestroyAssetLoader(JNIEnv*, jclass, jlong nativeLoader) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    NameComponentManager* names = loader->getNames();
    AssetLoader::destroy(&loader);
    delete names;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nCreateAsset(JNIEnv* env, jclass, jlong nativeLoader, jobject javaBuffer, jint remaining) {
    AutoBuffer buffer(env, javaBuffer, remaining);
    return (jlong) ((AssetLoader*)nativeLoader)->createAsset((const uint8_t*)buffer.getData(), buffer.getSize());
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nCreateInstancedAsset(JNIEnv* env, jclass, jlong nativeLoader, jobject javaBuffer, jint remaining, jlongArray instances) {
    AutoBuffer buffer(env, javaBuffer, remaining);
    jsize num = env->GetArrayLength(instances);
    FilamentInstance** ptrs = new FilamentInstance*[num];
    jlong asset = (jlong) ((AssetLoader*)nativeLoader)->createInstancedAsset((const uint8_t*)buffer.getData(), buffer.getSize(), ptrs, num);
    if (asset) {
        jlong* l = env->GetLongArrayElements(instances, nullptr);
        for (jsize i = 0; i < num; i++) l[i] = (jlong) ptrs[i];
        env->ReleaseLongArrayElements(instances, l, 0);
    }
    delete[] ptrs;
    return asset;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nCreateInstance(JNIEnv*, jclass, jlong nativeLoader, jlong nativeAsset) {
    return (jlong) ((AssetLoader*)nativeLoader)->createInstance((FilamentAsset*)nativeAsset);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nEnableDiagnostics(JNIEnv*, jclass, jlong nativeLoader, jboolean enable) {
    ((AssetLoader*)nativeLoader)->enableDiagnostics(enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_AssetLoader_nDestroyAsset(JNIEnv*, jclass, jlong nativeLoader, jlong nativeAsset) {
    ((AssetLoader*)nativeLoader)->destroyAsset((FilamentAsset*)nativeAsset);
}
