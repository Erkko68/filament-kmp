#!/usr/bin/env python3
"""
Download the Android Java API sources for a specific Filament version.

Only the Java files needed for API coverage checks are extracted — the full
C++ source tree is skipped to save disk space.

Downloaded sources land in:
    scripts/filament-{version}/

Usage:
    python3 download_filament.py 1.71.0
    python3 download_filament.py 1.72.0 --force    # re-download even if cached
    python3 download_filament.py --list             # show downloaded versions
"""

import argparse
import sys
import tarfile
import urllib.request
from pathlib import Path

SCRIPTS_DIR = Path(__file__).parent
GITHUB_ARCHIVE = "https://github.com/google/filament/archive/refs/tags/v{version}.tar.gz"

# Only extract files inside these Android module paths
ANDROID_JAVA_PATHS = [
    "android/filament-android/src/main/java",
    "android/filamat-android/src/main/java",
    "android/gltfio-android/src/main/java",
    "android/filament-utils-android/src/main/java",
]


def dest_dir(version: str) -> Path:
    return SCRIPTS_DIR / f"filament-{version}"


def is_wanted(member_name: str) -> bool:
    """Return True for Java files inside the Android module paths we care about."""
    # member names look like: filament-1.71.0/android/filament-android/src/...
    # strip the leading "filament-{version}/" prefix
    parts = member_name.split("/", 1)
    if len(parts) < 2:
        return False
    path_in_repo = parts[1]
    return (
        path_in_repo.endswith(".java")
        and any(path_in_repo.startswith(p) for p in ANDROID_JAVA_PATHS)
    )


def download(version: str, force: bool = False) -> Path:
    out = dest_dir(version)
    if out.exists() and not force:
        print(f"Already downloaded: {out.relative_to(SCRIPTS_DIR.parent)}")
        return out

    url = GITHUB_ARCHIVE.format(version=version)
    print(f"Downloading Filament v{version} …")
    print(f"  Source: {url}")

    try:
        response = urllib.request.urlopen(url)
    except Exception as e:
        print(f"ERROR: {e}", file=sys.stderr)
        sys.exit(1)

    out.mkdir(parents=True, exist_ok=True)
    extracted = 0

    print("  Extracting Android Java sources …", flush=True)
    with tarfile.open(fileobj=response, mode="r|gz") as tar:
        for member in tar:
            if not member.isfile():
                continue
            if not is_wanted(member.name):
                continue

            # Strip the leading "filament-{version}/" prefix
            rel = member.name.split("/", 1)[1]
            target = out / rel
            target.parent.mkdir(parents=True, exist_ok=True)

            f = tar.extractfile(member)
            if f:
                target.write_bytes(f.read())
                extracted += 1

    print(f"  Extracted {extracted} Java files → {out.relative_to(SCRIPTS_DIR.parent)}")
    return out


def list_versions():
    versions = sorted(
        d.name.removeprefix("filament-")
        for d in SCRIPTS_DIR.iterdir()
        if d.is_dir() and d.name.startswith("filament-")
    )
    if versions:
        print("Downloaded versions:")
        for v in versions:
            count = sum(1 for _ in dest_dir(v).rglob("*.java"))
            print(f"  {v}  ({count} Java files)")
    else:
        print("No versions downloaded yet.")
        print(f"Run:  python3 {Path(__file__).name} <version>")


def main():
    parser = argparse.ArgumentParser(
        description="Download Filament Android Java API sources for a given version"
    )
    parser.add_argument("version", nargs="?", help="Version to download (e.g. 1.71.0)")
    parser.add_argument("--force", action="store_true",
                        help="Re-download even if already cached")
    parser.add_argument("--list", action="store_true",
                        help="List already-downloaded versions")
    args = parser.parse_args()

    if args.list or args.version is None:
        list_versions()
        return

    download(args.version, force=args.force)


if __name__ == "__main__":
    main()
