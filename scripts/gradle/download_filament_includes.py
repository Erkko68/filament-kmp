#!/usr/bin/env python3
"""
Download Filament public headers from the GitHub source tarball and sync them
into <repo>/include/.

The header tree must match the version of the prebuilt static libraries in
<repo>/prebuilts/, because some types (e.g. SingleInstanceComponentManager in
1.71.4) underwent ABI breaks across patch versions: building JNI / cinterop
shims against stale headers but linking against newer .a files corrupts memory
layouts at runtime.

We pull from the platform-neutral source tarball
    https://github.com/google/filament/archive/refs/tags/v<version>.tar.gz
rather than any one platform's release tarball. Per-platform release tarballs
ship headers that are functionally identical for our wrapper code (the only
real differences are auto-generated blobs like gltfio/materials/uberarchive.h
and platform-only helpers like filament-matp/ or getopt/, none of which we
include from our wrappers). Pulling from source guarantees no platform bias
and 1:1 reproducibility with the upstream tag.

The tarball is cached under <repo>/.gradle/filament-prebuilts-cache/, shared
with download_filament_prebuilts.py so we don't download twice.

Usage:
    python3 download_filament_includes.py 1.71.4
    python3 download_filament_includes.py 1.71.4 --force
"""

import argparse
import os
import re
import ssl
import sys
import tarfile
import urllib.request
from pathlib import Path

def _make_ssl_context() -> ssl.SSLContext:
    try:
        import certifi
        return ssl.create_default_context(cafile=certifi.where())
    except ImportError:
        pass
    ctx = ssl.create_default_context()
    if sys.platform == "win32":
        for store in ("ROOT", "CA"):
            for cert, _enc, _trust in ssl.enum_certificates(store):  # type: ignore[attr-defined]
                try:
                    ctx.load_verify_locations(cadata=cert)
                except ssl.SSLError:
                    pass
    return ctx


_SSL_CTX = _make_ssl_context()

REPO_ROOT = Path(__file__).resolve().parent.parent.parent
CACHE_DIR = REPO_ROOT / ".gradle" / "filament-prebuilts-cache"
INCLUDE_DIR = REPO_ROOT / "include"

# Source-tarball sub-trees to mirror into <repo>/include/. Each entry is the
# directory inside the source archive whose contents (not the directory itself)
# get copied into include/. So 'libs/utils/include/' contributes utils/,
# generic/, etc. directly under include/.
INCLUDE_SOURCES = [
    "libs/utils/include/",
    "libs/filamat/include/",
    "libs/camutils/include/",
    "libs/filabridge/include/",
    "libs/filaflat/include/",
    "libs/gltfio/include/",
    "libs/ibl/include/",
    "libs/image/include/",
    "libs/imageio-lite/include/",
    "libs/ktxreader/include/",
    "libs/mathio/include/",
    "libs/uberz/include/",
    "libs/viewer/include/",
    "libs/geometry/include/",
    "libs/math/include/",
    "libs/iblprefilter/include/",
    "libs/filameshio/include/",
    "libs/generatePrefilterMipmap/include/",
    "filament/include/",
    "filament/backend/include/",
    # Third-party headers that filament's public API transitively includes.
    "third_party/robin-map/include/",
    "third_party/mikktspace/include/",
    "third_party/getopt/include/",
]


def download_tarball(version: str) -> Path:
    name = f"filament-src-v{version}.tar.gz"
    cached = CACHE_DIR / name
    if cached.exists():
        print(f"  cached: {name}")
        return cached
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    url = f"https://github.com/google/filament/archive/refs/tags/v{version}.tar.gz"
    print(f"  download: {url}")
    tmp = cached.with_name(cached.name + f".{os.getpid()}.part")
    try:
        with urllib.request.urlopen(url, context=_SSL_CTX, timeout=120) as resp, tmp.open("wb") as out:
            total = int(resp.headers.get("Content-Length") or 0)
            downloaded = 0
            while True:
                chunk = resp.read(1 << 16)
                if not chunk:
                    break
                out.write(chunk)
                downloaded += len(chunk)
                if total:
                    pct = downloaded * 100 // total
                    print(f"\r  {pct:3d}%  {downloaded >> 20}/{total >> 20} MB", end="", flush=True)
                else:
                    print(f"\r  {downloaded >> 20} MB", end="", flush=True)
            print()
        tmp.replace(cached)
    except BaseException:
        if tmp.exists():
            tmp.unlink()
        raise
    return cached


def detect_root_prefix(tar: tarfile.TarFile) -> str:
    """GitHub source tarballs are wrapped in a top-level 'filament-<version>/'
    directory. Find and return it (including trailing slash)."""
    for m in tar:
        parts = m.name.split("/", 1)
        if parts[0].startswith("filament-"):
            return parts[0] + "/"
    sys.exit("ERROR: could not detect top-level prefix in source tarball")


def extract_includes(tarball: Path, out_dir: Path) -> int:
    out_dir.mkdir(parents=True, exist_ok=True)
    n = 0
    with tarfile.open(tarball, "r:gz") as tar:
        members = tar.getmembers()
        if not members:
            return 0
        root = members[0].name.split("/", 1)[0] + "/"
        roots = [root + s for s in INCLUDE_SOURCES]
        for m in members:
            if not m.isfile():
                continue
            for src in roots:
                if m.name.startswith(src):
                    rel = m.name[len(src):]
                    if not rel:
                        break
                    dest = out_dir / rel
                    dest.parent.mkdir(parents=True, exist_ok=True)
                    f = tar.extractfile(m)
                    if f is None:
                        break
                    dest.write_bytes(f.read())
                    n += 1
                    break
    return n


# gltfio/materials/uberarchive.h is auto-generated from the per-platform .matc
# bundle and ships ONLY in the per-platform release tarballs (not the source
# tarball we pull headers from). Each platform's copy hardcodes a single
# UBERARCHIVE_DEFAULT_SIZE. java/gltfio/src/main/cpp/MaterialProvider.cpp
# includes the header to know how many bytes to feed to createUbershaderProvider,
# so we synthesize a cross-platform header that picks the right size at compile
# time based on __APPLE__ / TARGET_OS_IPHONE / _WIN32. Sizes are extracted at
# this script's runtime from the matching release tarballs (shared cache with
# download_filament_prebuilts.py), so they re-derive automatically on upgrade.
UBERARCHIVE_TARBALL_PATHS = {
    # tarball-suffix : path inside the tarball
    "mac":       "filament/include/gltfio/materials/uberarchive.h",
    "ios":       "filament/include/gltfio/materials/uberarchive.h",
    "linux":     "filament/include/gltfio/materials/uberarchive.h",
    "windows":   "include/gltfio/materials/uberarchive.h",
}

_SIZE_RE = re.compile(r"#define\s+UBERARCHIVE_DEFAULT_SIZE\s+(\d+)")


def download_platform_tarball(version: str, suffix: str) -> Path:
    name = f"filament-v{version}-{suffix}.tgz"
    cached = CACHE_DIR / name
    if cached.exists():
        return cached
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    url = f"https://github.com/google/filament/releases/download/v{version}/{name}"
    print(f"  download: {url}")
    tmp = cached.with_name(cached.name + f".{os.getpid()}.part")
    try:
        with urllib.request.urlopen(url, context=_SSL_CTX, timeout=120) as resp, tmp.open("wb") as out:
            while True:
                chunk = resp.read(1 << 16)
                if not chunk:
                    break
                out.write(chunk)
        tmp.replace(cached)
    except BaseException:
        if tmp.exists():
            tmp.unlink()
        raise
    return cached


def extract_uberarchive_size(tarball: Path, member_path: str) -> int:
    with tarfile.open(tarball, "r:gz") as tar:
        try:
            m = tar.getmember(member_path)
        except KeyError:
            sys.exit(f"ERROR: '{member_path}' not in {tarball.name}")
        f = tar.extractfile(m)
        if f is None:
            sys.exit(f"ERROR: could not read '{member_path}' from {tarball.name}")
        text = f.read().decode("utf-8", errors="replace")
    match = _SIZE_RE.search(text)
    if not match:
        sys.exit(f"ERROR: UBERARCHIVE_DEFAULT_SIZE not found in {tarball.name}:{member_path}")
    return int(match.group(1))


def write_uberarchive_header(out_dir: Path, sizes: dict[str, int]) -> None:
    # Sizes keyed by tarball suffix. Map each onto a preprocessor branch so the
    # one synthesized header works for every consumer build of this repo.
    dst = out_dir / "gltfio" / "materials" / "uberarchive.h"
    dst.parent.mkdir(parents=True, exist_ok=True)
    dst.write_text(
        "// Generated by scripts/download_filament_includes.py — do not edit.\n"
        "// Sizes are extracted from each platform's per-release tarball.\n"
        "#ifndef UBERARCHIVE_H_\n"
        "#define UBERARCHIVE_H_\n"
        "\n"
        "#include <stdint.h>\n"
        "\n"
        "#if defined(__APPLE__)\n"
        "#include <TargetConditionals.h>\n"
        "#endif\n"
        "\n"
        "extern \"C\" {\n"
        "    extern const uint8_t UBERARCHIVE_PACKAGE[];\n"
        "}\n"
        "\n"
        "#define UBERARCHIVE_DEFAULT_OFFSET 0\n"
        "#if defined(__APPLE__) && TARGET_OS_IPHONE\n"
        f"#define UBERARCHIVE_DEFAULT_SIZE {sizes['ios']}\n"
        "#elif defined(__APPLE__)\n"
        f"#define UBERARCHIVE_DEFAULT_SIZE {sizes['mac']}\n"
        "#elif defined(_WIN32) || defined(_WIN64)\n"
        f"#define UBERARCHIVE_DEFAULT_SIZE {sizes['windows']}\n"
        "#else\n"
        f"#define UBERARCHIVE_DEFAULT_SIZE {sizes['linux']}\n"
        "#endif\n"
        "#define UBERARCHIVE_DEFAULT_DATA (UBERARCHIVE_PACKAGE + UBERARCHIVE_DEFAULT_OFFSET)\n"
        "\n"
        "#endif\n"
    )


def synthesize_uberarchive(version: str, out_dir: Path) -> None:
    print("  uberarchive.h (per-platform sizes):")
    sizes: dict[str, int] = {}
    for suffix, member_path in UBERARCHIVE_TARBALL_PATHS.items():
        tarball = download_platform_tarball(version, suffix)
        size = extract_uberarchive_size(tarball, member_path)
        sizes[suffix] = size
        print(f"    {suffix:8s}  {size}")
    write_uberarchive_header(out_dir, sizes)


def main() -> None:
    p = argparse.ArgumentParser(
        description=__doc__,
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    p.add_argument("version", help="Filament version, e.g. 1.71.4")
    p.add_argument(
        "--force", action="store_true",
        help="Re-extract even if include/ is already stamped at this version",
    )
    args = p.parse_args()

    stamp = INCLUDE_DIR / ".filament-version"
    if stamp.exists() and stamp.read_text().strip() == args.version and not args.force:
        print(f"include/ already at version {args.version} (use --force to re-extract)")
        return

    print(f"[includes v{args.version}]")
    tarball = download_tarball(args.version)
    count = extract_includes(tarball, INCLUDE_DIR)
    synthesize_uberarchive(args.version, INCLUDE_DIR)
    stamp.write_text(args.version + "\n")
    print(f"  extracted {count} headers -> {INCLUDE_DIR.relative_to(REPO_ROOT)}")


if __name__ == "__main__":
    main()
