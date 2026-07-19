#!/usr/bin/env bash
#
# Print everything we care about when bumping filaVersion. Diffs the upstream
# Filament tree at two tags across the surfaces that drive this repo's bindings:
#
#   * Public C++ headers       — drive native, JVM (indirectly), and the Java API.
#   * Backend headers          — drive native shims in c/, and feature-level changes.
#   * Android Java sources     — drive the JVM/Android actuals.
#   * Web JS bindings          — the *real* JS surface. filament.d.ts lags behind
#                                jsbindings.cpp / jsbindings_generated.cpp, so we
#                                diff the bindings sources, not just the d.ts.
#   * Material/engine enums    — material version bumps and CONFIG_MAX_* defaults
#                                (e.g. CONFIG_MAX_INSTANCES for the WebGL UBO
#                                workaround). Behavioral, not just API.
#   * Feature flag defaults    — silent renderer behavior changes (e.g. UBO
#                                batching flipping on/off).
#   * RELEASE_NOTES.md         — Filament uses it for behavior changes that
#                                aren't API breaks.
#
# Usage:
#   scripts/dev/upgrade-diff.sh [<old-tag>] [<new-tag>] [--summary] [--output file]
#
# Tags are optional:
#   no tags     — old = filaVersion from gradle.properties, new = latest
#                 upstream release tag (queried from GitHub).
#   one tag     — old = filaVersion from gradle.properties, new = given tag.
#   two tags    — explicit old and new.
#
# Examples:
#   scripts/dev/upgrade-diff.sh --summary            # current → latest upstream
#   scripts/dev/upgrade-diff.sh v1.74.0              # current → v1.74.0
#   scripts/dev/upgrade-diff.sh v1.71.4 v1.72.0 --summary > upgrade.md
#
# Modes:
#   default     — full unified diffs per area, separated by banner.
#   --summary   — only file-level changes (added / removed / modified). Use
#                 this first to scope the upgrade, then re-run without
#                 --summary on the interesting areas.
#
# Every run starts with a HIGHLIGHTS section: MATERIAL_VERSION bump,
# CONFIG_MAX_* changes, feature-flag default changes, added/removed Android
# Java classes, and added/removed embind JS bindings — the things that have
# historically caused surprises, extracted so they can't be missed in a big
# diff.
#
# The script keeps a shallow git clone of upstream Filament in
# scripts/.filament-src-cache/ and fetches additional tags into it as needed.
# First run is slow (downloads ~200 MB); subsequent runs reuse the cache.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CACHE_DIR="${SCRIPT_DIR}/.filament-src-cache"
REMOTE_URL="https://github.com/google/filament.git"

# Areas to diff, in the order they're printed. Each entry is:
#   "Banner|path1 path2 path3..."
# Paths are relative to the Filament repo root.
AREAS=(
  "Public C++ headers (Filament engine API)|filament/include"
  "Backend headers (drives native shims in c/)|filament/backend/include"
  "Android Java sources (drives JVM/Android actuals)|android/filament-android/src/main/java android/filamat-android/src/main/java android/gltfio-android/src/main/java android/filament-utils-android/src/main/java"
  "Web JS bindings (jsbindings.cpp — the *real* JS surface)|web/filament-js/jsbindings.cpp web/filament-js/jsbindings_generated.cpp web/filament-js/jsbindings_gltfio.cpp web/filament-js/jsenums.cpp web/filament-js/jsenums_generated.cpp"
  "Web JS typed surface (filament.d.ts — lags the bindings)|web/filament-js/filament.d.ts"
  "Material version & engine enums (CONFIG_MAX_*, MATERIAL_VERSION)|libs/filabridge/include/filament/MaterialEnums.h libs/filabridge/include/private/filament/EngineEnums.h libs/filabridge/include/private/filament/UibStructs.h"
  "Feature flag defaults (silent behavior toggles)|libs/utils/include/private/utils/FeatureFlagManager.h libs/utils/src/FeatureFlagManager.cpp"
  "gltfio public headers|libs/gltfio/include"
  "filamat public headers|libs/filamat/include"
  "filament-utils native deps (ktxreader / iblprefilter / camutils)|libs/ktxreader/include libs/iblprefilter/include libs/camutils/include"
  "Release notes (Filament's own narrative)|RELEASE_NOTES.md"
)

usage() {
  sed -n '2,/^$/p' "$0" | sed 's/^# \?//'
  exit "${1:-0}"
}

SUMMARY_ONLY=0
OUTPUT=""
POSITIONAL=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --summary)  SUMMARY_ONLY=1; shift ;;
    --output)   OUTPUT="$2"; shift 2 ;;
    -h|--help)  usage 0 ;;
    -*)         echo "Unknown flag: $1" >&2; usage 1 ;;
    *)          POSITIONAL+=("$1"); shift ;;
  esac
done

REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

current_version() {
  grep -E '^filaVersion=' "$REPO_ROOT/gradle.properties" | cut -d= -f2
}

# Latest upstream release tag (vX.Y.Z only — skips rc/preview tags).
latest_upstream_tag() {
  git ls-remote --tags "$REMOTE_URL" 'refs/tags/v*' \
    | awk -F/ '{print $NF}' \
    | grep -E '^v[0-9]+\.[0-9]+\.[0-9]+$' \
    | awk -F'[v.]' '{printf "%05d%05d%05d %s\n", $2, $3, $4, $0}' \
    | sort | tail -1 | awk '{print $2}'
}

case ${#POSITIONAL[@]} in
  2) OLD_TAG="${POSITIONAL[0]}"; NEW_TAG="${POSITIONAL[1]}" ;;
  1) OLD_TAG="$(current_version)"; NEW_TAG="${POSITIONAL[0]}"
     echo "Old tag defaulted from gradle.properties: v$OLD_TAG" >&2 ;;
  0) OLD_TAG="$(current_version)"
     echo "Old tag defaulted from gradle.properties: v$OLD_TAG" >&2
     echo "Querying latest upstream release tag …" >&2
     NEW_TAG="$(latest_upstream_tag)"
     [[ -n "$NEW_TAG" ]] || { echo "Could not determine latest upstream tag." >&2; exit 1; }
     echo "Latest upstream release: $NEW_TAG" >&2 ;;
  *) echo "Error: at most two tags (old, new)." >&2; usage 1 ;;
esac

# Normalize: accept "1.71.4" or "v1.71.4"
[[ "$OLD_TAG" =~ ^v ]] || OLD_TAG="v$OLD_TAG"
[[ "$NEW_TAG" =~ ^v ]] || NEW_TAG="v$NEW_TAG"

ensure_cache() {
  if [[ ! -d "$CACHE_DIR/.git" ]]; then
    echo "First run: cloning upstream Filament into $CACHE_DIR (this takes a minute) …" >&2
    git clone --filter=blob:none --no-checkout "$REMOTE_URL" "$CACHE_DIR" >&2
  fi
}

ensure_tag() {
  local tag="$1"
  if ! git -C "$CACHE_DIR" rev-parse --verify --quiet "refs/tags/$tag" >/dev/null; then
    echo "Fetching tag $tag …" >&2
    git -C "$CACHE_DIR" fetch --depth 1 origin "refs/tags/$tag:refs/tags/$tag" >&2 \
      || { echo "Could not fetch $tag — does it exist upstream?" >&2; exit 1; }
  fi
}

run_diff() {
  local title="$1"; shift
  local paths=("$@")

  # Filter to paths that actually exist in at least one of the two trees;
  # git diff is happy to report "file vanished" but doesn't like all-missing.
  local existing=()
  for p in "${paths[@]}"; do
    if git -C "$CACHE_DIR" cat-file -e "$OLD_TAG:$p" 2>/dev/null \
       || git -C "$CACHE_DIR" cat-file -e "$NEW_TAG:$p" 2>/dev/null; then
      existing+=("$p")
    fi
  done

  echo
  echo "================================================================================"
  echo "## $title"
  echo "   paths: ${paths[*]}"
  echo "================================================================================"

  if [[ ${#existing[@]} -eq 0 ]]; then
    echo "  (no matching paths exist at either tag)"
    return
  fi

  if [[ $SUMMARY_ONLY -eq 1 ]]; then
    # File-level only: A/M/D plus a per-file insertions/deletions count.
    git -C "$CACHE_DIR" diff --name-status "$OLD_TAG..$NEW_TAG" -- "${existing[@]}" \
      | sed 's/^/  /'
    echo
    git -C "$CACHE_DIR" diff --stat "$OLD_TAG..$NEW_TAG" -- "${existing[@]}" \
      | sed 's/^/  /'
  else
    git -C "$CACHE_DIR" diff "$OLD_TAG..$NEW_TAG" -- "${existing[@]}"
  fi
}

# Extract the things that have historically caused surprises, so they can't
# be missed inside a big diff.
emit_highlights() {
  echo
  echo "================================================================================"
  echo "## HIGHLIGHTS — read this section even if you skim the rest"
  echo "================================================================================"

  # MATERIAL_VERSION bump → every shipped .filamat must be recompiled with the
  # release-tag matc (and note: upstream sometimes repacks UIBs *without*
  # bumping it — see UibStructs.h in the areas below).
  local old_mv new_mv
  old_mv="$(git -C "$CACHE_DIR" show "$OLD_TAG:libs/filabridge/include/filament/MaterialEnums.h" 2>/dev/null | grep -oE 'MATERIAL_VERSION\s*=\s*[0-9]+' | grep -oE '[0-9]+' || true)"
  new_mv="$(git -C "$CACHE_DIR" show "$NEW_TAG:libs/filabridge/include/filament/MaterialEnums.h" 2>/dev/null | grep -oE 'MATERIAL_VERSION\s*=\s*[0-9]+' | grep -oE '[0-9]+' || true)"
  if [[ "$old_mv" != "$new_mv" ]]; then
    echo
    echo "  !! MATERIAL_VERSION: $old_mv → $new_mv — recompile every shipped .filamat with the $NEW_TAG matc."
  else
    echo
    echo "  MATERIAL_VERSION unchanged ($old_mv). Still check UibStructs.h below — repacks without a bump have happened (filament#10188)."
  fi

  echo
  echo "  --- CONFIG_MAX_* / engine-enum constant changes ---"
  git -C "$CACHE_DIR" diff "$OLD_TAG..$NEW_TAG" -- \
      libs/filabridge/include/private/filament/EngineEnums.h \
      libs/filabridge/include/filament/MaterialEnums.h 2>/dev/null \
    | grep -E '^[-+][^-+]' | grep -E 'CONFIG_|MAX_' | sed 's/^/  /' \
    || echo "  (none)"

  echo
  echo "  --- Feature-flag additions / removals / default flips ---"
  git -C "$CACHE_DIR" diff "$OLD_TAG..$NEW_TAG" -- \
      libs/utils/src/FeatureFlagManager.cpp \
      libs/utils/include/private/utils/FeatureFlagManager.h 2>/dev/null \
    | grep -E '^[-+][^-+]' | sed 's/^/  /' \
    || echo "  (none)"

  echo
  echo "  --- Android Java classes added / removed (new binding surface) ---"
  git -C "$CACHE_DIR" diff --name-status --diff-filter=ADR "$OLD_TAG..$NEW_TAG" -- \
      'android/filament-android/src/main/java' 'android/filamat-android/src/main/java' \
      'android/gltfio-android/src/main/java' 'android/filament-utils-android/src/main/java' 2>/dev/null \
    | sed 's/^/  /' | grep . || echo "  (none)"

  echo
  echo "  --- embind JS bindings added / removed (jsbindings*.cpp, jsenums*.cpp) ---"
  git -C "$CACHE_DIR" diff "$OLD_TAG..$NEW_TAG" -- \
      'web/filament-js/jsbindings*.cpp' 'web/filament-js/jsenums*.cpp' 2>/dev/null \
    | grep -E '^[-+].*\.(function|class_function|BUILDER_FUNCTION|value)\(' | sed 's/^/  /' \
    || echo "  (none)"

  echo
  echo "  Follow-ups after applying the bump:"
  echo "    scripts/dev/check-common-api.sh --tag $NEW_TAG    # Android API vs commonMain"
  echo "    scripts/dev/check-js-bindings.sh --tag $NEW_TAG   # jsbindings vs web externals"
}

emit_report() {
  cat <<EOF
Filament upgrade diff: $OLD_TAG → $NEW_TAG
$(date -u +"Generated: %Y-%m-%dT%H:%M:%SZ")
$([ $SUMMARY_ONLY -eq 1 ] && echo "Mode: summary (file-level only — re-run without --summary for full diffs)")

How to read this:
  * "Web JS bindings" is the section most likely to surface methods that the
    auto-generated Kotlin externals (driven by filament.d.ts) don't see. New
    .function("foo", &Class::foo) entries there usually mean an actual JS-side
    method is reachable via .asDynamic() right now.
  * "Material version & engine enums" — any change to MATERIAL_VERSION means
    every shipped .filamat must be recompiled with matc at the new version.
    Changes to CONFIG_MAX_* constants are workaround-relevant (see the WebGL
    instancing UBO bug).
  * "Feature flag defaults" — these flip silently between releases (UBO
    batching has flipped four times in the last year). Worth aligning your
    user-visible defaults afterwards.
  * "Release notes" — Filament's own narrative, often the only place where
    behavior-only changes (no API delta) are written down.
EOF

  emit_highlights

  for area in "${AREAS[@]}"; do
    local title="${area%%|*}"
    local paths_str="${area#*|}"
    read -ra paths <<< "$paths_str"
    run_diff "$title" "${paths[@]}"
  done
}

ensure_cache
ensure_tag "$OLD_TAG"
ensure_tag "$NEW_TAG"

if [[ -n "$OUTPUT" ]]; then
  emit_report > "$OUTPUT"
  echo "Wrote $OUTPUT" >&2
else
  emit_report
fi
