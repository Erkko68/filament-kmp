#!/usr/bin/env bash
#
# Dev setup utility: download the jextract tool used to generate the JVM/Panama (FFM) bindings
# for the Filament C wrapper.
#
# jextract is an OpenJDK early-access tool published per JDK feature release at
# https://jdk.java.net/jextract/ . The code it generates targets the finalized java.lang.foreign
# API, so any jextract >= 22 works on JDK 22+.
#
# This is a one-time prerequisite — the Gradle build does NOT download jextract; it expects the
# binary to already exist at:
#     <repo>/.gradle/jextract/jextract-<major>/bin/jextract
# Run this once after cloning (and again only when the pinned version changes). The tarball is
# cached under <repo>/.gradle/jextract-cache/ so re-runs are free.
#
# Usage:
#     scripts/dev/download-jextract.sh         # installs the default major (22, what the build uses)
#     scripts/dev/download-jextract.sh 25      # installs a specific major
set -euo pipefail

MAJOR="${1:-22}"

# Pinned early-access build coordinates per jextract major, scraped from
# https://jdk.java.net/jextract/ . Keep in sync with JEXTRACT_MAJOR in buildSrc/FilamentJvmNative.kt.
#   major -> "build revision"  forming openjdk-<major>-jextract+<build>-<rev>
case "$MAJOR" in
  22) BUILD=6; REV=47 ;;
  25) BUILD=2; REV=4 ;;
  *) echo "Unknown jextract major '$MAJOR'. Known: 22, 25." >&2; exit 1 ;;
esac

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
CACHE_DIR="$REPO_ROOT/.gradle/jextract-cache"
EXTRACT_DIR="$REPO_ROOT/.gradle/jextract"
BIN="$EXTRACT_DIR/jextract-$MAJOR/bin/jextract"

if [[ -x "$BIN" ]]; then
  echo "jextract $MAJOR already installed: $BIN"
  exit 0
fi

# Host token: <os>-<arch>. jextract only publishes windows-x64 (no aarch64 Windows build).
case "$(uname -s)" in
  Darwin) OS=macos ;;
  Linux)  OS=linux ;;
  *)      OS=windows ;;
esac
case "$(uname -m)" in
  arm64|aarch64) ARCH=aarch64 ;;
  *)             ARCH=x64 ;;
esac
[[ "$OS" == "windows" ]] && ARCH=x64
HOST="${OS}-${ARCH}"

NAME="openjdk-${MAJOR}-jextract+${BUILD}-${REV}_${HOST}_bin.tar.gz"
URL="https://download.java.net/java/early_access/jextract/${MAJOR}/${BUILD}/${NAME}"

mkdir -p "$CACHE_DIR" "$EXTRACT_DIR"
TARBALL="$CACHE_DIR/$NAME"
if [[ ! -f "$TARBALL" ]]; then
  echo "Downloading $URL"
  curl -fSL --retry 3 -o "$TARBALL" "$URL"
fi

echo "Extracting $NAME -> $EXTRACT_DIR"
tar -xzf "$TARBALL" -C "$EXTRACT_DIR"

if [[ ! -x "$BIN" ]]; then
  echo "jextract binary not found after extraction at $BIN" >&2
  exit 1
fi
echo "Installed jextract $MAJOR: $BIN"
