#!/usr/bin/env bash
#
# Cross-check upstream Filament's embind-registered JS API against the Kotlin
# externals declared in this repo's `*.js.kt` files.
#
# Why this exists:
#   Filament's `web/filament-js/filament.d.ts` is hand-maintained, drifts behind
#   `jsbindings.cpp` (the *actual* binding source), and historically seeds this
#   repo's `js/src/jsMain/kotlin/filament.js.kt` externals — so missing,
#   wrong-arity, or wrong-return-type method declarations are normal. See
#   patches/README.md and recent fixes in Animator.js.kt, View.js.kt,
#   TransformManager.js.kt, BufferObject.js.kt for the class of bug.
#
# What this checks:
#   For every `class_<X>("JsName")` block in jsbindings.cpp, list every
#   .function(...) / .class_function(...) / .BUILDER_FUNCTION(...). Then check
#   whether each name appears in the corresponding Kotlin actual file
#   (kotlin/*/src/jsMain/.../<JsName>.js.kt — with `$Builder` mapped to a
#   nested `Builder` class inside the parent's file).
#
#   Prints two lists:
#     1. MISSING — bindings present upstream but not referenced anywhere in
#        our `*.js.kt` files. These are stubs you can implement.
#     2. ORPHANED — references in `*.js.kt` to JS function names that don't
#        appear in jsbindings.cpp. Usually false positives from generic
#        Kotlin code, but occasionally catches a removed upstream binding.
#
# Usage:
#   scripts/dev/check-js-bindings.sh                # uses .filament-src-cache
#   scripts/dev/check-js-bindings.sh /path/to/src   # use a specific clone
#   scripts/dev/check-js-bindings.sh --tag v1.71.4  # check a specific tag from cache

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
CACHE_DIR="${SCRIPT_DIR}/.filament-src-cache"
KOTLIN_GLOB="$REPO_ROOT/kotlin/*/src/jsMain"
EXTERNALS_FILE="$REPO_ROOT/js/src/jsMain/kotlin/filament.js.kt"

FILAMENT_SRC=""
TAG=""
while [[ $# -gt 0 ]]; do
  case "$1" in
    --tag) TAG="$2"; shift 2 ;;
    -h|--help) sed -n '2,/^$/p' "$0" | sed 's/^# \?//'; exit 0 ;;
    *) FILAMENT_SRC="$1"; shift ;;
  esac
done

# Resolve where to read jsbindings.cpp from.
JSBINDINGS=""
if [[ -n "$FILAMENT_SRC" ]]; then
  JSBINDINGS="$FILAMENT_SRC/web/filament-js/jsbindings.cpp"
  [[ -f "$JSBINDINGS" ]] || { echo "Not a Filament source tree: $FILAMENT_SRC" >&2; exit 1; }
elif [[ -d "$CACHE_DIR/.git" ]]; then
  [[ -n "$TAG" ]] || TAG="$(grep -E '^filaVersion=' "$REPO_ROOT/gradle.properties" | cut -d= -f2)"
  [[ "$TAG" =~ ^v ]] || TAG="v$TAG"
  # Fetch the tag if missing.
  if ! git -C "$CACHE_DIR" rev-parse --verify --quiet "refs/tags/$TAG" >/dev/null; then
    git -C "$CACHE_DIR" fetch --depth 1 origin "refs/tags/$TAG:refs/tags/$TAG" >&2
  fi
  JSBINDINGS="$(git -C "$CACHE_DIR" show "$TAG:web/filament-js/jsbindings.cpp")"
else
  echo "No Filament source available. Either pass a clone path or run scripts/dev/upgrade-diff.sh once to seed $CACHE_DIR." >&2
  exit 1
fi

# Convert the source (file path or stdin string) into a stream we can grep.
if [[ -f "$JSBINDINGS" ]]; then
  read_jsbindings() { cat "$JSBINDINGS"; }
else
  # JSBINDINGS holds the file contents (from `git show`).
  read_jsbindings() { printf '%s' "$JSBINDINGS"; }
fi

# Extract (JsClassName, FuncName) pairs from jsbindings.cpp.
# embind macros we care about:
#   class_<CppType>("JsName")          ← starts a class block
#   .function("name", ...)
#   .class_function("name", ...)
#   .BUILDER_FUNCTION("name", ...)
# All are followed up to the next class_<...>(...) declaration.
#
# We use awk in single-pass: track current class, emit "Class Func" per
# function. Skip blank "" function names (none in practice). Names starting
# with `_` are embind-internal lambdas that the JS extension layer rewrites
# to the un-prefixed name; strip the leading underscore so the lookup
# matches what Kotlin actually calls (e.g. `_setBuffer` -> `setBuffer`).
PAIRS=$(read_jsbindings | awk '
  /class_<[^>]+>\("[^"]+"\)/ {
    if (match($0, /"[^"]+"/)) {
      cls = substr($0, RSTART+1, RLENGTH-2);
      next
    }
  }
  cls && /\.(function|class_function|BUILDER_FUNCTION)\("[^"]+"/ {
    if (match($0, /"[^"]+"/)) {
      fn = substr($0, RSTART+1, RLENGTH-2);
      sub(/^_/, "", fn);
      if (fn != "")
        printf "%s %s\n", cls, fn;
    }
  }
' | sort -u)

# Collect identifier tokens from the externals file separately from the actuals,
# so we can distinguish "missing from filament.js.kt entirely" (actionable: add a
# declaration) from "declared in externals but no actual references it"
# (informational: a binding upstream exposes that we don't currently use).
EXT_TOKENS="$(grep -ohE '[A-Za-z_][A-Za-z0-9_]*' "$EXTERNALS_FILE" 2>/dev/null | sort -u)"
KT_TOKENS="$(grep -rohE '[A-Za-z_][A-Za-z0-9_]*' $KOTLIN_GLOB --include='*.kt' 2>/dev/null | sort -u)"

MISSING_EXT_COUNT=0
DECLARED_UNUSED_COUNT=0
ORPHANED_COUNT=0

echo "===== MISSING IN EXTERNALS — declare these in filament.js.kt ====="
echo "(upstream binds these but the external Kotlin class doesn't list them;"
echo " adding a declaration lets actuals call them with full type-checking)"
echo

while IFS=' ' read -r cls fn; do
  [[ -z "$cls" ]] && continue
  if ! grep -qx -- "$fn" <<< "$EXT_TOKENS"; then
    printf "  %-40s  %s\n" "$cls" "$fn"
    MISSING_EXT_COUNT=$((MISSING_EXT_COUNT + 1))
  fi
done <<< "$PAIRS"

echo
echo "Found $MISSING_EXT_COUNT missing-from-externals."
echo
echo "===== DECLARED, NOT USED — bindings present in externals but no actual ====="
echo "(informational: bindings available for future use; safe to ignore until needed)"
echo

while IFS=' ' read -r cls fn; do
  [[ -z "$cls" ]] && continue
  if grep -qx -- "$fn" <<< "$EXT_TOKENS" && ! grep -qx -- "$fn" <<< "$KT_TOKENS"; then
    printf "  %-40s  %s\n" "$cls" "$fn"
    DECLARED_UNUSED_COUNT=$((DECLARED_UNUSED_COUNT + 1))
  fi
done <<< "$PAIRS"

echo
echo "Found $DECLARED_UNUSED_COUNT declared-but-unused."
echo
echo "===== ORPHANED — calls in *.js.kt that don't match any jsbindings name  ====="
echo "(usually false positives — locals, Kotlin builtins, JS DOM symbols)"
echo

# Build the set of all upstream-known function names, irrespective of class.
UPSTREAM_NAMES="$(awk '{print $2}' <<< "$PAIRS" | sort -u)"

# Heuristic: every identifier we use that *looks* like a JS method call on
# `jsX.foo(...)` or `jsXBuilder.foo(...)`. Scope the orphan check to those
# patterns; otherwise the volume of false positives is unmanageable.
JS_CALLS="$(grep -rohE 'js[A-Z][A-Za-z]*\.[A-Za-z_][A-Za-z0-9_]*\s*\(' $KOTLIN_GLOB --include='*.kt' 2>/dev/null \
  | sed -E 's/^[^.]+\.([A-Za-z_][A-Za-z0-9_]*).*/\1/' \
  | sort -u)"

while IFS= read -r call; do
  [[ -z "$call" ]] && continue
  if ! grep -qx -- "$call" <<< "$UPSTREAM_NAMES"; then
    # Skip very common false positives — embind's get/set on Vectors, length etc.
    case "$call" in
      size|get|set|toInt|toFloat|toDouble|toLong|toByte|toString|asDynamic|unsafeCast|hashCode|equals|valueOf) continue ;;
    esac
    printf "  %s\n" "$call"
    ORPHANED_COUNT=$((ORPHANED_COUNT + 1))
  fi
done <<< "$JS_CALLS"

echo
echo "Found $ORPHANED_COUNT potentially orphaned references."
echo
if [[ $MISSING_EXT_COUNT -gt 0 ]]; then
  echo "Tip: add the missing declarations to js/src/jsMain/kotlin/filament.js.kt with the"
  echo "correct signature from web/filament-js/jsbindings.cpp. Then the actuals can call"
  echo "them directly without asDynamic()."
fi
