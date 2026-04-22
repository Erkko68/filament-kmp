#include <jni.h>
#include <vulkan/vulkan.h>
#include <cstdint>
#include <mutex>
#include <unordered_map>

// Maps VkImage handle → {VkDevice, VkDeviceMemory} for cleanup in nReleaseSharedTexture.
struct ImageAlloc { VkDevice device; VkDeviceMemory memory; };
static std::mutex gMapMutex;
static std::unordered_map<VkImage, ImageAlloc> gImageAllocs;

static uint32_t findMemoryType(VkPhysicalDevice physDevice, uint32_t typeBits, VkMemoryPropertyFlags props) {
    VkPhysicalDeviceMemoryProperties memProps;
    vkGetPhysicalDeviceMemoryProperties(physDevice, &memProps);
    for (uint32_t i = 0; i < memProps.memoryTypeCount; i++) {
        if ((typeBits & (1u << i)) && (memProps.memoryTypes[i].propertyFlags & props) == props)
            return i;
    }
    return UINT32_MAX;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateSharedTexture(
        JNIEnv*, jclass,
        jlong devicePtr, jlong physDevicePtr, jint width, jint height) {
    auto device     = reinterpret_cast<VkDevice>(devicePtr);
    auto physDevice = reinterpret_cast<VkPhysicalDevice>(physDevicePtr);
    if (!device || !physDevice) return 0;

    VkImageCreateInfo imageInfo{};
    imageInfo.sType         = VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO;
    imageInfo.imageType     = VK_IMAGE_TYPE_2D;
    imageInfo.format        = VK_FORMAT_B8G8R8A8_UNORM;
    imageInfo.extent        = {(uint32_t)width, (uint32_t)height, 1};
    imageInfo.mipLevels     = 1;
    imageInfo.arrayLayers   = 1;
    imageInfo.samples       = VK_SAMPLE_COUNT_1_BIT;
    imageInfo.tiling        = VK_IMAGE_TILING_OPTIMAL;
    imageInfo.usage         = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
    imageInfo.sharingMode   = VK_SHARING_MODE_EXCLUSIVE;
    imageInfo.initialLayout = VK_IMAGE_LAYOUT_UNDEFINED;

    VkImage image;
    if (vkCreateImage(device, &imageInfo, nullptr, &image) != VK_SUCCESS) return 0;

    VkMemoryRequirements memReq;
    vkGetImageMemoryRequirements(device, image, &memReq);
    uint32_t memType = findMemoryType(physDevice, memReq.memoryTypeBits, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
    if (memType == UINT32_MAX) { vkDestroyImage(device, image, nullptr); return 0; }

    VkMemoryAllocateInfo allocInfo{};
    allocInfo.sType           = VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO;
    allocInfo.allocationSize  = memReq.size;
    allocInfo.memoryTypeIndex = memType;

    VkDeviceMemory memory;
    if (vkAllocateMemory(device, &allocInfo, nullptr, &memory) != VK_SUCCESS) {
        vkDestroyImage(device, image, nullptr);
        return 0;
    }
    vkBindImageMemory(device, image, memory, 0);

    { std::lock_guard<std::mutex> lk(gMapMutex); gImageAllocs[image] = {device, memory}; }
    return reinterpret_cast<jlong>(image);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nReleaseSharedTexture(JNIEnv*, jclass, jlong handle) {
    if (!handle) return;
    auto image = reinterpret_cast<VkImage>(handle);
    ImageAlloc alloc{};
    {
        std::lock_guard<std::mutex> lk(gMapMutex);
        auto it = gImageAllocs.find(image);
        if (it == gImageAllocs.end()) return;
        alloc = it->second;
        gImageAllocs.erase(it);
    }
    vkDestroyImage(alloc.device, image, nullptr);
    vkFreeMemory(alloc.device, alloc.memory, nullptr);
}
