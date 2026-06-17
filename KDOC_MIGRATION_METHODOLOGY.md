# KDoc Migration Methodology

## Objective
Migrate C++ header documentation to Kotlin `expect` class declarations while preserving the essence, structure, and meaning of original comments.

## Process

### 1. Locate Kotlin Expect Class
- File: `kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament/`
- Read the `expect` declaration (class/interface/function)
- Note the function/property signature and type constraints

### 2. Find Corresponding C++ Header
- Source: `include/filament/` (root folder)
- Match file name to the Kotlin module (e.g., `Light.kt` → `Light.h`)
- Find the class/function definition that matches the Kotlin expect

### 3. Extract C++ Documentation
- Read all comments above the C++ declaration (Doxygen `///`, `//`, multi-line `/** */`)
- Preserve:
  - Parameter descriptions (translate to `@param` tags)
  - Return value descriptions (translate to `@return` tags)
  - Notes, warnings, constraints
  - Examples if present
  - Enum/constant value meanings
- Discard C++-specific syntax details (pointers, references, `const`, `noexcept`)

### 4. Adapt to KDoc Format
- Use standard Kotlin KDoc format: `/**  ... */`
- Convert parameters: `@param name — description`
- Convert returns: `@return description`
- Convert notes: Wrap in plain text or `*` bullets
- Preserve logical flow and emphasis from original

### 5. Preserve Essence
- Keep technical meaning exactly
- Translate C++ concepts to Kotlin equivalents:
  - `std::vector<T>` → `List<T>`
  - Pointers → nullable types or references
  - Const refs → immutable parameters
  - `bool` → `Boolean`
- Retain all constraints, requirements, and caveats
- Keep examples if they translate logically

### 6. Apply to Expect Declaration
- Place KDoc immediately before `expect` keyword
- Ensure alignment with actual parameter names in Kotlin signature
- Review for clarity and completeness

## Template

```kotlin
/**
 * [One-line summary from C++ header]
 *
 * [Detailed description, if present in C++ header]
 *
 * @param name Description from C++ parameter list
 * @return Description of return value, if applicable
 *
 * @note Any notes or constraints from C++ header
 */
expect class/fun Name(...)
```

## IMPORTANT: Use FULL Comprehensive Documentation

**DO NOT use one-liners or summaries.** Copy the COMPLETE C++ documentation and adapt it:

✗ **WRONG:**
```kotlin
/** Set geometry for a primitive. */
fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder
```

✓ **CORRECT:**
```kotlin
/**
 * Specifies the geometry data for a primitive.
 *
 * Associates a vertex buffer and an index buffer with a primitive. Typically, each
 * primitive is specified with a pair of daisy-chained calls: geometry(...) and
 * material(...).
 *
 * @param index zero-based index of the primitive, must be less than the count passed to Builder constructor
 * @param type specifies the topology of the primitive (e.g., PrimitiveType.TRIANGLES)
 * @param vb specifies the vertex buffer, which in turn specifies a set of attributes
 * @param ib specifies the index buffer (either u16 or u32)
 * @return Builder reference for chaining calls.
 */
fun geometry(index: Int, type: PrimitiveType, vb: VertexBuffer, ib: IndexBuffer): Builder
```

## Quality Checks

- [ ] **FULL documentation copied from C++ header (not summarized)**
- [ ] **Every parameter has @param tag** (no omissions)
- [ ] **Return value has @return tag** (always)
- [ ] **Constraints, defaults, ranges documented** (e.g., "clamped to [0..7]")
- [ ] **Edge cases and warnings preserved** (e.g., "must not", "ignored when")
- [ ] **Cross-references maintained** (translate @see to Kotlin names)
- [ ] Parameter names match Kotlin signature
- [ ] No C++-specific syntax leaked (translate C++ types to Kotlin)
- [ ] Code examples converted to Kotlin syntax where present
- [ ] KDoc format is valid Markdown

## Verification Process (CRITICAL)

After writing KDoc for each file, **always verify against the C++ headers** using this exact process:

### Step 1: Read the complete C++ header file
```bash
cat /Users/eric/IdeaProjects/filament-kmp/include/filament/<ClassName>.h
```
or
```bash
head -400 <file.h>  # for large files
tail -300 <file.h>  # for specific sections
grep -A 20 "methodName" <file.h>  # for specific methods
```

**Why:** The C++ header is the source of truth. Every documentation claim must trace back to the actual C++ code and comments.

### Step 2: Read the current Kotlin file
```kotlin
// Open in IDE or use:
head -200 Kotlin.kt
```

**Why:** Verify what you actually documented, not what you think you documented.

### Step 3: Compare method-by-method

For each method in Kotlin, check:

| Check | How | Fix |
|-------|-----|-----|
| **Parameter names match** | C++ `setParameter(uint32_t left, ...)` → Kotlin `setParameter(left: Int, ...)` | Kotlin names may differ; document Kotlin's actual names |
| **Default values documented** | C++ says "default = 30000" | Add to @param or description |
| **Formulas/math explained** | C++ says "scale * dz + r * constant" | Copy exactly |
| **Constraints stated** | C++ says "clamped to [0..7]" or "must be between 0 and 1" | Document precisely |
| **Defaults for enums** | C++ says "default is KEEP" for stencil ops | Add to description |
| **Missing Kotlin methods** | C++ has `isSampler()` but Kotlin doesn't expose it | Skip; only document what Kotlin exposes |
| **Simplified Kotlin signatures** | C++ has `handler` parameter, Kotlin doesn't | Document Kotlin's actual signature |

### Step 4: Fix discrepancies with Edit tool

When you find a mismatch:

1. Read the exact Kotlin doc string (copy-paste preserving formatting)
2. Read the exact C++ text (copy-paste the relevant section)
3. Rewrite Kotlin doc to match C++ wording while preserving Kotlin specifics
4. Commit with message: `docs(Class): align KDoc precisely with C++ headers`

### Example verification flow

**C++ header says:**
```cpp
void setScissor(uint32_t left, uint32_t bottom, uint32_t width, uint32_t height) noexcept;
/**
 * Set-up a custom scissor rectangle; by default it is disabled.
 *
 * The scissor rectangle gets clipped by the View's viewport, in other words, the scissor
 * cannot affect fragments outside of the View's Viewport.
 */
```

**Kotlin currently has:**
```kotlin
fun setScissor(left: Int, bottom: Int, width: Int, height: Int)
/**
 * Specifies a scissor box to restrict rendering to a rectangular region.
 * Pixels outside the scissor box are discarded.
 */
```

**Fix:**
Update Kotlin doc to: "Set-up a custom scissor rectangle; by default it is disabled. The scissor rectangle gets clipped by the View's viewport..."

### Critical Rules

- **NEVER infer defaults.** Only document what the C++ header explicitly states.
- **NEVER simplify C++ explanations.** Use the exact wording from headers (e.g., "scale * dz + r * constant" not "scale and constant offset").
- **ALWAYS clarify Kotlin-specific changes** (e.g., "parameter broken into x, y, z separate params").
- **ALWAYS check for missing methods** in Kotlin that exist in C++.
- **ALWAYS note fixed-size constraints** (e.g., "3-band SH only, 9 float3 values").
- **Match the tone of C++ docs** - if C++ is terse ("Set the quality..."), don't add fluff.

## Output
- One commit per file/logical group
- Commit message: `docs(moduleName): align KDoc precisely with C++ headers` (or `add` if new)
- Include verification notes in commit when fixing discrepancies

## Unmodified Kotlin files (queue for migration)

✅ Done: Texture.kt, LightManager.kt, Camera.kt, Fence.kt, SurfaceOrientation.kt, Box.kt, NativeSurface.kt, EntityManager.kt, ToneMapper.kt, BufferExtensions.kt

📋 Remaining (14 files, after batch 2):
- **Easiest (small, few methods):**
  - [ ] Entity.kt (type aliases, constants — Kotlin-only, no C++ header)
- **Small-medium:**
  - [ ] BufferExtensions.kt
  - [ ] NativeSurface.kt
  - [ ] TextureSampler.kt
  - [ ] ToneMapper.kt
- **Medium:**
  - [ ] Engine.kt
  - [ ] EntityManager.kt
  - [ ] IndirectLight.kt
  - [ ] Material.kt
  - [ ] MaterialInstance.kt
  - [ ] MorphTargetBuffer.kt
  - [ ] RenderableManager.kt
  - [ ] Renderer.kt
  - [ ] Skybox.kt
  - [ ] Stream.kt
