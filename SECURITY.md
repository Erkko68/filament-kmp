# Security Policy

Filament KMP is a **Kotlin Multiplatform wrapper** around
[Google Filament](https://github.com/google/filament). That shapes its security model: the
heavy, memory-unsafe work — rendering, shader/material compilation, backend drivers, and
asset parsing (glTF, KTX, images) — happens inside the **native Filament engine** we ship as
prebuilt binaries, not in this repository. Please read the triage section below before
reporting; it usually decides *where* a vulnerability belongs.

## Supported versions

This project is pre-1.0 and releases frequently, tracking upstream Filament (see
[README → Versioning & stability](README.md#versioning--stability)). Only the **latest
published release** receives security fixes. Older versions are not patched — upgrade to
the newest version first and confirm the issue still reproduces.

| Version | Supported |
| :-- | :-- |
| Latest release | ✅ |
| Older releases | ❌ |

## Is it this wrapper, or Filament?

Same rule of thumb as [CONTRIBUTING.md](CONTRIBUTING.md): *if the same symptom would happen
from C++/JS using Filament directly, it is an engine issue.*

- **Engine vulnerabilities** — crashes, memory corruption, or unsafe parsing triggered by a
  malformed model/texture/material, backend (GL/Metal/Vulkan/WebGPU) faults, or anything
  inside the native engine — should be reported to
  [google/filament](https://github.com/google/filament/security). These reproduce
  independently of Kotlin. We track such issues with the `upstream-filament` label and refresh
  our prebuilts once a fix lands upstream.
- **Wrapper vulnerabilities** belong here. These are the parts we actually own:
  - the hand-written native glue (C wrapper, JNI, Project-Panama/FFM bindings) and the
    Kotlin/JS externals — e.g. incorrect buffer sizing in out-parameters, marshalling bugs,
    or type-unsafe JS interop;
  - the build/prebuilt plumbing — integrity of the Filament binaries downloaded per
    `filaVersion`, and the download scripts under `scripts/`;
  - packaging, signing, and publishing of the released artifacts.

## Reporting a vulnerability

**Do not open a public issue for a security vulnerability.**

Use GitHub's **private vulnerability reporting** for this repository:
[**Report a vulnerability**](https://github.com/Erkko68/filament-kmp/security/advisories/new).
This opens a private advisory visible only to the maintainers.

Please include, as far as you can:

- affected version(s) and platform/target (Android, iOS, JVM/Desktop, Web);
- a minimal reproduction, and whether it also reproduces against Filament directly (this tells
  us if it is a wrapper or an engine issue);
- impact and any known workaround.

## What to expect

- **Acknowledgement** within a few days.
- An assessment of whether the issue is a wrapper bug (fixed here) or an upstream engine bug
  (escalated to Google Filament and tracked via `upstream-filament`).
- A fix or mitigation in the next release once validated, with credit to the reporter unless
  you prefer to remain anonymous.

Because this is a small, pre-1.0 project, timelines are best-effort. Thanks for helping keep
Filament KMP and its users safe.
