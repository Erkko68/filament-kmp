#!/usr/bin/env bash
#
# Cross-check the public Filament Android Java API against this repo's
# `commonMain` `expect` declarations. Filament's Android Java API is the
# canonical public surface that the Kotlin Multiplatform API should mirror
# (modulo Kotlin-idiomatic property accessors and a few Android-only types).
# This script flags methods present upstream that are missing from common.
#
# Pairs each KMP module to its Android counterpart:
#
#   kotlin/filament/src/commonMain        ↔  android/filament-android
#   kotlin/filamat/src/commonMain         ↔  android/filamat-android
#   kotlin/gltfio/src/commonMain          ↔  android/gltfio-android
#   kotlin/filament-utils/src/commonMain  ↔  android/filament-utils-android
#
# For each class file in Android, find public method names and check whether
# the corresponding identifier is referenced (declaration or property) in any
# `.kt` file in the matching commonMain tree. Kotlin properties bridge
# `getFoo()`/`setFoo(...)` automatically, so `foo` matching either is fine.
#
# Usage:
#   scripts/dev/check-common-api.sh                 # uses .filament-src-cache @ filaVersion
#   scripts/dev/check-common-api.sh --tag v1.71.4   # specific tag
#   scripts/dev/check-common-api.sh /path/to/clone  # explicit Filament tree
#
# Output: per-module list of public Java methods absent from common Kotlin
# code, plus a summary count. Same caveats as check-js-bindings.sh — token-
# level matching, so a method that exists only inside a comment will be
# considered "covered" (rare in practice).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CACHE_DIR="${SCRIPT_DIR}/.filament-src-cache"

# Module map: KMP path  ↔  Android subpath inside the Filament source tree.
declare -a MODULES=(
  "filament|kotlin/filament/src/commonMain/kotlin|android/filament-android/src/main/java/com/google/android/filament"
  "filamat|kotlin/filamat/src/commonMain/kotlin|android/filamat-android/src/main/java/com/google/android/filament/filamat"
  "gltfio|kotlin/gltfio/src/commonMain/kotlin|android/gltfio-android/src/main/java/com/google/android/filament/gltfio"
  "filament-utils|kotlin/filament-utils/src/commonMain/kotlin|android/filament-utils-android/src/main/java/com/google/android/filament/utils"
)

# Java method names we never want to flag:
#   - JNI plumbing (n*, native objects, nativeObject, getNativeObject, finalize)
#   - Object overrides (hashCode, equals, toString)
#   - Internal lifecycle (clearNativeObject)
#   - Kotlin/Java accessor noise
SKIP_NAMES_REGEX='^(n[A-Z]|nativeObject$|getNativeObject$|finalize$|hashCode$|equals$|toString$|clearNativeObject$|access\$)'

# Java classes we never want to compare (Android-only / not applicable to KMP):
#   - NioUtils, AndroidPlatform, NativeSurface, SwapChainFlags: infra plumbing
#   - Stream: Android SurfaceTexture
#   - AutomationEngine, ImageDiff, RemoteServer: Android testing/CI tooling
#   - DeviceUtils: Android device introspection
#   - Filament: Android library loader init
SKIP_CLASSES_REGEX='^(NioUtils|AndroidPlatform|NativeSurface|SwapChainFlags|Stream|AutomationEngine|ImageDiff|RemoteServer|DeviceUtils|Filament|DisplayHelper|UiHelper)$'

FILAMENT_SRC=""
TAG=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --tag) TAG="$2"; shift 2 ;;
    -h|--help) sed -n '2,/^$/p' "$0" | sed 's/^# \?//'; exit 0 ;;
    *) FILAMENT_SRC="$1"; shift ;;
  esac
done

# Resolve where to read Android sources from. Either a passed-in tree, or
# the cached clone at the current filaVersion (or an explicit --tag).
if [[ -n "$FILAMENT_SRC" ]]; then
  [[ -d "$FILAMENT_SRC/android" ]] || { echo "Not a Filament tree: $FILAMENT_SRC" >&2; exit 1; }
  show_file() { cat "$FILAMENT_SRC/$1"; }
  list_files() { find "$FILAMENT_SRC/$1" -name '*.java' 2>/dev/null; }
else
  [[ -d "$CACHE_DIR/.git" ]] || { echo "No Filament cache. Seed it with scripts/dev/upgrade-diff.sh first." >&2; exit 1; }
  if [[ -z "$TAG" ]]; then
    TAG="$(grep -E '^filaVersion=' "$REPO_ROOT/gradle.properties" | cut -d= -f2)"
  fi
  [[ "$TAG" =~ ^v ]] || TAG="v$TAG"
  if ! git -C "$CACHE_DIR" rev-parse --verify --quiet "refs/tags/$TAG" >/dev/null; then
    echo "Fetching $TAG …" >&2
    git -C "$CACHE_DIR" fetch --depth 1 origin "refs/tags/$TAG:refs/tags/$TAG" >&2
  fi
  show_file() { git -C "$CACHE_DIR" show "$TAG:$1" 2>/dev/null; }
  list_files() {
    git -C "$CACHE_DIR" ls-tree -r --name-only "$TAG" "$1" 2>/dev/null | grep '\.java$' || true
  }
fi

# Extract every `public` method name from a Java file. We accept some noise —
# annotated returns, generic wrappers, etc. — by being permissive about what
# comes before the name, and require an opening paren after.
extract_java_methods() {
  # Strip /* ... */ comments and // ... line comments before matching, so
  # commented-out signatures don't pollute the list.
  awk '
    BEGIN { incomment = 0 }
    {
      line = $0
      if (incomment) {
        if (idx = index(line, "*/")) { incomment = 0; line = substr(line, idx + 2) } else next
      }
      while ((idx = index(line, "/*")) > 0) {
        rest = substr(line, idx + 2)
        if (eidx = index(rest, "*/")) {
          line = substr(line, 1, idx - 1) substr(rest, eidx + 2)
        } else {
          line = substr(line, 1, idx - 1); incomment = 1; break
        }
      }
      sub(/\/\/.*/, "", line)
      print line
    }
  ' \
  | grep -oE 'public\s+(static\s+)?(final\s+)?(synchronized\s+)?(abstract\s+)?(native\s+)?([A-Za-z_][A-Za-z0-9_<>\[\],?@.\s]*\s+)?[A-Za-z_][A-Za-z0-9_]*\s*\(' \
  | sed -E 's/.*[[:space:]]([A-Za-z_][A-Za-z0-9_]*)\s*\(.*/\1/' \
  | grep -vE "$SKIP_NAMES_REGEX" \
  | sort -u
}

# Print classification.
TOTAL_MISSING=0

for entry in "${MODULES[@]}"; do
  IFS='|' read -r mod kt_path java_path <<< "$entry"
  kt_full="$REPO_ROOT/$kt_path"

  if [[ ! -d "$kt_full" ]]; then
    echo "[$mod] commonMain path missing ($kt_path) — skipping" >&2
    continue
  fi

  # Collect every identifier token from the module's commonMain Kotlin files.
  kt_tokens="$(grep -rohE '[A-Za-z_][A-Za-z0-9_]*' "$kt_full" --include='*.kt' 2>/dev/null | sort -u)"

  # Discover every Java file in the matching Android tree.
  module_missing=0
  echo
  echo "================================================================================"
  echo "## $mod   (Android: $java_path)"
  echo "================================================================================"

  java_files_list="$(list_files "$java_path")"
  if [[ -z "$java_files_list" ]]; then
    echo "  (no Java sources found at $java_path for tag ${TAG:-<custom>})"
    continue
  fi

  while IFS= read -r jfile; do
    [[ -z "$jfile" ]] && continue
    classname="$(basename "$jfile" .java)"
    # Skip Android-only / infra classes.
    if [[ "$classname" =~ $SKIP_CLASSES_REGEX ]]; then
      continue
    fi
    # Inner classes (e.g. RenderableManager$Bone in compiled form, but Java
    # source treats them as nested classes — we still want to compare).

    # Extract method names from this Java file. `|| true` keeps `set -e`
    # happy when a file has no public methods at all (e.g. constants-only).
    methods="$(show_file "$jfile" 2>/dev/null | extract_java_methods || true)"
    [[ -z "$methods" ]] && continue

    # Filter to only methods that aren't represented in the commonMain
    # token set. Property-bridged matches are accepted: getFoo / setFoo
    # become foo, so we strip the get/set prefix lowercase and check that
    # candidate too.
    file_missing_str=""
    file_missing_count=0
    while IFS= read -r m; do
      [[ -z "$m" ]] && continue
      # Skip Java constructors — name matches the (outer or inner) class.
      # `classname` here is the *file* name. To also skip inner-class
      # constructors (e.g. View.InternalOnPickCallback) we check whether
      # the method name appears anywhere as a `class NAME` declaration in
      # the same file.
      if show_file "$jfile" 2>/dev/null | grep -qE "\bclass\s+$m\b|\binterface\s+$m\b|\benum\s+$m\b"; then
        continue
      fi
      if grep -qx -- "$m" <<< "$kt_tokens"; then
        continue
      fi
      # Property bridge: getFoo/setFoo → foo
      if [[ "$m" =~ ^(get|set)([A-Z]) ]]; then
        prop="$(echo "${m:3}" | sed -E 's/^(.)/\L\1/')"
        if [[ -n "$prop" ]] && grep -qx -- "$prop" <<< "$kt_tokens"; then
          continue
        fi
      fi
      file_missing_str="${file_missing_str}    ${m}"$'\n'
      file_missing_count=$((file_missing_count + 1))
    done <<< "$methods"

    if [[ $file_missing_count -gt 0 ]]; then
      printf "  %s\n%s" "$classname" "$file_missing_str"
      module_missing=$((module_missing + file_missing_count))
    fi
  done <<< "$java_files_list"

  echo
  echo "  ($mod) $module_missing missing-from-common."
  TOTAL_MISSING=$((TOTAL_MISSING + module_missing))
done

echo
echo "================================================================================"
echo "Total missing: $TOTAL_MISSING"
echo "================================================================================"

if [[ $TOTAL_MISSING -gt 0 ]]; then
  echo
  echo "Tip: each entry is a public Filament Android method that has no matching"
  echo "identifier in the KMP commonMain expects. Either:"
  echo "  - add an 'expect fun ...' (and the four actuals: jvm/android/native/js), or"
  echo "  - confirm it's genuinely Android-only and add the class/method to the skip"
  echo "    regexes at the top of this script."
fi
