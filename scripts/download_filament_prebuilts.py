#!/usr/bin/env python3
"""
Download Filament prebuilt static libraries for one or more native targets.

Tarballs are cached under <repo>/.gradle/filament-prebuilts-cache/ so they
survive ./gradlew clean and are shared across all targets that use the same
asset. Each target's .a / .lib files are extracted into:
    <repo>/prebuilts/<target>/lib/

Since Filament 1.71.4 the iOS tarball ships xcframeworks: each library lives at
    filament/lib/lib<name>.xcframework/<slice>/lib<name>.a
where <slice> is "ios-arm64" (device) or "ios-arm64_x86_64-simulator" (fat
simulator binary covering both arm64 and x86_64). Targets that reference an
xcframework slice use the "xcf:<slice>" prefix notation in TARGETS below.

Usage:
    python3 download_filament_prebuilts.py 1.71.4 iosArm64 macosArm64
    python3 download_filament_prebuilts.py 1.71.4 --all
    python3 download_filament_prebuilts.py 1.71.4 host          # host JVM target
"""

import argparse
import os
import platform
import sys
import tarfile
import threading
import urllib.request
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CACHE_DIR = REPO_ROOT / ".gradle" / "filament-prebuilts-cache"
PREBUILTS_DIR = REPO_ROOT / "prebuilts"

# (tarball-asset, path-prefix-inside-tarball OR "xcf:<slice-name>")
#
# iOS targets use xcframework slices (since 1.71.4). Both simulator targets
# point to the same fat simulator slice; each gets its own output directory so
# the build system can address them independently.
TARGETS = {
    "iosArm64":          ("ios",       "xcf:ios-arm64"),
    "iosSimulatorArm64": ("ios",       "xcf:ios-arm64_x86_64-simulator"),
    "iosX64":            ("ios",       "xcf:ios-arm64_x86_64-simulator"),
    "macosArm64":        ("mac",       "filament/lib/arm64"),
    "macosX64":          ("mac",       "filament/lib/x86_64"),
    "linuxX64":          ("linux",     "filament/lib/x86_64"),
    "linuxArm64":        ("arm-linux", "filament/lib/aarch64"),
    "mingwX64":          ("windows",   "lib/x86_64/md"),
}

_lock = threading.Lock()


def host_target() -> str:
    sysname = platform.system()
    arch = platform.machine().lower()
    if sysname == "Darwin":
        return "macosArm64" if arch in ("arm64", "aarch64") else "macosX64"
    if sysname == "Linux":
        return "linuxArm64" if arch in ("arm64", "aarch64") else "linuxX64"
    if sysname == "Windows":
        return "mingwX64"
    sys.exit(f"ERROR: unsupported host OS '{sysname}'")


def download_tarball(version: str, suffix: str) -> Path:
    name = f"filament-v{version}-{suffix}.tgz"
    cached = CACHE_DIR / name
    with _lock:
        if cached.exists():
            print(f"  cached: {name}")
            return cached
        CACHE_DIR.mkdir(parents=True, exist_ok=True)
        url = f"https://github.com/google/filament/releases/download/v{version}/{name}"
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


def extract(tarball: Path, lib_prefix: str, out_dir: Path) -> int:
    """Extract .a/.lib files from a flat directory inside the tarball."""
    out_dir.mkdir(parents=True, exist_ok=True)
    prefix = lib_prefix.rstrip("/") + "/"
    n = 0
    with tarfile.open(tarball, "r:gz") as tar:
        for m in tar:
            if not m.isfile():
                continue
            if not m.name.startswith(prefix):
                continue
            base = os.path.basename(m.name)
            if not (base.endswith(".a") or base.endswith(".lib")):
                continue
            f = tar.extractfile(m)
            if f is None:
                continue
            (out_dir / base).write_bytes(f.read())
            n += 1
    return n


def extract_xcframework(tarball: Path, xcf_slice: str, out_dir: Path) -> int:
    """Extract .a files from xcframework-structured iOS tarballs (since 1.71.4).

    Expected path pattern inside the tarball:
        filament/lib/lib<name>.xcframework/<slice>/lib<name>.a
    """
    out_dir.mkdir(parents=True, exist_ok=True)
    n = 0
    with tarfile.open(tarball, "r:gz") as tar:
        for m in tar:
            if not m.isfile():
                continue
            parts = m.name.split("/")
            # Expect exactly: filament / lib / <name>.xcframework / <slice> / <file>
            if (len(parts) == 5 and
                    parts[1] == "lib" and
                    parts[2].endswith(".xcframework") and
                    parts[3] == xcf_slice):
                base = parts[4]
                if base.endswith(".a") or base.endswith(".lib"):
                    f = tar.extractfile(m)
                    if f is None:
                        continue
                    (out_dir / base).write_bytes(f.read())
                    n += 1
    return n


def fetch(version: str, target: str, force: bool) -> None:
    if target not in TARGETS:
        sys.exit(f"ERROR: unknown target '{target}'. Known: {', '.join(TARGETS)}")
    suffix, lib_prefix = TARGETS[target]
    out_dir = PREBUILTS_DIR / target / "lib"

    if out_dir.exists() and any(out_dir.iterdir()) and not force:
        print(f"[{target}] up-to-date ({out_dir.relative_to(REPO_ROOT)})")
        return

    print(f"[{target}]")
    tarball = download_tarball(version, suffix)
    if lib_prefix.startswith("xcf:"):
        count = extract_xcframework(tarball, lib_prefix[4:], out_dir)
    else:
        count = extract(tarball, lib_prefix, out_dir)
    print(f"  extracted {count} libs → {out_dir.relative_to(REPO_ROOT)}")


def main() -> None:
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("version", help="Filament version, e.g. 1.71.4")
    p.add_argument("targets", nargs="*", help="Target names (or 'host' for current JVM host)")
    p.add_argument("--all", action="store_true", help="Fetch all known targets")
    p.add_argument("--force", action="store_true", help="Re-extract even if output is non-empty")
    args = p.parse_args()

    if args.all:
        targets = list(TARGETS.keys())
    else:
        targets = [host_target() if t == "host" else t for t in args.targets]

    if not targets:
        p.error("no targets given (use --all, 'host', or explicit target names)")

    for t in targets:
        fetch(args.version, t, args.force)


if __name__ == "__main__":
    main()
