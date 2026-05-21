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
import sys
import tarfile
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
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
        with urllib.request.urlopen(url) as resp, tmp.open("wb") as out:
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
    stamp.write_text(args.version + "\n")
    print(f"  extracted {count} headers -> {INCLUDE_DIR.relative_to(REPO_ROOT)}")


if __name__ == "__main__":
    main()
