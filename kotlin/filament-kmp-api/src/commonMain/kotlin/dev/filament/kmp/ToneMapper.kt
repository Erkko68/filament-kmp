package dev.filament.kmp

/**
 * Interface for tone mapping operators. A tone mapping operator, or tone mapper,
 * is responsible for compressing the dynamic range of the rendered scene to a
 * dynamic range suitable for display.
 *
 * In Filament, tone mapping is a color grading step. ToneMapper instances are
 * created and passed to the ColorGrading::Builder to produce a 3D LUT that will
 * be used during post-processing to prepare the final color buffer for display.
 *
 * Filament provides several default tone mapping operators that fall into three
 * categories:
 *
 * <ul>
 * <li>Configurable tone mapping operators</li>
 * <ul>
 *   <li>GenericToneMapper</li>
 *   <li>AgXToneMapper</li>
 * </ul>
 * <li>Fixed-aesthetic tone mapping operators</li>
 * <ul>
 *   <li>ACESToneMapper</li>
 *   <li>ACESLegacyToneMapper</li>
 *   <li>FilmicToneMapper</li>
 *   <li>PBRNeutralToneMapper</li>
 *   <li>GT7ToneMapper</li>
 * </ul>
 * <li>Debug/validation tone mapping operators</li>
 * <ul>
 *   <li>LinearToneMapper</li>
 *   <li>DisplayRangeToneMapper</li>
 * </ul>
 * </ul>
 */
expect open class ToneMapper {
    fun getNativeObject(): Long

    /**
     * Linear tone mapping operator that returns the input color but clamped to
     * the 0..1 range. This operator is mostly useful for debugging.
     */
    class Linear() : ToneMapper

    /**
     * ACES tone mapping operator.
     * This operator is an implementation of the
     * ACES Reference Rendering Transform (RRT) combined with the Output Device
     * Transform (ODT) for sRGB monitors (dim surround, 100 nits).
     */
    class ACES() : ToneMapper

    /**
     * ACES tone mapping operator, modified to match the perceived brightness
     * of FilmicToneMapper. This operator is the same as ACESToneMapper but
     * applies a brightness multiplier of ~1.6 to the input color value to
     * target brighter viewing environments.
     */
    class ACESLegacy() : ToneMapper

    /**
     * "Filmic" tone mapping operator.
     * This tone mapper was designed to
     * approximate the aesthetics of the ACES RRT + ODT for Rec.709
     * and historically Filament's default tone mapping operator. It exists
     * only for backward compatibility purposes and is not otherwise recommended.
     */
    class Filmic() : ToneMapper

    /**
     * Khronos PBR Neutral tone mapping operator.
     * This tone mapper was designed
     * to preserve the appearance of materials across lighting conditions while
     * avoiding artifacts in the highlights in high dynamic range conditions.
     */
    class PBRNeutralToneMapper() : ToneMapper

    /**
     * Gran Turismo 7 tone mapping operator.
     * This tone mapper was designed
     * to preserve the appearance of materials across lighting conditions while
     * avoiding artifacts in the highlights in high dynamic range conditions.
     * This tone mapper targets an SDR paper white value of 250 nits, with a
     * reference luminance of 100 cd/m^2 (a value of 1.0 in the HDR framebuffer).
     */
    class GT7ToneMapper() : ToneMapper

    /**
     * AgX tone mapping operator.
     */
    class Agx(look: AgxLook = AgxLook.NONE) : ToneMapper {
        enum class AgxLook {
            /**
             * Base contrast with no look applied
             */
            NONE,

            /**
             * A punchy and more chroma laden look for sRGB displays
             */
            PUNCHY,

            /**
             * A golden tinted, slightly washed look for BT.1886 displays
             */
            GOLDEN,
        }
    }

    /**
     * Generic tone mapping operator that gives control over the tone mapping
     * curve. This operator can be used to control the aesthetics of the final
     * image. This operator also allows to control the dynamic range of the
     * scene referred values.
     */
    class Generic(
        contrast: Float = 1.55f,
        midGrayIn: Float = 0.18f,
        midGrayOut: Float = 0.215f,
        hdrMax: Float = 10.0f,
    ) : ToneMapper {
        /** Returns the contrast of the curve as a strictly positive value. */
        fun getContrast(): Float

        /** Sets the contrast of the curve, must be > 0.0, values in the range 0.5..2.0 are recommended. */
        fun setContrast(contrast: Float)

        /** Returns the middle gray point for input values as a value between 0.0 and 1.0. */
        fun getMidGrayIn(): Float

        /** Sets the input middle gray, between 0.0 and 1.0. */
        fun setMidGrayIn(midGrayIn: Float)

        /** Returns the middle gray point for output values as a value between 0.0 and 1.0. */
        fun getMidGrayOut(): Float

        /** Sets the output middle gray, between 0.0 and 1.0. */
        fun setMidGrayOut(midGrayOut: Float)

        /** Returns the maximum input value that will map to output white, as a value >= 1.0. */
        fun getHdrMax(): Float

        /** Defines the maximum input value that will be mapped to output white. Must be >= 1.0. */
        fun setHdrMax(hdrMax: Float)
    }
}

