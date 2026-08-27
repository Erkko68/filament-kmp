package io.github.erkko68.filament

/**
 * Properties of the display this [Renderer] presents to, used for frame pacing.
 *
 * @param refreshRate Refresh rate of the display in Hz. Set to 0 for offscreen rendering or to
 *   disable frame pacing. Default: 60.
 */
data class DisplayInfo(
    var refreshRate: Float = 60.0f,
)

/**
 * Frame rate control and dynamic resolution scaling options.
 *
 * @param interval Desired frame interval in multiples of the refresh period
 *   (1 / [DisplayInfo.refreshRate]). Set to 1 to render at the display refresh rate. Default: 1.
 * @param headRoomRatio Additional headroom for the GPU as a ratio of the target frame time,
 *   covering constant costs like post-processing or GPU work from other views. Default: 0.
 * @param scaleRate Rate at which the system reacts to load changes, as a ratio of the target
 *   frame time. Default: 1/15.
 * @param history Number of frames the system averages load over. Default: 15.
 */
data class FrameRateOptions(
    var interval: Float = 1.0f,
    var headRoomRatio: Float = 0.0f,
    var scaleRate: Float = 1.0f / 15.0f,
    var history: Int = 15,
)

/**
 * How the SwapChain is cleared and discarded at the start of a frame.
 *
 * @param clearColor Linear RGBA colour the SwapChain is cleared to. Default: transparent black.
 * @param clear Whether the SwapChain should be cleared using [clearColor]. Default: false.
 * @param discard Whether the SwapChain content should be discarded. Set false to preserve
 *   existing content. Default: true.
 */
data class ClearOptions(
    var clearColor: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0, 0.0),
    var clear: Boolean = false,
    var discard: Boolean = true,
) {
    // `clearColor` is an array, whose generated equality is by reference; compare contents.
    private fun key() = listOf(clearColor.toList(), clear, discard)
    override fun equals(other: Any?) = this === other || (other is ClearOptions && key() == other.key())
    override fun hashCode() = key().hashCode()
}
