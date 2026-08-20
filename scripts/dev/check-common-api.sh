#!/usr/bin/env bash
#
# Cross-check the public Filament Android Java API against this repo's
# `commonMain` `expect` declarations. Filament's Android Java API is the
# canonical public surface that the Kotlin Multiplatform API should mirror
# (modulo Kotlin-idiomatic property accessors and a few Android-only types).
#
# What this checks, per module (filament / filamat / gltfio / filament-utils):
#
#   1. CLASS      — every public Java class file has a matching Kotlin
#                   class/interface/object declaration in commonMain.
#   2. NESTED     — every public nested type (class/interface/enum) inside a
#                   Java class exists as an identifier in commonMain.
#   3. CONST      — enum constants and ALL_CAPS public constants exist in
#                   commonMain.
#   4. METHOD     — every public Java method name is referenced in commonMain
#                   (property-bridged: getFoo/setFoo/isFoo ↔ foo).
#   5. FIELD      — every non-private field exists in commonMain. Filament's
#                   option structs (ShadowOptions, FogOptions, Engine.Config)
#                   expose state as bare fields rather than accessors, so
#                   without this the contents of every value struct went
#                   unchecked. Fields prefixed `m` are treated as internal.
#
# Kotlin sources are tokenized with comments stripped first, so a method that
# is only *mentioned in KDoc* no longer counts as covered (it used to).
# Members that are @Deprecated upstream are flagged "(deprecated upstream)" —
# usually fine to skip rather than bind.
#
# Suppressions live in check-common-api-ignores.txt next to this script:
# one entry per line, `ClassName` (whole class) or `ClassName.member`,
# `#` comments allowed. Prefer that file over editing the regexes below.
#
# Usage:
#   scripts/dev/check-common-api.sh                 # uses .filament-src-cache @ filaVersion
#   scripts/dev/check-common-api.sh --tag v1.71.4   # specific tag
#   scripts/dev/check-common-api.sh /path/to/clone  # explicit Filament tree
#
# Exit code: 0 when clean, 1 when anything unsuppressed is missing (CI-able).

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
CACHE_DIR="${SCRIPT_DIR}/.filament-src-cache"
IGNORE_FILE="${SCRIPT_DIR}/check-common-api-ignores.txt"

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
#   - Filament's own member/static field conventions (mFoo, sFoo) — always
#     implementation detail, never public surface
SKIP_NAMES_REGEX='^(n[A-Z]|m[A-Z]|s[A-Z]|nativeObject$|getNativeObject$|finalize$|hashCode$|equals$|toString$|clearNativeObject$|access\$)'

# Java classes we never want to compare. Several of these have cross-platform
# C++ counterparts — the reason to skip them is not "Android-only":
#   - NioUtils, Platform*, NativeSurface, SwapChainFlags, DeviceUtils, Asserts,
#     UsedBy*: JNI/Android infra with no Kotlin equivalent.
#   - TextureHelper, ChoreographerHelper, DisplayHelper, UiHelper, Filament,
#     FilamentHelper: Android Surface/Bitmap/loader glue.
#   - AutomationEngine, RemoteServer (libs/viewer), ImageDiff (libs/imageio):
#     cross-platform C++, but dev/automation tooling rather than rendering API,
#     and absent from our prebuilts.
#   - MathUtils: Java helper over libs/math; we vendor kotlin-math instead.
#   - Entity, EntityInstance: real C++ types (utils/Entity.h), deliberately
#     modelled as Int in common — see docs/platform-notes.md.
SKIP_CLASSES_REGEX='^(NioUtils|AndroidPlatform.*|Platform|NativeSurface|SwapChainFlags|AutomationEngine|ImageDiff|RemoteServer|DeviceUtils|Filament|FilamentHelper|DisplayHelper|UiHelper|TextureHelper|ChoreographerHelper|Asserts|MathUtils|Entity|EntityInstance|UsedByNative|UsedByReflection)$'

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
  list_files() { find "$FILAMENT_SRC/$1" -name '*.java' 2>/dev/null | sed "s|^$FILAMENT_SRC/||"; }
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

TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

# Load suppressions (Class or Class.member entries).
IGNORES="$TMP_DIR/ignores"
if [[ -f "$IGNORE_FILE" ]]; then
  sed 's/#.*//' "$IGNORE_FILE" | tr -d ' \t' | grep -v '^$' > "$IGNORES" || true
else
  : > "$IGNORES"
fi
is_ignored() { grep -qxF -- "$1" "$IGNORES" || grep -qxF -- "${1%%.*}" "$IGNORES"; }

# Strip // and /* */ comments from a C-family/Kotlin source stream.
strip_comments() {
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
  '
}

# Emit "KIND<TAB>name<TAB>deprecated-flag" lines for a Java source on stdin.
# Kinds: NESTED (public nested type), CONST (enum constant / ALL_CAPS public
# constant), FIELD (non-private instance/static field), METHOD (public method).
# Comments are already stripped. A pending @Deprecated marks the next member.
#
# Brace depth is tracked so that FIELD and CONST only fire at class scope —
# without it every local variable in a method body would look like a field.
# Filament's option structs (ShadowOptions, FogOptions, Engine.Config, …)
# expose their state as bare package-private fields, which is why FIELD cannot
# require a `public` modifier the way METHOD does.
extract_java_surface() {
  awk '
    BEGIN { depth = 0; scope[0] = "file"; cname[0] = "" }
    {
      line = $0

      if (line ~ /@Deprecated/) dep = 1

      # Any visibility — scope tracking needs package-private types too.
      # No \b here: the awk shipped on macOS does not implement it.
      declares_type = match(line, /(^|[ \t])(class|interface|enum|@interface)[ \t]+[A-Za-z_][A-Za-z0-9_]*/)
      if (declares_type) {
        decl = substr(line, RSTART, RLENGTH)
        n = split(decl, parts, /[ \t]+/)
        pending_type = 1; pending_name = parts[n]
      }

      if (declares_type && match(line, /(public|protected)[ \t]+([a-z]+[ \t]+)*(class|interface|enum|@interface)[ \t]+[A-Za-z_][A-Za-z0-9_]*/)) {
        decl = substr(line, RSTART, RLENGTH)
        n = split(decl, parts, /[ \t]+/)
        printf "NESTED\t%s\t%d\t%s\n", parts[n], dep, cname[depth]; dep = 0
      }
      # Enum constants / annotation-typedef constants: indented ALL_CAPS
      # identifier immediately followed by "," ";" "(" or "=".
      else if (scope[depth] == "class" && match(line, /^[ \t]+[A-Z][A-Z0-9_]{2,}[ \t]*[,;(=]/)) {
        id = substr(line, RSTART, RLENGTH)
        gsub(/[^A-Z0-9_]/, "", id)
        printf "CONST\t%s\t%d\t%s\n", id, dep, cname[depth]; dep = 0
      }
      # Public methods: permissive about modifiers/return type, require "(".
      else if (match(line, /public[ \t]+(static[ \t]+)?(final[ \t]+)?(synchronized[ \t]+)?(abstract[ \t]+)?(native[ \t]+)?([A-Za-z_][A-Za-z0-9_<>\[\],?@. \t]*[ \t]+)?[A-Za-z_][A-Za-z0-9_]*[ \t]*\(/)) {
        sig = substr(line, RSTART, RLENGTH)
        sub(/[ \t]*\($/, "", sig)
        n = split(sig, parts, /[ \t]+/)
        printf "METHOD\t%s\t%d\t%s\n", parts[n], dep, cname[depth]; dep = 0
      }
      # Fields: `public|protected Type name` terminated by "=" or ";", at
      # class scope, with no "(" on the line (which would make it a method or
      # a constructor call). Public-only, like the METHOD rule: Filament has
      # package-private fields that are public in C++ but deliberately not
      # Android API (LightManager.ShadowOptions.polygonOffsetConstant), and
      # this audit measures against the Java public surface.
      else if (scope[depth] == "class" && line !~ /\(/ && line ~ /(^|[ \t])(public|protected)[ \t]/ &&
               match(line, /^[ \t]*([A-Za-z_][A-Za-z0-9_]*[ \t]+)*[A-Za-z_][A-Za-z0-9_.<>,?]*(\[\])?[ \t]+[a-z][A-Za-z0-9_]*[ \t]*(\[\])?[ \t]*[=;]/)) {
        decl = substr(line, RSTART, RLENGTH)
        sub(/[ \t]*(\[\])?[ \t]*[=;]$/, "", decl)
        n = split(decl, parts, /[ \t]+/)
        printf "FIELD\t%s\t%d\t%s\n", parts[n], dep, cname[depth]; dep = 0
      }

      # Brace accounting, after the emit so a declaration is judged in the
      # scope that contains it. A type declaration claims the next "{".
      rest = line
      while (length(rest) > 0) {
        c = substr(rest, 1, 1)
        if (c == "{") {
          depth++
          scope[depth] = pending_type ? "class" : "block"
          cname[depth] = pending_type ? pending_name : cname[depth - 1]
          pending_type = 0
        }
        else if (c == "}") {
          if (depth > 0) { delete scope[depth]; delete cname[depth]; depth-- }
        }
        rest = substr(rest, 2)
      }
    }
  ' | sort -u
}

# macOS-safe first-char lowercasing (BSD sed has no \L).
lower_first() { printf '%s%s' "$(printf '%s' "${1:0:1}" | tr '[:upper:]' '[:lower:]')" "${1:1}"; }

TOTAL_MISSING=0
TOTAL_DEPRECATED=0

for entry in "${MODULES[@]}"; do
  IFS='|' read -r mod kt_path java_path <<< "$entry"
  kt_full="$REPO_ROOT/$kt_path"

  if [[ ! -d "$kt_full" ]]; then
    echo "[$mod] commonMain path missing ($kt_path) — skipping" >&2
    continue
  fi

  # Tokenize the module's commonMain Kotlin with comments stripped, so a name
  # that only appears in KDoc doesn't count as covered. Dependent modules
  # legitimately reuse core filament types (e.g. filamat's require() takes
  # VertexBuffer.VertexAttribute), so they also get the core token set.
  kt_tokens="$TMP_DIR/tokens-$mod"
  src_dirs=("$kt_full")
  [[ "$mod" != "filament" ]] && src_dirs+=("$REPO_ROOT/kotlin/filament/src/commonMain/kotlin")
  find "${src_dirs[@]}" -name '*.kt' -exec cat {} + 2>/dev/null \
    | strip_comments \
    | grep -ohE '[A-Za-z_][A-Za-z0-9_]*' | sort -u > "$kt_tokens"

  # Kotlin *declared* type names (class/interface/object) — stricter than the
  # token set, used for the class-level check.
  kt_types="$TMP_DIR/types-$mod"
  find "$kt_full" -name '*.kt' -exec cat {} + 2>/dev/null \
    | strip_comments \
    | grep -oE '\b(class|interface|object)[ \t]+[A-Z][A-Za-z0-9_]*' \
    | awk '{print $2}' | sort -u > "$kt_types"

  has_token() { grep -qxF -- "$1" "$kt_tokens"; }

  module_missing=0
  module_deprecated=0
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
    [[ "$classname" =~ $SKIP_CLASSES_REGEX ]] && continue
    is_ignored "$classname" && continue

    content="$(show_file "$jfile" | strip_comments)"
    [[ -z "$content" ]] && continue

    surface="$(printf '%s\n' "$content" | extract_java_surface || true)"

    # 1. Class-level: the top-level Java class should exist as a declared
    #    Kotlin type. If it doesn't, report once and move on — per-member
    #    output for a wholly missing class is noise.
    if ! grep -qxF -- "$classname" "$kt_types"; then
      members=$(printf '%s\n' "$surface" | grep -c . || true)
      if printf '%s\n' "$content" | grep -qE '@Deprecated' ; then depnote=" (deprecated upstream)"; else depnote=""; fi
      printf "  %-18s %s — no Kotlin declaration (%s public members)%s\n" "CLASS" "$classname" "$members" "$depnote"
      module_missing=$((module_missing + 1))
      continue
    fi

    file_out=""
    while IFS=$'\t' read -r kind name dep owner; do
      [[ -z "$name" ]] && continue
      [[ "$name" =~ $SKIP_NAMES_REGEX ]] && continue
      [[ "$name" == "$classname" ]] && continue
      is_ignored "$classname.$name" && continue
      # Members of an ignored nested type are ignored with it: suppressing
      # `Renderer.FrameInfo` has to suppress FrameInfo's fields too.
      if [[ -n "$owner" && "$owner" != "$classname" ]]; then
        is_ignored "$classname.$owner" && continue
        is_ignored "$owner" && continue
        is_ignored "$owner.$name" && continue
      fi
      # Qualify the report when the member comes from a nested type — `View`
      # alone has maxPenumbraRatio in two different option structs.
      label="$name"
      [[ -n "$owner" && "$owner" != "$classname" ]] && label="$owner.$name"

      case "$kind" in
        NESTED|CONST|FIELD)
          has_token "$name" && continue
          ;;
        METHOD)
          # Constructors of nested classes: name matches a declared type in
          # the same file.
          if printf '%s\n' "$content" | grep -qE "\b(class|interface|enum)[ \t]+$name\b"; then
            continue
          fi
          has_token "$name" && continue
          # Property bridge: getFoo/setFoo/isFoo → foo. Boolean setters also
          # bridge to Kotlin's isFoo / isFooEnabled convention
          # (setDepthWrite → isDepthWriteEnabled).
          if [[ "$name" =~ ^(get|set)[A-Z] ]]; then
            stem="${name:3}"
            { has_token "$(lower_first "$stem")" \
              || has_token "is$stem" \
              || has_token "is${stem}Enabled"; } && continue
          elif [[ "$name" =~ ^is[A-Z] ]]; then
            has_token "$(lower_first "${name:2}")" && continue
          fi
          ;;
      esac

      if [[ "$dep" == "1" ]]; then
        file_out="${file_out}    $(printf '%-8s' "$kind") ${label}  (deprecated upstream)"$'\n'
        module_deprecated=$((module_deprecated + 1))
      else
        file_out="${file_out}    $(printf '%-8s' "$kind") ${label}"$'\n'
        module_missing=$((module_missing + 1))
      fi
    done <<< "$surface"

    if [[ -n "$file_out" ]]; then
      printf "  %s\n%s" "$classname" "$file_out"
    fi
  done <<< "$java_files_list"

  echo
  echo "  ($mod) $module_missing missing-from-common, $module_deprecated deprecated-upstream."
  TOTAL_MISSING=$((TOTAL_MISSING + module_missing))
  TOTAL_DEPRECATED=$((TOTAL_DEPRECATED + module_deprecated))
done

echo
echo "================================================================================"
echo "Total missing: $TOTAL_MISSING   (plus $TOTAL_DEPRECATED deprecated-upstream, informational)"
echo "================================================================================"

if [[ $TOTAL_MISSING -gt 0 ]]; then
  echo
  echo "Tip: each entry is public Filament Android API surface with no matching"
  echo "identifier in the KMP commonMain expects. Either:"
  echo "  - add the expect (and the four actuals: jvm/android/native/js), or"
  echo "  - confirm it doesn't apply to KMP and add 'Class', 'Class.member' or
    'Class.NestedType' (which also suppresses that type's members) to"
  echo "    scripts/dev/check-common-api-ignores.txt (with a comment saying why)."
  exit 1
fi
