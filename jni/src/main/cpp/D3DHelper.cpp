#include <jni.h>

#ifdef _WIN32
#define NOMINMAX
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <d3d12.h>
#include <dxgi1_4.h>
#include <wrl/client.h>

// BlueVK defines VK_USE_PLATFORM_WIN32_KHR on Windows, for the Win32 external memory / semaphore structs.
#include <backend/platforms/VulkanPlatform.h>
#include <filament/Engine.h>

#include <cstring>
#include <memory>
#include <mutex>
#include <unordered_set>
#include <vector>

#define D3D_JNI(ret, name) extern "C" JNIEXPORT ret JNICALL Java_io_github_erkko68_filament_jni_D3DHelper_##name

using namespace bluevk;
using filament::backend::Platform;
using filament::backend::VulkanPlatform;
using Microsoft::WRL::ComPtr;

namespace {

// DXGI_FORMAT_R8G8B8A8_UNORM on the D3D12 side.
constexpr VkFormat COLOR_FORMAT = VK_FORMAT_R8G8B8A8_UNORM;
constexpr VkFormat DEPTH_FORMAT = VK_FORMAT_D32_SFLOAT;
constexpr uint32_t IMAGE_COUNT = 2;

constexpr auto D3D12_RESOURCE_HANDLE = VK_EXTERNAL_MEMORY_HANDLE_TYPE_D3D12_RESOURCE_BIT;
constexpr auto D3D12_FENCE_HANDLE = VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_D3D12_FENCE_BIT;

// Beyond what Filament enables on Windows: import D3D12 resources and fences.
constexpr char const* INTEROP_EXTENSIONS[] = {
        VK_KHR_EXTERNAL_MEMORY_EXTENSION_NAME,
        VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME,
        VK_KHR_EXTERNAL_SEMAPHORE_EXTENSION_NAME,
        VK_KHR_EXTERNAL_SEMAPHORE_WIN32_EXTENSION_NAME,
        VK_KHR_TIMELINE_SEMAPHORE_EXTENSION_NAME,
};

// ponytail: skiko 0.150's DirectXDevice (directXRedrawer.cc) starts with its HWND and a
// GrD3DBackendContext, whose first members are the adapter and device. Both are checked before use.
struct SkikoDirectXDevice {
    HWND hWnd;
    IDXGIAdapter1* adapter;
    ID3D12Device* device;
};

// skiko loads D3D12.dll lazily too: linking it would keep libfilament-c from loading where it's missing.
HRESULT createD3D12Device(IUnknown* adapter, ID3D12Device** device) {
    using Create = HRESULT (WINAPI*)(IUnknown*, D3D_FEATURE_LEVEL, REFIID, void**);
    static auto create = [] {
        HMODULE d3d12 = LoadLibraryW(L"d3d12.dll");
        return d3d12 ? (Create) GetProcAddress(d3d12, "D3D12CreateDevice") : nullptr;
    }();
    if (!create) return E_NOTIMPL;
    return create(adapter, D3D_FEATURE_LEVEL_11_0, IID_PPV_ARGS(device));
}

// Filament's "swap chain": images whose memory is a shared D3D12 texture on skiko's device, and a
// shared D3D12 fence signaled with the frame count once the GPU finishes each presented frame.
struct SharedSwapChain : Platform::SwapChain {
    VkDevice device = VK_NULL_HANDLE;
    VkExtent2D extent = {};
    ComPtr<ID3D12Resource> resources[IMAGE_COUNT];
    VkImage colors[IMAGE_COUNT] = {};
    VkDeviceMemory colorMemory[IMAGE_COUNT] = {};
    VkImage depth = VK_NULL_HANDLE;
    VkDeviceMemory depthMemory = VK_NULL_HANDLE;
    ComPtr<ID3D12Fence> fence;
    // A timeline semaphore over [fence].
    VkSemaphore semaphore = VK_NULL_HANDLE;
    // Driver thread only.
    uint64_t presented = 0;
    uint32_t next = 0;

    ~SharedSwapChain() {
        for (uint32_t i = 0; i < IMAGE_COUNT; i++) {
            if (colors[i]) vkDestroyImage(device, colors[i], nullptr);
            if (colorMemory[i]) vkFreeMemory(device, colorMemory[i], nullptr);
        }
        if (depth) vkDestroyImage(device, depth, nullptr);
        if (depthMemory) vkFreeMemory(device, depthMemory, nullptr);
        if (semaphore) vkDestroySemaphore(device, semaphore, nullptr);
    }
};

/**
 * Filament's Vulkan backend can't import textures, but lets a platform supply the swap chain's images:
 * this one renders into D3D12 textures skiko's DirectContext can wrap. It runs on the GPU skiko draws
 * with (matched by LUID), since shared resources don't cross adapters.
 */
class D3DSharedPlatform final : public VulkanPlatform {
public:
    D3DSharedPlatform(ComPtr<ID3D12Device> d3d, LUID luid) : mD3D(std::move(d3d)), mLuid(luid) {}

    bool interopReady() const { return mGpuMatched && mInteropEnabled; }

    // Main thread, once the engine is built.
    SharedSwapChain* createSharedSwapChain(uint32_t width, uint32_t height) {
        auto chain = std::make_unique<SharedSwapChain>();
        chain->device = getDevice();
        chain->extent = { width, height };

        D3D12_HEAP_PROPERTIES heap = {};
        heap.Type = D3D12_HEAP_TYPE_DEFAULT;
        D3D12_RESOURCE_DESC desc = {};
        desc.Dimension = D3D12_RESOURCE_DIMENSION_TEXTURE2D;
        desc.Width = width;
        desc.Height = height;
        desc.DepthOrArraySize = 1;
        desc.MipLevels = 1;
        desc.Format = DXGI_FORMAT_R8G8B8A8_UNORM;
        desc.SampleDesc.Count = 1;
        // Simultaneous access keeps the texture in COMMON between command lists, the state skiko's
        // BackendRenderTarget.makeDirect3D declares.
        desc.Flags = D3D12_RESOURCE_FLAG_ALLOW_RENDER_TARGET | D3D12_RESOURCE_FLAG_ALLOW_SIMULTANEOUS_ACCESS;
        for (uint32_t i = 0; i < IMAGE_COUNT; i++) {
            if (FAILED(mD3D->CreateCommittedResource(&heap, D3D12_HEAP_FLAG_SHARED, &desc,
                    D3D12_RESOURCE_STATE_COMMON, nullptr, IID_PPV_ARGS(&chain->resources[i])))) {
                return nullptr;
            }
            HANDLE handle = nullptr;
            if (FAILED(mD3D->CreateSharedHandle(chain->resources[i].Get(), nullptr, GENERIC_ALL, nullptr, &handle))) {
                return nullptr;
            }
            // Importing an NT handle doesn't take it over.
            const bool imported = importColor(*chain, i, handle);
            CloseHandle(handle);
            if (!imported) return nullptr;
        }
        if (!createDepth(*chain)) return nullptr;

        if (FAILED(mD3D->CreateFence(0, D3D12_FENCE_FLAG_SHARED, IID_PPV_ARGS(&chain->fence)))) return nullptr;
        HANDLE handle = nullptr;
        if (FAILED(mD3D->CreateSharedHandle(chain->fence.Get(), nullptr, GENERIC_ALL, nullptr, &handle))) {
            return nullptr;
        }
        const bool imported = importFence(*chain, handle);
        CloseHandle(handle);
        if (!imported) return nullptr;

        std::lock_guard<std::mutex> lock(mLock);
        mChains.insert(chain.get());
        return chain.release();
    }

    SwapChainPtr createSwapChain(void* nativeWindow, uint64_t flags, VkExtent2D extent) override {
        if (SharedSwapChain* chain = find(nativeWindow)) return chain;
        return VulkanPlatform::createSwapChain(nativeWindow, flags, extent);
    }

    SwapChainBundle getSwapChainBundle(SwapChainPtr handle) override {
        SharedSwapChain* chain = find(handle);
        if (!chain) return VulkanPlatform::getSwapChainBundle(handle);
        SwapChainBundle bundle;
        bundle.colors.reserve(IMAGE_COUNT);
        for (VkImage image : chain->colors) bundle.colors.push_back(image);
        bundle.depth = chain->depth;
        bundle.colorFormat = COLOR_FORMAT;
        bundle.depthFormat = DEPTH_FORMAT;
        bundle.extent = chain->extent;
        return bundle;
    }

    // Round-robin, like Filament's headless swap chain; the caller only renders into an image once
    // skiko is done with its previous frame.
    VkResult acquire(SwapChainPtr handle, ImageSyncData* outImageSyncData) override {
        SharedSwapChain* chain = find(handle);
        if (!chain) return VulkanPlatform::acquire(handle, outImageSyncData);
        outImageSyncData->imageIndex = chain->next;
        outImageSyncData->imageReadySemaphore = VK_NULL_HANDLE;
        chain->next = (chain->next + 1) % IMAGE_COUNT;
        return VK_SUCCESS;
    }

    // Signals the shared fence with the frame count once the frame's commands finish.
    VkResult present(SwapChainPtr handle, uint32_t index, VkSemaphore finishedDrawing) override {
        SharedSwapChain* chain = find(handle);
        if (!chain) return VulkanPlatform::present(handle, index, finishedDrawing);
        const uint64_t value = ++chain->presented;
        const uint64_t binary = 0; // ignored for the binary wait semaphore
        const VkPipelineStageFlags stage = VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
        const bool wait = finishedDrawing != VK_NULL_HANDLE;
        VkTimelineSemaphoreSubmitInfo timeline = {
                .sType = VK_STRUCTURE_TYPE_TIMELINE_SEMAPHORE_SUBMIT_INFO,
                .waitSemaphoreValueCount = wait ? 1u : 0u,
                .pWaitSemaphoreValues = &binary,
                .signalSemaphoreValueCount = 1,
                .pSignalSemaphoreValues = &value,
        };
        // ponytail: no queue-family release to VK_QUEUE_FAMILY_EXTERNAL; D3D12 reads the image
        // in PRESENT_SRC layout, which desktop drivers keep decompressed for presentation engines.
        VkSubmitInfo submit = {
                .sType = VK_STRUCTURE_TYPE_SUBMIT_INFO,
                .pNext = &timeline,
                .waitSemaphoreCount = wait ? 1u : 0u,
                .pWaitSemaphores = &finishedDrawing,
                .pWaitDstStageMask = &stage,
                .signalSemaphoreCount = 1,
                .pSignalSemaphores = &chain->semaphore,
        };
        return vkQueueSubmit(getGraphicsQueue(), 1, &submit, VK_NULL_HANDLE);
    }

    bool hasResized(SwapChainPtr handle) override {
        return find(handle) ? false : VulkanPlatform::hasResized(handle);
    }

    bool isProtected(SwapChainPtr handle) override {
        return find(handle) ? false : VulkanPlatform::isProtected(handle);
    }

    VkResult recreate(SwapChainPtr handle) override {
        return find(handle) ? VK_SUCCESS : VulkanPlatform::recreate(handle);
    }

    void destroy(SwapChainPtr handle) override {
        SharedSwapChain* chain = find(handle);
        if (!chain) {
            VulkanPlatform::destroy(handle);
            return;
        }
        {
            std::lock_guard<std::mutex> lock(mLock);
            mChains.erase(chain);
        }
        delete chain;
    }

protected:
    ExtensionSet getSwapchainInstanceExtensions() const override {
        // VK_KHR_swapchain, which Filament always enables, depends on it.
        return { utils::ImmutableCString(VK_KHR_SURFACE_EXTENSION_NAME) };
    }

    // Only swap chains over shared textures and headless ones are made here: no surfaces.
    SurfaceBundle createVkSurfaceKHR(void*, VkInstance, uint64_t) const noexcept override {
        return { VK_NULL_HANDLE, VkExtent2D{} };
    }

    VkPhysicalDevice selectVkPhysicalDevice(VkInstance instance) noexcept override {
        uint32_t count = 0;
        vkEnumeratePhysicalDevices(instance, &count, nullptr);
        std::vector<VkPhysicalDevice> gpus(count);
        vkEnumeratePhysicalDevices(instance, &count, gpus.data());
        for (VkPhysicalDevice gpu : gpus) {
            VkPhysicalDeviceIDProperties id = { .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ID_PROPERTIES };
            VkPhysicalDeviceProperties2 properties = {
                    .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,
                    .pNext = &id,
            };
            vkGetPhysicalDeviceProperties2(gpu, &properties);
            if (id.deviceLUIDValid && !memcmp(id.deviceLUID, &mLuid, VK_LUID_SIZE)) {
                mGpuMatched = true;
                return gpu;
            }
        }
        // interopReady() reports it; the engine still works for everything but Compose.
        return VulkanPlatform::selectVkPhysicalDevice(instance);
    }

    VkDevice createVkDevice(VkDeviceCreateInfo const& createInfo) noexcept override {
        VkPhysicalDevice gpu = getPhysicalDevice();
        uint32_t count = 0;
        vkEnumerateDeviceExtensionProperties(gpu, nullptr, &count, nullptr);
        std::vector<VkExtensionProperties> supported(count);
        vkEnumerateDeviceExtensionProperties(gpu, nullptr, &count, supported.data());
        auto isSupported = [&](char const* name) {
            for (auto const& ext : supported) {
                if (!strcmp(ext.extensionName, name)) return true;
            }
            return false;
        };

        VkPhysicalDeviceTimelineSemaphoreFeatures timelineSupport = {
                .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES,
        };
        VkPhysicalDeviceFeatures2 features = {
                .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,
                .pNext = &timelineSupport,
        };
        vkGetPhysicalDeviceFeatures2(gpu, &features);

        bool enable = mGpuMatched && timelineSupport.timelineSemaphore;
        for (char const* name : INTEROP_EXTENSIONS) enable = enable && isSupported(name);
        if (!enable) return VulkanPlatform::createVkDevice(createInfo);

        std::vector<char const*> extensions(createInfo.ppEnabledExtensionNames,
                createInfo.ppEnabledExtensionNames + createInfo.enabledExtensionCount);
        for (char const* name : INTEROP_EXTENSIONS) {
            bool present = false;
            for (char const* enabled : extensions) present = present || !strcmp(enabled, name);
            if (!present) extensions.push_back(name);
        }
        VkPhysicalDeviceTimelineSemaphoreFeatures timeline = {
                .sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES,
                .pNext = const_cast<void*>(createInfo.pNext),
                .timelineSemaphore = VK_TRUE,
        };
        VkDeviceCreateInfo info = createInfo;
        info.pNext = &timeline;
        info.enabledExtensionCount = (uint32_t) extensions.size();
        info.ppEnabledExtensionNames = extensions.data();
        VkDevice device = VulkanPlatform::createVkDevice(info);

        mGetMemoryHandleProperties = (PFN_vkGetMemoryWin32HandlePropertiesKHR)
                vkGetDeviceProcAddr(device, "vkGetMemoryWin32HandlePropertiesKHR");
        mImportSemaphore = (PFN_vkImportSemaphoreWin32HandleKHR)
                vkGetDeviceProcAddr(device, "vkImportSemaphoreWin32HandleKHR");
        mInteropEnabled = mImportSemaphore != nullptr;
        return device;
    }

private:
    SharedSwapChain* find(void* handle) {
        std::lock_guard<std::mutex> lock(mLock);
        auto* chain = static_cast<SharedSwapChain*>(static_cast<Platform::SwapChain*>(handle));
        return mChains.count(chain) ? chain : nullptr;
    }

    uint32_t deviceLocalMemoryType(uint32_t typeBits) const {
        VkPhysicalDeviceMemoryProperties properties;
        vkGetPhysicalDeviceMemoryProperties(getPhysicalDevice(), &properties);
        for (uint32_t i = 0; i < properties.memoryTypeCount; i++) {
            if ((typeBits & (1u << i)) &&
                    (properties.memoryTypes[i].propertyFlags & VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT)) {
                return i;
            }
        }
        return UINT32_MAX;
    }

    bool importColor(SharedSwapChain& chain, uint32_t index, HANDLE handle) {
        VkExternalMemoryImageCreateInfo external = {
                .sType = VK_STRUCTURE_TYPE_EXTERNAL_MEMORY_IMAGE_CREATE_INFO,
                .handleTypes = D3D12_RESOURCE_HANDLE,
        };
        VkImageCreateInfo imageInfo = {
                .sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO,
                .pNext = &external,
                .imageType = VK_IMAGE_TYPE_2D,
                .format = COLOR_FORMAT,
                .extent = { chain.extent.width, chain.extent.height, 1 },
                .mipLevels = 1,
                .arrayLayers = 1,
                .samples = VK_SAMPLE_COUNT_1_BIT,
                .tiling = VK_IMAGE_TILING_OPTIMAL,
                // Filament blits from and to swap chain images (readPixels, copyFrame).
                .usage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT |
                         VK_IMAGE_USAGE_TRANSFER_DST_BIT,
        };
        if (vkCreateImage(chain.device, &imageInfo, nullptr, &chain.colors[index]) != VK_SUCCESS) return false;

        VkMemoryRequirements requirements;
        vkGetImageMemoryRequirements(chain.device, chain.colors[index], &requirements);
        uint32_t typeBits = requirements.memoryTypeBits;
        VkMemoryWin32HandlePropertiesKHR handleProperties = {
                .sType = VK_STRUCTURE_TYPE_MEMORY_WIN32_HANDLE_PROPERTIES_KHR,
        };
        if (mGetMemoryHandleProperties &&
                mGetMemoryHandleProperties(chain.device, D3D12_RESOURCE_HANDLE, handle, &handleProperties) == VK_SUCCESS &&
                handleProperties.memoryTypeBits) {
            typeBits &= handleProperties.memoryTypeBits;
        }
        const uint32_t memoryType = deviceLocalMemoryType(typeBits);
        if (memoryType == UINT32_MAX) return false;

        // D3D12 resources import as dedicated allocations.
        VkMemoryDedicatedAllocateInfo dedicated = {
                .sType = VK_STRUCTURE_TYPE_MEMORY_DEDICATED_ALLOCATE_INFO,
                .image = chain.colors[index],
        };
        VkImportMemoryWin32HandleInfoKHR import = {
                .sType = VK_STRUCTURE_TYPE_IMPORT_MEMORY_WIN32_HANDLE_INFO_KHR,
                .pNext = &dedicated,
                .handleType = D3D12_RESOURCE_HANDLE,
                .handle = handle,
        };
        VkMemoryAllocateInfo allocInfo = {
                .sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO,
                .pNext = &import,
                .allocationSize = requirements.size,
                .memoryTypeIndex = memoryType,
        };
        if (vkAllocateMemory(chain.device, &allocInfo, nullptr, &chain.colorMemory[index]) != VK_SUCCESS) {
            return false;
        }
        return vkBindImageMemory(chain.device, chain.colors[index], chain.colorMemory[index], 0) == VK_SUCCESS;
    }

    bool createDepth(SharedSwapChain& chain) {
        VkImageCreateInfo imageInfo = {
                .sType = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO,
                .imageType = VK_IMAGE_TYPE_2D,
                .format = DEPTH_FORMAT,
                .extent = { chain.extent.width, chain.extent.height, 1 },
                .mipLevels = 1,
                .arrayLayers = 1,
                .samples = VK_SAMPLE_COUNT_1_BIT,
                .tiling = VK_IMAGE_TILING_OPTIMAL,
                .usage = VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT |
                         VK_IMAGE_USAGE_TRANSFER_DST_BIT,
        };
        if (vkCreateImage(chain.device, &imageInfo, nullptr, &chain.depth) != VK_SUCCESS) return false;
        VkMemoryRequirements requirements;
        vkGetImageMemoryRequirements(chain.device, chain.depth, &requirements);
        VkMemoryAllocateInfo allocInfo = {
                .sType = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO,
                .allocationSize = requirements.size,
                .memoryTypeIndex = deviceLocalMemoryType(requirements.memoryTypeBits),
        };
        if (allocInfo.memoryTypeIndex == UINT32_MAX) return false;
        if (vkAllocateMemory(chain.device, &allocInfo, nullptr, &chain.depthMemory) != VK_SUCCESS) return false;
        return vkBindImageMemory(chain.device, chain.depth, chain.depthMemory, 0) == VK_SUCCESS;
    }

    bool importFence(SharedSwapChain& chain, HANDLE handle) {
        VkSemaphoreTypeCreateInfo type = {
                .sType = VK_STRUCTURE_TYPE_SEMAPHORE_TYPE_CREATE_INFO,
                .semaphoreType = VK_SEMAPHORE_TYPE_TIMELINE,
                .initialValue = 0,
        };
        VkSemaphoreCreateInfo info = { .sType = VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO, .pNext = &type };
        if (vkCreateSemaphore(chain.device, &info, nullptr, &chain.semaphore) != VK_SUCCESS) return false;
        VkImportSemaphoreWin32HandleInfoKHR import = {
                .sType = VK_STRUCTURE_TYPE_IMPORT_SEMAPHORE_WIN32_HANDLE_INFO_KHR,
                .semaphore = chain.semaphore,
                .handleType = D3D12_FENCE_HANDLE,
                .handle = handle,
        };
        return mImportSemaphore(chain.device, &import) == VK_SUCCESS;
    }

    ComPtr<ID3D12Device> mD3D;
    LUID mLuid;
    bool mGpuMatched = false;
    bool mInteropEnabled = false;
    PFN_vkGetMemoryWin32HandlePropertiesKHR mGetMemoryHandleProperties = nullptr;
    PFN_vkImportSemaphoreWin32HandleKHR mImportSemaphore = nullptr;
    std::mutex mLock;
    std::unordered_set<SharedSwapChain*> mChains;
};

} // namespace

// A platform on skiko's GPU, from Direct3DRedrawer.device and its layer's HWND (to check skiko's
// layout); 0 if skiko's device doesn't look as expected.
D3D_JNI(jlong, nCreatePlatform)(JNIEnv*, jclass, jlong skikoDevice, jlong hwnd) {
    auto* skiko = (SkikoDirectXDevice*) skikoDevice;
    if (!skiko || skiko->hWnd != (HWND) hwnd || !skiko->adapter || !skiko->device) return 0;
    // D3D12 devices are per-adapter singletons: this is skiko's device, if the layout held.
    ComPtr<ID3D12Device> device;
    if (FAILED(createD3D12Device(skiko->adapter, &device)) || device.Get() != skiko->device) return 0;
    return (jlong) new D3DSharedPlatform(device, device->GetAdapterLuid());
}

// Filament doesn't own a platform it's given: nDestroyPlatform once the engine is destroyed.
D3D_JNI(jlong, nCreateEngine)(JNIEnv*, jclass, jlong platform) {
    return (jlong) filament::Engine::Builder()
            .backend(filament::Engine::Backend::VULKAN)
            .platform((D3DSharedPlatform*) platform)
            .build();
}

D3D_JNI(void, nDestroyPlatform)(JNIEnv*, jclass, jlong platform) {
    delete (D3DSharedPlatform*) platform;
}

D3D_JNI(jboolean, nIsInteropReady)(JNIEnv*, jclass, jlong platform) {
    return ((D3DSharedPlatform*) platform)->interopReady();
}

// The native window to create the Filament SwapChain with; Filament's destroySwapChain frees it.
D3D_JNI(jlong, nCreateSwapChain)(JNIEnv*, jclass, jlong platform, jint width, jint height) {
    return (jlong) ((D3DSharedPlatform*) platform)->createSharedSwapChain((uint32_t) width, (uint32_t) height);
}

D3D_JNI(jlong, nResource)(JNIEnv*, jclass, jlong chain, jint index) {
    return (jlong) ((SharedSwapChain*) chain)->resources[index].Get();
}

D3D_JNI(jlong, nCompletedFrames)(JNIEnv*, jclass, jlong chain) {
    return (jlong) ((SharedSwapChain*) chain)->fence->GetCompletedValue();
}
#endif
