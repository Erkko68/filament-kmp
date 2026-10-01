package io.github.erkko68.filament.compose.testutils

import kotlin.io.encoding.Base64

/**
 * A tiny KTX1 IBL and skybox, made by Filament 1.77.1's `cmgen --format=ktx --size=8` from an 8×4
 * flat-color HDR: small enough to inline, real enough for `rememberKTXEnvironment`.
 */
object TestKtx {
    val ibl: ByteArray by lazy {
        Base64.decode(
            "q0tUWCAxMbsNChoKAQIDBDqMAAABAAAABxkAADqMAAA6jAAACAAAAAgAAAAAAAAAAAAAAAYAAAAEAAAAFAEAAA4BAABzaAAxLjAwMzkxIDAuNT" +
            "AzOTA2IDAuMjUzOTA2Ci0zLjMyMTQ0ZS0wOSAxLjMwNDc2ZS0wOSAzLjUwOTQyZS0xMAowIDAgMAotMi4zNzI1MmUtMDkgLTEuMTYyNDFlLTA5" +
            "IC03LjA2ODE1ZS0xMAowIDAgMAotMi44MTA3NGUtMDkgLTEuOTk0NDVlLTA5IC0yLjM3NjQ2ZS0wOQotMi4wMTUzMmUtMDggLTEuMDA3NjZlLT" +
            "A4IDcuNTU3NDdlLTA5Ci02LjI5Nzg5ZS0xMCAtMS41NzQ0N2UtMDkgNC43MjM0MmUtMTAKLTEuMjUzMDllLTA4IC02LjQwODIzZS0wOSA0Ljgw" +
            "NDA1ZS0xMQoAAAABAADAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtc" +
            "aMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowA" +
            "tcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xo" +
            "wAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMAL" +
            "XGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtc" +
            "aMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowA" +
            "tcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xo" +
            "wAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMAL" +
            "XGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtc" +
            "aMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGhAAAAAwAtcaMALXGjAC1xowA" +
            "tcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xo" +
            "wAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xoEAAAAMADHGjAAxxowAscaMADHGjAAxxowAMcaMADHGjAAxxowAMcaMAD" +
            "HGjAAxxowAMcaMADHGjAAxxowAMcaMADHGjAAxxowAMcaMADHGjACxxowAMcaMADHGjAAxxowAMcaAQAAADAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGg=",
        )
    }

    val skybox: ByteArray by lazy {
        Base64.decode(
            "q0tUWCAxMbsNChoKAQIDBDqMAAABAAAABxkAADqMAAA6jAAACAAAAAgAAAAAAAAAAAAAAAYAAAABAAAAAAAAAAABAADAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMAL" +
            "XGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtc" +
            "aMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowA" +
            "tcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xo" +
            "wAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMAL" +
            "XGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaM" +
            "ALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtc" +
            "aMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowA" +
            "tcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xo" +
            "wAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1" +
            "xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjA" +
            "C1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXG" +
            "jAC1xowAtcaMALXGjAC1xowAtcaMALXGjAC1xowAtcaMALXGg=",
        )
    }
}
