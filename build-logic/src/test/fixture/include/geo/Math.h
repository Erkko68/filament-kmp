#pragma once

// Plain storage C mirrors as structs of the same layout.
namespace geo::math {

struct float2 { float x = 0, y = 0; };
struct float3 { float x = 0, y = 0, z = 0; };
// A SIMD register: C's mirror of it isn't aligned.
struct alignas(16) float4 { float x = 0, y = 0, z = 0, w = 0; };

} // namespace geo::math
