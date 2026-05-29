#!/usr/bin/env python3
"""
Download the jextract tool used to generate the JVM/Panama (FFM) bindings for
the Filament C wrapper.

jextract is an OpenJDK early-access tool published per JDK feature release at
https://jdk.java.net/jextract/ . The build it produces emits code against the
finalized java.lang.foreign API, so any jextract >= 22 works on JDK 22+.

The tarball is cached under <repo>/.gradle/jextract-cache/ (survives
./gradlew clean) and extracted into:
    <repo>/.gradle/jextract/jextract-<major>/bin/jextract

Usage:
    python3 download_jextract.py 25
    python3 download_jextract.py 25 --print-bin   # only print resolved binary path
"""

import argparse
import platform
import ssl
import sys
import tarfile
import urllib.request
from pathlib import Path

# Pinned early-access build coordinates per jextract major version, scraped from
# https://jdk.java.net/jextract/ . Bump alongside the JDK toolchain version.
#   major -> (build, revision)  forming openjdk-<major>-jextract+<build>-<rev>
JEXTRACT_BUILDS = {
    "22": ("6", "47"),
    "25": ("2", "4"),
}

BASE_URL = "https://download.java.net/java/early_access/jextract"

REPO_ROOT = Path(__file__).resolve().parent.parent
CACHE_DIR = REPO_ROOT / ".gradle" / "jextract-cache"
EXTRACT_DIR = REPO_ROOT / ".gradle" / "jextract"


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


def _host_token() -> str:
    sysname = platform.system().lower()
    machine = platform.machine().lower()
    if sysname == "darwin":
        os_tok = "macos"
    elif sysname == "windows":
        os_tok = "windows"
    else:
        os_tok = "linux"
    if machine in ("arm64", "aarch64"):
        arch_tok = "aarch64"
    else:
        arch_tok = "x64"
    # jextract only publishes windows-x64 (no aarch64 Windows build).
    if os_tok == "windows":
        arch_tok = "x64"
    return f"{os_tok}-{arch_tok}"


def jextract_bin(major: str) -> Path:
    exe = "jextract.bat" if platform.system().lower() == "windows" else "jextract"
    return EXTRACT_DIR / f"jextract-{major}" / "bin" / exe


def download(major: str) -> Path:
    if major not in JEXTRACT_BUILDS:
        sys.exit(f"Unknown jextract major '{major}'. Known: {', '.join(JEXTRACT_BUILDS)}")
    build, rev = JEXTRACT_BUILDS[major]
    host = _host_token()
    name = f"openjdk-{major}-jextract+{build}-{rev}_{host}_bin.tar.gz"
    url = f"{BASE_URL}/{major}/{build}/{name}"

    bin_path = jextract_bin(major)
    if bin_path.exists():
        return bin_path

    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    EXTRACT_DIR.mkdir(parents=True, exist_ok=True)
    tarball = CACHE_DIR / name
    if not tarball.exists():
        print(f"Downloading {url}")
        ctx = _make_ssl_context()
        req = urllib.request.Request(url, headers={"User-Agent": "filament-kmp-build"})
        with urllib.request.urlopen(req, context=ctx) as resp, open(tarball, "wb") as out:
            out.write(resp.read())

    print(f"Extracting {tarball.name} -> {EXTRACT_DIR}")
    with tarfile.open(tarball, "r:gz") as tf:
        tf.extractall(EXTRACT_DIR)

    if not bin_path.exists():
        sys.exit(f"jextract binary not found after extraction at {bin_path}")
    return bin_path


def main() -> None:
    ap = argparse.ArgumentParser(description="Download jextract for the FFM bindings.")
    ap.add_argument("major", help="jextract major version (e.g. 25)")
    ap.add_argument("--print-bin", action="store_true",
                    help="Print the resolved jextract binary path and exit.")
    args = ap.parse_args()

    bin_path = jextract_bin(args.major) if args.print_bin else download(args.major)
    if args.print_bin and not bin_path.exists():
        bin_path = download(args.major)
    print(bin_path)


if __name__ == "__main__":
    main()
