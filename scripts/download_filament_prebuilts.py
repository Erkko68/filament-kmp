#!/usr/bin/env python3
"""
Download Filament prebuilt static libraries for one or more native targets.

Tarballs are cached under <repo>/.gradle/filament-prebuilts-cache/ so they
survive ./gradlew clean and are shared across all targets that use the same
asset. Each target's .a / .lib files are extracted into:
    <repo>/prebuilts/<target>/lib/

Usage:
    python3 download_filament_prebuilts.py 1.71.0 iosArm64 macosArm64
    python3 download_filament_prebuilts.py 1.71.0 --all
    python3 download_filament_prebuilts.py 1.71.0 host          # host JVM target
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

# (tarball-asset, path-prefix-inside-tarball, output-subdir-name)
TARGETS = {
    "iosArm64":          ("ios",       "filament/lib/universal"),
    "iosSimulatorArm64": ("ios",       "filament/lib/universal"),
    "iosX64":            ("ios",       "filament/lib/universal"),
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
        tmp = cached.with_suffix(cached.suffix + ".part")
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
    count = extract(tarball, lib_prefix, out_dir)
    print(f"  extracted {count} libs → {out_dir.relative_to(REPO_ROOT)}")


def main() -> None:
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("version", help="Filament version, e.g. 1.71.0")
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
