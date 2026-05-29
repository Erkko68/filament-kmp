#!/usr/bin/env python3
"""
Download the jextract tool used to generate the JVM/Panama (FFM) bindings for
the Filament C wrapper.

jextract is an OpenJDK early-access tool published per JDK feature release at
https://jdk.java.net/jextract/ . The code it generates targets the finalized
java.lang.foreign API, so any jextract >= 22 works on JDK 22+.

This is a one-time prerequisite — the Gradle build does NOT download jextract;
it expects the binary to already exist at:
    <repo>/.gradle/jextract/jextract-<major>/bin/jextract[.bat]
Run this once after cloning (and again only when the pinned version changes).
The tarball is cached under <repo>/.gradle/jextract-cache/ so re-runs are free.

Pure stdlib (no pip), cross-platform (macOS / Linux / Windows) so any developer
— including Windows devs without a bash shell — can run it directly:
    python3 scripts/gradle/download_jextract.py        # default major (22)
    python3 scripts/gradle/download_jextract.py 25     # a specific major
"""

import argparse
import platform
import ssl
import sys
import tarfile
import urllib.request
from pathlib import Path

# Pinned early-access build coordinates per jextract major, scraped from
# https://jdk.java.net/jextract/ . Keep in sync with JEXTRACT_MAJOR in
# buildSrc/src/main/kotlin/FilamentJvmNative.kt.
#   major -> (build, revision)  forming openjdk-<major>-jextract+<build>-<rev>
COORDS = {
    "22": (6, 47),
    "25": (2, 4),
}

REPO_ROOT = Path(__file__).resolve().parent.parent.parent
CACHE_DIR = REPO_ROOT / ".gradle" / "jextract-cache"
EXTRACT_DIR = REPO_ROOT / ".gradle" / "jextract"


def _make_ssl_context() -> ssl.SSLContext:
    # certifi provides a bundled, up-to-date CA store — use it when available.
    try:
        import certifi
        return ssl.create_default_context(cafile=certifi.where())
    except ImportError:
        pass
    ctx = ssl.create_default_context()
    if sys.platform == "win32":
        # Python's embeddable/Store installs on Windows don't load the system
        # root CA store automatically. Pull it from the Windows certificate store.
        for store in ("ROOT", "CA"):
            for cert, _enc, _trust in ssl.enum_certificates(store):  # type: ignore[attr-defined]
                try:
                    ctx.load_verify_locations(cadata=cert)
                except ssl.SSLError:
                    pass
    return ctx


_SSL_CTX = _make_ssl_context()


def host_token() -> str:
    """<os>-<arch>. jextract only publishes windows-x64 (no aarch64 Windows build)."""
    sysname = platform.system()
    os_name = {"Darwin": "macos", "Linux": "linux", "Windows": "windows"}.get(sysname, "linux")
    arch = "aarch64" if platform.machine().lower() in ("arm64", "aarch64") else "x64"
    if os_name == "windows":
        arch = "x64"
    return f"{os_name}-{arch}"


def bin_path(major: str) -> Path:
    name = "jextract.bat" if platform.system() == "Windows" else "jextract"
    return EXTRACT_DIR / f"jextract-{major}" / "bin" / name


def download(url: str, dest: Path) -> None:
    print(f"Downloading {url}")
    dest.parent.mkdir(parents=True, exist_ok=True)
    tmp = dest.with_name(dest.name + ".part")
    try:
        with urllib.request.urlopen(url, context=_SSL_CTX, timeout=120) as resp, tmp.open("wb") as out:
            total = int(resp.headers.get("Content-Length") or 0)
            done = 0
            while True:
                chunk = resp.read(1 << 16)
                if not chunk:
                    break
                out.write(chunk)
                done += len(chunk)
                if total:
                    print(f"\r  {done * 100 // total:3d}%  {done >> 20}/{total >> 20} MB", end="", flush=True)
            print()
        tmp.replace(dest)
    except BaseException:
        if tmp.exists():
            tmp.unlink()
        raise


def main() -> None:
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("major", nargs="?", default="22", help="jextract major version (default: 22, what the build uses)")
    args = p.parse_args()

    major = args.major
    if major not in COORDS:
        sys.exit(f"ERROR: unknown jextract major '{major}'. Known: {', '.join(COORDS)}.")

    binary = bin_path(major)
    if binary.exists():
        print(f"jextract {major} already installed: {binary}")
        return

    build, rev = COORDS[major]
    name = f"openjdk-{major}-jextract+{build}-{rev}_{host_token()}_bin.tar.gz"
    url = f"https://download.java.net/java/early_access/jextract/{major}/{build}/{name}"

    tarball = CACHE_DIR / name
    if not tarball.exists():
        download(url, tarball)
    else:
        print(f"Using cached {tarball.name}")

    EXTRACT_DIR.mkdir(parents=True, exist_ok=True)
    print(f"Extracting {name} -> {EXTRACT_DIR}")
    with tarfile.open(tarball, "r:gz") as tar:
        tar.extractall(EXTRACT_DIR)

    if not binary.exists():
        sys.exit(f"ERROR: jextract binary not found after extraction at {binary}")
    print(f"Installed jextract {major}: {binary}")


if __name__ == "__main__":
    main()
