/*
 * Curated overlay for Filament's upstream `filament.d.ts`.
 *
 * WHY THIS EXISTS
 * Filament.js exposes its API through embind registrations in
 * `web/filament-js/jsbindings.cpp` — that file is the source of truth. The
 * shipped `filament.d.ts` is hand-maintained and lags behind it: methods that
 * are reachable at runtime are routinely missing or under-typed (wrong arity).
 *
 * The :js Gradle build concatenates this overlay onto the upstream
 * `filament.d.ts` before running Karakum, so the generated Kotlin externals
 * cover the *real* surface. Everything declared here is registered in
 * jsbindings.cpp for Filament 1.71.5.
 *
 * HOW TO EDIT
 * - Instance methods: reopen the class as an `interface` of the same name
 *   (TypeScript declaration merging). Adding an existing signature again is a
 *   harmless overload; adding a new-arity overload makes the under-typed
 *   upstream declaration callable with the real arguments.
 * - Missing enums / value objects: declare them here as a top-level `export`.
 * - Static (class) methods can't be added by merging — Karakum renders a merged
 *   `namespace` as a separate object. They're injected into the upstream class
 *   body instead; see `staticInjections` in js/build.gradle.kts.
 *
 * Names use `$` (the upstream separator); the build normalises `$`→`_` for the
 * whole assembled d.ts before generation. Run `scripts/dev/check-js-bindings.sh`
 * on every Filament bump to surface bindings present upstream but absent here.
 */

// ── Enums missing from the d.ts (registered as embind enums) ──────────────────

export enum FeatureLevel {
    FEATURE_LEVEL_1,
    FEATURE_LEVEL_2,
    FEATURE_LEVEL_3,
}

export enum TransparencyMode {
    DEFAULT,
    TWO_PASSES_ONE_SIDE,
    TWO_PASSES_TWO_SIDES,
}

// Returned by Engine.getConfig(); the upstream d.ts only types the *input*
// config object (Engine.create options), not the getter's result.
export interface Engine$Config {
    commandBufferSizeMB?: number;
    perRenderPassArenaSizeMB?: number;
    driverHandleArenaSizeMB?: number;
    minCommandBufferSizeMB?: number;
    perFrameCommandsSizeMB?: number;
    jobSystemThreadCount?: number;
    stereoscopicEyeCount?: number;
    resourceAllocatorCacheSizeMB?: number;
    resourceAllocatorCacheMaxAge?: number;
    sharedUboInitialSizeInBytes?: number;
    forceGLES2Context?: boolean;
}

// ── Engine ────────────────────────────────────────────────────────────────────

export interface Engine {
    getBackend(): Backend;
    getSupportedFeatureLevel(): FeatureLevel;
    setActiveFeatureLevel(level: FeatureLevel): FeatureLevel;
    getActiveFeatureLevel(): FeatureLevel;
    setAutomaticInstancingEnabled(enabled: boolean): void;
    isAutomaticInstancingEnabled(): boolean;
    enableAccurateTranslations(): void;
    unprotected(): boolean;
    hasUnrecoverableFailure(): boolean;
    getConfig(): Engine$Config;
    getEntityManager(): EntityManager;

    isValidRenderer(renderer: Renderer): boolean;
    isValidView(view: View): boolean;
    isValidScene(scene: Scene): boolean;
    isValidIndexBuffer(buffer: IndexBuffer): boolean;
    isValidVertexBuffer(buffer: VertexBuffer): boolean;
    isValidIndirectLight(light: IndirectLight): boolean;
    isValidMaterial(material: Material): boolean;
    isValidMaterialInstance(material: Material, instance: MaterialInstance): boolean;
    isValidExpensiveMaterialInstance(instance: MaterialInstance): boolean;
    isValidSkybox(skybox: Skybox): boolean;
    isValidColorGrading(colorGrading: ColorGrading): boolean;
    isValidTexture(texture: Texture): boolean;
    isValidRenderTarget(renderTarget: RenderTarget): boolean;
    isValidSwapChain(swapChain: SwapChain): boolean;
}


// ── Renderer ──────────────────────────────────────────────────────────────────

export interface Renderer {
    getUserTime(): number;
    resetUserTime(): void;
    skipNextFrames(frames: number): void;
    getFrameToSkipCount(): number;
    shouldRenderFrame(): boolean;
    setVsyncTime(steadyClockTimeNano: number): void;
    skipFrame(vsyncSteadyClockTimeNano: number): void;
}

// ── View ──────────────────────────────────────────────────────────────────────

export interface View {
    setShadowingEnabled(enabled: boolean): void;
    setFrontFaceWindingInverted(inverted: boolean): void;
    isFrontFaceWindingInverted(): boolean;
    setMaterialGlobal(index: number, value: float4): void;
    getMaterialGlobal(index: number): number[];
    getFogEntity(): Entity;
    clearFrameHistory(engine: Engine): void;
    setDynamicLightingOptions(zLightNear: number, zLightFar: number): void;
}

// ── Scene ─────────────────────────────────────────────────────────────────────

export interface Scene {
    getEntityCount(): number;
    hasEntity(entity: Entity): boolean;
}

// ── Camera ────────────────────────────────────────────────────────────────────

export interface Camera {
    setShift(shift: float2): void;
    getShift(): float2;
}

// ── Material ──────────────────────────────────────────────────────────────────

export interface Material {
    getParameterTransformName(parameter: string): string;
}

// ── MaterialInstance ──────────────────────────────────────────────────────────

export interface MaterialInstance {
    getCullingMode(): CullingMode;
    setCullingModeSeparate(color: CullingMode, shadows: CullingMode): void;
    getShadowCullingMode(): CullingMode;
    getDepthFunc(): CompareFunc;
    setTransparencyMode(mode: TransparencyMode): void;
    getTransparencyMode(): TransparencyMode;
    isColorWriteEnabled(): boolean;
    isDepthCullingEnabled(): boolean;
    isDepthWriteEnabled(): boolean;
    isDoubleSided(): boolean;
    getMaskThreshold(): number;
    getSpecularAntiAliasingThreshold(): number;
    setSpecularAntiAliasingThreshold(value: number): void;
    getSpecularAntiAliasingVariance(): number;
    setSpecularAntiAliasingVariance(value: number): void;
}

// ── RenderableManager ─────────────────────────────────────────────────────────

export interface RenderableManager {
    getBlendOrderAt(instance: RenderableManager$Instance, primitiveIndex: number): number;
    setGlobalBlendOrderEnabledAt(instance: RenderableManager$Instance, primitiveIndex: number, enabled: boolean): void;
    isGlobalBlendOrderEnabledAt(instance: RenderableManager$Instance, primitiveIndex: number): boolean;
    clearMaterialInstanceAt(instance: RenderableManager$Instance, primitiveIndex: number): void;
    getInstanceCount(instance: RenderableManager$Instance): number;
    getPriority(instance: RenderableManager$Instance): number;
    getChannel(instance: RenderableManager$Instance): number;
    setChannel(instance: RenderableManager$Instance, channel: number): void;
    getLightChannel(instance: RenderableManager$Instance, channel: number): boolean;
    setLightChannel(instance: RenderableManager$Instance, channel: number, enable: boolean): void;
    getFogEnabled(instance: RenderableManager$Instance): boolean;
    setFogEnabled(instance: RenderableManager$Instance, enabled: boolean): void;
    isCullingEnabled(instance: RenderableManager$Instance): boolean;
    setCulling(instance: RenderableManager$Instance, enable: boolean): void;
    isScreenSpaceContactShadowsEnabled(instance: RenderableManager$Instance): boolean;
    setScreenSpaceContactShadows(instance: RenderableManager$Instance, enabled: boolean): void;
}

// ── LightManager ──────────────────────────────────────────────────────────────

export interface LightManager {
    getLightChannel(instance: LightManager$Instance, channel: number): boolean;
    setLightChannel(instance: LightManager$Instance, channel: number, enable: boolean): void;
}

// ── TransformManager ──────────────────────────────────────────────────────────

export interface TransformManager {
    getParent(instance: TransformManager$Instance): Entity;
    getChildren(instance: TransformManager$Instance): Vector<Entity>;
}

// ── Texture ───────────────────────────────────────────────────────────────────


// ── RenderTarget — upstream declares these without the attachment argument ─────

export interface RenderTarget {
    getMipLevel(attachment: RenderTarget$AttachmentPoint): number;
    getFace(attachment: RenderTarget$AttachmentPoint): Texture$CubemapFace;
    getLayer(attachment: RenderTarget$AttachmentPoint): number;
    getTexture(attachment: RenderTarget$AttachmentPoint): Texture;
}

// ── Skybox ────────────────────────────────────────────────────────────────────

export interface Skybox {
    setLayerMask(select: number, value: number): void;
    getLayerMask(): number;
    getIntensity(): number;
}


// ── SwapChain ─────────────────────────────────────────────────────────────────


// ── BufferObject ──────────────────────────────────────────────────────────────

export interface BufferObject {
    getByteCount(): number;
    delete(): void;
}

// ── gltfio ────────────────────────────────────────────────────────────────────

export interface gltfio$FilamentAsset {
    getFirstEntityByName(name: string): Entity;
}

export interface gltfio$Animator {
    applyAnimation(index: number, time: number): void;
}

// ── Builders (the d.ts under-reports these too) ───────────────────────────────

export interface RenderableManager$Builder {
    geometryType(type: number): RenderableManager$Builder;
    channel(value: number): RenderableManager$Builder;
    fog(enable: boolean): RenderableManager$Builder;
    lightChannel(channel: number, enable: boolean): RenderableManager$Builder;
    instances(instanceCount: number): RenderableManager$Builder;
    globalBlendOrderEnabled(index: number, enabled: boolean): RenderableManager$Builder;
    screenSpaceContactShadows(enable: boolean): RenderableManager$Builder;
}

export interface LightManager$Builder {
    lightChannel(channel: number, enable: boolean): LightManager$Builder;
}

export interface Skybox$Builder {
    priority(priority: number): Skybox$Builder;
}

export interface BufferObject$Builder {
    // Unlike the other builders, filament.js's loadClassExtensions installs no
    // `.build` wrapper on BufferObject$Builder — only the raw embind `_build` is
    // callable, and it does not auto-delete the builder (callers do so manually).
    _build(engine: Engine): BufferObject;
    delete(): void;
}
