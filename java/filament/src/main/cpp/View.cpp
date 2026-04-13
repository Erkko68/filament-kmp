#include <jni.h>
#include <filament/View.h>
#include <filament/Scene.h>
#include <filament/Camera.h>
#include <filament/Viewport.h>
#include <filament/ColorGrading.h>
#include <filament/Texture.h>
#include <math/vec3.h>
#include <math/vec4.h>

#include "common/CallbackUtils.h"
#include "common/VirtualMachineEnv.h"

using namespace filament;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetScene(JNIEnv* env, jclass clazz, jlong nativeView, jlong nativeScene) {
    ((View*) nativeView)->setScene((Scene*) nativeScene);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetCamera(JNIEnv* env, jclass clazz, jlong nativeView, jlong nativeCamera) {
    ((View*) nativeView)->setCamera((Camera*) nativeCamera);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetColorGrading(JNIEnv* env, jclass, jlong nativeView, jlong nativeColorGrading) {
    ((View*) nativeView)->setColorGrading((ColorGrading*) nativeColorGrading);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetRenderTarget(JNIEnv* env, jclass, jlong nativeView, jlong nativeTarget) {
    ((View*) nativeView)->setRenderTarget((RenderTarget*) nativeTarget);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetPostProcessingEnabled(JNIEnv* env, jclass, jlong nativeView, jboolean enabled) {
    ((View*) nativeView)->setPostProcessingEnabled(enabled);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetDithering(JNIEnv* env, jclass, jlong nativeView, jint dithering) {
    ((View*) nativeView)->setDithering((View::Dithering) dithering);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetViewport(JNIEnv* env, jclass clazz, jlong nativeView, jint left, jint bottom, jint width, jint height) {
    ((View*) nativeView)->setViewport({left, bottom, (uint32_t) width, (uint32_t) height});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetDynamicResolutionOptions(JNIEnv*, jclass, jlong nativeView,
        jboolean enabled, jboolean homogeneousScaling,
        jfloat minScale, jfloat maxScale, jfloat sharpness, jint quality) {
    View* view = (View*)nativeView;
    View::DynamicResolutionOptions options;
    options.enabled = enabled;
    options.homogeneousScaling = homogeneousScaling;
    options.minScale = math::float2{ minScale };
    options.maxScale = math::float2{ maxScale };
    options.sharpness = sharpness;
    options.quality = (View::QualityLevel)quality;
    view->setDynamicResolutionOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetBloomOptions(JNIEnv*, jclass,
        jlong nativeView, jlong nativeTexture,
        jfloat dirtStrength, jfloat strength, jint resolution, jint levels,
        jint blendMode, jboolean threshold, jboolean enabled, jfloat highlight,
        jint quality, jboolean lensFlare, jboolean starburst, jfloat chromaticAberration, jint ghostCount,
        jfloat ghostSpacing, jfloat ghostThreshold, jfloat haloThickness, jfloat haloRadius,
        jfloat haloThreshold) {
    View* view = (View*) nativeView;
    View::BloomOptions options = view->getBloomOptions();
    options.dirt = (Texture*) nativeTexture;
    options.dirtStrength = dirtStrength;
    options.strength = strength;
    options.resolution = (uint32_t)resolution;
    options.levels = (uint8_t)levels;
    options.blendMode = (View::BloomOptions::BlendMode)blendMode;
    options.threshold = (bool)threshold;
    options.enabled = (bool)enabled;
    options.highlight = highlight;
    options.quality = (View::QualityLevel)quality;
    options.lensFlare = (bool)lensFlare;
    options.starburst = (bool)starburst;
    options.chromaticAberration = chromaticAberration;
    options.ghostCount = (uint8_t)ghostCount;
    options.ghostSpacing = ghostSpacing;
    options.ghostThreshold = ghostThreshold;
    options.haloThickness = haloThickness;
    options.haloRadius = haloRadius;
    options.haloThreshold = haloThreshold;
    view->setBloomOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetFogOptions(JNIEnv *, jclass , jlong nativeView,
        jfloat distance, jfloat maximumOpacity, jfloat height, jfloat heightFalloff, jfloat cutOffDistance,
        jfloat r, jfloat g, jfloat b, jfloat density, jfloat inScatteringStart,
        jfloat inScatteringSize, jboolean fogColorFromIbl, jlong skyColorNativeObject, jboolean enabled) {
    View* view = (View*) nativeView;
    View::FogOptions options = {
             .distance = distance,
             .cutOffDistance = cutOffDistance,
             .maximumOpacity = maximumOpacity,
             .height = height,
             .heightFalloff = heightFalloff,
             .color = math::float3{r, g, b},
             .density = density,
             .inScatteringStart = inScatteringStart,
             .inScatteringSize = inScatteringSize,
             .fogColorFromIbl = (bool)fogColorFromIbl,
             .skyColor = (Texture*) skyColorNativeObject,
             .enabled = (bool)enabled
    };
    view->setFogOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetVignetteOptions(JNIEnv*, jclass, jlong nativeView, jfloat midPoint, jfloat roundness,
        jfloat feather, jfloat r, jfloat g, jfloat b, jfloat a, jboolean enabled) {
    View* view = (View*) nativeView;
    view->setVignetteOptions({.midPoint = midPoint, .roundness = roundness, .feather = feather,
            .color = math::float4{r, g, b, a}, .enabled = (bool)enabled});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetRenderQuality(JNIEnv*, jclass,
        jlong nativeView, jint hdrColorBufferQuality) {
    View* view = (View*) nativeView;
    View::RenderQuality renderQuality;
    renderQuality.hdrColorBuffer = View::QualityLevel(hdrColorBufferQuality);
    view->setRenderQuality(renderQuality);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetStereoscopicOptions(JNIEnv *, jclass, jlong nativeView,
        jboolean enabled) {
    View* view = (View*) nativeView;
    view->setStereoscopicOptions({ .enabled = (bool) enabled });
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetGuardBandOptions(JNIEnv *, jclass,
        jlong nativeView, jboolean enabled) {
    View* view = (View*) nativeView;
    view->setGuardBandOptions({ .enabled = (bool)enabled });
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetVisibleLayers(JNIEnv*, jclass, jlong nativeView, jint select, jint value) {
    ((View*) nativeView)->setVisibleLayers((uint8_t) select, (uint8_t) value);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_View_nGetVisibleLayers(JNIEnv*, jclass, jlong nativeView) {
    return (jint) ((View*) nativeView)->getVisibleLayers();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetShadowingEnabled(JNIEnv*, jclass, jlong nativeView, jboolean enabled) {
    ((View*) nativeView)->setShadowingEnabled(enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_View_nIsShadowingEnabled(JNIEnv*, jclass, jlong nativeView) {
    return (jboolean) ((View*) nativeView)->isShadowingEnabled();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetFrustumCullingEnabled(JNIEnv*, jclass, jlong nativeView, jboolean enabled) {
    ((View*) nativeView)->setFrustumCullingEnabled(enabled);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_View_nIsFrustumCullingEnabled(JNIEnv*, jclass, jlong nativeView) {
    return (jboolean) ((View*) nativeView)->isFrustumCullingEnabled();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetMaterialGlobal(JNIEnv*, jclass, jlong nativeView, jint index, jfloat x, jfloat y, jfloat z, jfloat w) {
    ((View*) nativeView)->setMaterialGlobal((uint32_t) index, {x, y, z, w});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nGetMaterialGlobal(JNIEnv* env, jclass, jlong nativeView, jint index, jfloatArray out_) {
    jfloat* out = env->GetFloatArrayElements(out_, nullptr);
    math::float4 result = ((View*) nativeView)->getMaterialGlobal((uint32_t) index);
    out[0] = result.x; out[1] = result.y; out[2] = result.z; out[3] = result.w;
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nClearFrameHistory(JNIEnv*, jclass, jlong nativeView, jlong nativeEngine) {
    ((View*) nativeView)->clearFrameHistory(*(Engine*) nativeEngine);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetAmbientOcclusionOptions(JNIEnv*, jclass,
    jlong nativeView, jfloat radius, jfloat power, jfloat bias, jfloat resolution, jfloat intensity,
    jfloat bilateralThreshold,
    jint quality, jint lowPassFilter, jint upsampling, jboolean enabled, jboolean bentNormals,
    jfloat minHorizonAngleRad, jfloat ssctLightConeRad, jfloat ssctShadowDistance, 
    jfloat ssctContactDistanceMax, jfloat ssctIntensity, jfloat ssctLightDirX, 
    jfloat ssctLightDirY, jfloat ssctLightDirZ, jfloat ssctDepthBias, 
    jfloat ssctDepthSlopeBias, jint ssctSampleCount, jint ssctRayCount, jboolean ssctEnabled,
    jint aoType, jint gtaoSampleSliceCount, jint gtaoSampleStepsPerSlice, 
    float gtaoThicknessHeuristic, jboolean gtaoUseVisibilityBitmasks, 
    jfloat gtaoConstThickness, jboolean gtaoLinearThickness) {
    
    View* view = (View*) nativeView;
    View::AmbientOcclusionOptions options = view->getAmbientOcclusionOptions();
    options.radius = radius;
    options.power = power;
    options.bias = bias;
    options.resolution = resolution;
    options.intensity = intensity;
    options.bilateralThreshold = bilateralThreshold;
    options.quality = (View::QualityLevel)quality;
    options.lowPassFilter = (View::QualityLevel)lowPassFilter;
    options.upsampling = (View::QualityLevel)upsampling;
    options.enabled = (bool)enabled;
    options.bentNormals = (bool)bentNormals;
    options.minHorizonAngleRad = minHorizonAngleRad;
    options.ssct.lightConeRad = ssctLightConeRad;
    options.ssct.shadowDistance = ssctShadowDistance;
    options.ssct.contactDistanceMax = ssctContactDistanceMax;
    options.ssct.intensity = ssctIntensity;
    options.ssct.lightDirection = math::float3{ ssctLightDirX, ssctLightDirY, ssctLightDirZ };
    options.ssct.depthBias = ssctDepthBias;
    options.ssct.depthSlopeBias = ssctDepthSlopeBias;
    options.ssct.sampleCount = (uint8_t)ssctSampleCount;
    options.ssct.rayCount = (uint8_t)ssctRayCount;
    options.ssct.enabled = (bool)ssctEnabled;
    options.type = (View::AmbientOcclusionOptions::AmbientOcclusionType)aoType;
    options.gtao.sampleSliceCount = gtaoSampleSliceCount;
    options.gtao.sampleStepsPerSlice = gtaoSampleStepsPerSlice;
    options.gtao.thicknessHeuristic = gtaoThicknessHeuristic;
    options.gtao.useVisibilityBitmasks = gtaoUseVisibilityBitmasks;
    options.gtao.constantThickness = gtaoConstThickness;
    options.gtao.linearThickness = gtaoLinearThickness;
    view->setAmbientOcclusionOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetTemporalAntiAliasingOptions(JNIEnv *, jclass,
    jlong nativeView, jfloat filterWidth, jfloat feedback, jfloat edgeThreshold, 
    jfloat filterThreshold, jfloat lodBias, jfloat sharpness, jboolean enabled, 
    jfloat upscaling, jboolean filterHistory, jboolean filterInput, jboolean useYCoCg, 
    jboolean hdr, jint boxType, jint boxClipping, jint jitterPattern, 
    jfloat varianceGamma, jboolean preventFlickering, jboolean historyReprojection) {
    
    View* view = (View*) nativeView;
    View::TemporalAntiAliasingOptions options = view->getTemporalAntiAliasingOptions();
    options.feedback = feedback;
    options.filterWidth = filterWidth;
    options.lodBias = lodBias;
    options.sharpness = sharpness;
    options.enabled = (bool) enabled;
    options.upscaling = upscaling;
    options.filterHistory = filterHistory;
    options.filterInput = filterInput;
    options.useYCoCg = useYCoCg;
    options.hdr = hdr;
    options.boxType = (View::TemporalAntiAliasingOptions::BoxType) boxType;
    options.boxClipping = (View::TemporalAntiAliasingOptions::BoxClipping) boxClipping;
    options.jitterPattern = (View::TemporalAntiAliasingOptions::JitterPattern) jitterPattern;
    options.varianceGamma = varianceGamma;
    options.preventFlickering = preventFlickering;
    options.historyReprojection = historyReprojection;
    view->setTemporalAntiAliasingOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetMultiSampleAntiAliasingOptions(JNIEnv* env, jclass clazz,
        jlong nativeView, jboolean enabled, jint sampleCount, jboolean customResolve) {
    View* view = (View*) nativeView;
    view->setMultiSampleAntiAliasingOptions({
            .enabled = (bool)enabled,
            .sampleCount = (uint8_t)sampleCount,
            .customResolve = (bool)customResolve});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetScreenSpaceReflectionsOptions(JNIEnv*, jclass,
        jlong nativeView, jfloat thickness, jfloat bias, jfloat maxDistance, jfloat stride, jboolean enabled) {
    View* view = (View*) nativeView;
    view->setScreenSpaceReflectionsOptions({.thickness = thickness, .bias = bias,
            .maxDistance = maxDistance, .stride = stride, .enabled = (bool) enabled
    });
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetDepthOfFieldOptions(JNIEnv *, jclass,
        jlong nativeView, jfloat cocScale, jfloat cocAspectRatio, jfloat maxApertureDiameter, 
        jboolean enabled, jint filter, jboolean nativeResolution, jint foregroundRingCount, 
        jint backgroundRingCount, jint fastGatherRingCount, jint maxForegroundCOC, jint maxBackgroundCOC) {
    View* view = (View*) nativeView;
    View::DepthOfFieldOptions options = view->getDepthOfFieldOptions();
    options.cocScale = cocScale;
    options.cocAspectRatio = cocAspectRatio;
    options.maxApertureDiameter = maxApertureDiameter;
    options.enabled = (bool)enabled;
    options.filter = (View::DepthOfFieldOptions::Filter)filter;
    options.nativeResolution = (bool)nativeResolution;
    options.foregroundRingCount = (uint8_t)foregroundRingCount;
    options.backgroundRingCount = (uint8_t)backgroundRingCount;
    options.fastGatherRingCount = (uint8_t)fastGatherRingCount;
    options.maxForegroundCOC = (uint8_t)maxForegroundCOC;
    options.maxBackgroundCOC = (uint8_t)maxBackgroundCOC;
    view->setDepthOfFieldOptions(options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nPick(JNIEnv* env, jclass,
        jlong nativeView, jint x, jint y, jobject handler, jobject callback) {

    static const struct JniState {
        jclass queryResultClass;
        jmethodID queryResultCtor;
        jfieldID renderableField;
        jfieldID depthField;
        jfieldID fragCoordsField;
        explicit JniState(JNIEnv* env) noexcept {
            queryResultClass = (jclass) env->NewGlobalRef(env->FindClass("io/github/erkko68/filament/View$PickingQueryResult"));
            queryResultCtor = env->GetMethodID(queryResultClass, "<init>", "()V");
            renderableField = env->GetFieldID(queryResultClass, "renderable", "I");
            depthField = env->GetFieldID(queryResultClass, "depth", "F");
            fragCoordsField = env->GetFieldID(queryResultClass, "fragCoords", "[F");
        }
    } jni(env);

    View* view = (View*) nativeView;
    JniCallback* jniCallback = JniCallback::make(env, handler, callback);

    view->pick(uint32_t(x), uint32_t(y), [jniCallback](View::PickingQueryResult const& result) {
        JNIEnv* env = VirtualMachineEnv::get().getEnvironment();
        
        jobject jResult = env->NewObject(jni.queryResultClass, jni.queryResultCtor);
        env->SetIntField(jResult, jni.renderableField, result.renderable.getId());
        env->SetFloatField(jResult, jni.depthField, result.depth);
        jfloatArray jCoords = (jfloatArray) env->GetObjectField(jResult, jni.fragCoordsField);
        env->SetFloatArrayRegion(jCoords, 0, 3, result.fragCoords.v);
        
        // Call back to Java logic (which should invoke the OnPickCallback)
        // Note: In our Java bridge, nPick takes the callback directly.
        jclass callbackClass = env->GetObjectClass(jniCallback->getCallbackObject());
        jmethodID onPick = env->GetMethodID(callbackClass, "onPick", "(Lio/github/erkko68/filament/View$PickingQueryResult;)V");
        env->CallVoidMethod(jniCallback->getCallbackObject(), onPick, jResult);
        
        JniCallback::postToJavaAndDestroy(jniCallback);
    }, jniCallback->getHandler());
}

} // extern "C"
