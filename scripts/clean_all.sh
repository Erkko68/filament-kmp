#!/usr/bin/env bash
# Nuke every Gradle / CMake / Kotlin build output in the repo.
# Use when caches get into a bad state (stale CMakeCache, weird "task graph cannot be reused", etc).
#
# What it removes:
#   - Gradle caches & daemon state for this project (./.gradle, build/)
#   - All build/ dirs under kotlin/, java/, samples/, js/, buildSrc/
#   - CMake out-of-tree build dirs under c/build/
#   - Kotlin Native compilation caches local to the project
#   - IDE convergence files that often hold stale paths (.kotlin/)
#
# What it does NOT touch:
#   - prebuilts/ (download is slow — use scripts/download_filament_prebuilts.py if you want fresh)
#   - ~/.gradle/caches (global; rarely the cause; nuking it slows down every other project)
#   - .idea/ (your IDE settings)
#
# Pass --hard to also stop the Gradle daemon and clear ~/.gradle/caches/build-cache-*.

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

HARD=0
for arg in "$@"; do
    case "$arg" in
        --hard) HARD=1 ;;
        -h|--help)
            sed -n '2,18p' "$0"; exit 0 ;;
        *) echo "Unknown arg: $arg"; exit 1 ;;
    esac
done

echo "→ Cleaning Gradle project state in $ROOT"

# Stop the daemon first so it lets go of file locks (esp. on Windows / over network FS).
if command -v ./gradlew >/dev/null 2>&1 || [ -x ./gradlew ]; then
    ./gradlew --stop >/dev/null 2>&1 || true
fi

# Project-local Gradle state
rm -rf .gradle build

# Per-module build dirs
find kotlin java samples js buildSrc -type d -name build -prune -exec rm -rf {} + 2>/dev/null || true
find kotlin java samples js buildSrc -type d -name .gradle -prune -exec rm -rf {} + 2>/dev/null || true

# Kotlin Native / KMP project-local caches
find . -type d -name .kotlin -not -path "./prebuilts/*" -prune -exec rm -rf {} + 2>/dev/null || true

# CMake out-of-tree build dirs used by the C-wrapper / KMP native builds
rm -rf c/build

# IntelliJ / generated bin dirs left behind by older runs
find java -type d -name bin -prune -exec rm -rf {} + 2>/dev/null || true

if [ "$HARD" -eq 1 ]; then
    echo "→ --hard: clearing global Gradle build-cache (may slow next build)"
    rm -rf "$HOME/.gradle/caches/build-cache-"* 2>/dev/null || true
    rm -rf "$HOME/.gradle/caches/transforms-"* 2>/dev/null || true
fi

echo "✓ Done. prebuilts/ kept — run scripts/download_filament_prebuilts.py if you need to refresh those too."
