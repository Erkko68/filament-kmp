#!/usr/bin/env bash
#
# Run the Kotlin Multiplatform test suites across every target this repo
# supports. Mirrors what `.github/workflows/test.yml` does on CI, with two
# exceptions:
#   * The CI `jvm` matrix runs on 4 host OSes (macosArm64 / linuxX64 /
#     linuxArm64 / mingwX64). Locally we just run `jvmTest` on the current
#     host — whichever prebuilt happens to be cached under prebuilts/.
#   * Android tests need a running emulator (or physical device); we try to
#     boot the first AVD that `emulator -list-avds` knows about if `adb
#     devices` shows none, then run `connectedDebugAndroidTest` against it.
#
# Targets (each can be skipped via flag — see below):
#   * jvm     — :kotlin:filament/{filamat,filament-utils,gltfio}:jvmTest
#   * js      — :kotlin:filament/{filamat,filament-utils,gltfio}:jsTest
#                (needs Chrome on PATH for Karma)
#   * ios     — :kotlin:filament/{filamat,filament-utils,gltfio}:iosSimulatorArm64Test
#                (macOS host only; arm64 simulator)
#   * android — connectedDebugAndroidTest on every module
#                (needs ANDROID_HOME, an AVD, adb on PATH)
#
# Usage:
#   scripts/dev/run-tests.sh                 # run everything the host supports
#   scripts/dev/run-tests.sh jvm js          # run just those targets
#   scripts/dev/run-tests.sh --no-android    # skip android (everything else)
#   scripts/dev/run-tests.sh --no-ios        # skip ios (everything else)
#
# Exit code is the OR of every gradle invocation: any failure → non-zero, but
# we run all selected targets before returning so you see every failure.

set -uo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
cd "$REPO_ROOT"

MODULES=(
  ":kotlin:filament"
  ":kotlin:filamat"
  ":kotlin:filament-utils"
  ":kotlin:gltfio"
)

# Defaults: run every target. Disable on non-macOS for iOS.
RUN_JVM=1
RUN_JS=1
RUN_IOS=1
RUN_ANDROID=1
[[ "$(uname -s)" != "Darwin" ]] && RUN_IOS=0

# If specific targets are listed, only run those.
explicit_selection=0
for arg in "$@"; do
  case "$arg" in
    --no-jvm)     RUN_JVM=0 ;;
    --no-js)      RUN_JS=0 ;;
    --no-ios)     RUN_IOS=0 ;;
    --no-android) RUN_ANDROID=0 ;;
    jvm|js|ios|android)
      if [[ $explicit_selection -eq 0 ]]; then
        RUN_JVM=0; RUN_JS=0; RUN_IOS=0; RUN_ANDROID=0
        explicit_selection=1
      fi
      case "$arg" in
        jvm)     RUN_JVM=1 ;;
        js)      RUN_JS=1 ;;
        ios)     RUN_IOS=1 ;;
        android) RUN_ANDROID=1 ;;
      esac
      ;;
    -h|--help)
      sed -n '2,/^$/p' "$0" | sed 's/^# \?//'; exit 0 ;;
    *) echo "Unknown arg: $arg (use -h)" >&2; exit 2 ;;
  esac
done

EXIT=0
run_gradle() {
  local label="$1"; shift
  echo "──────── $label ────────"
  if ! ./gradlew "$@" --no-configuration-cache; then
    echo "✗ $label failed" >&2
    EXIT=1
  else
    echo "✓ $label passed"
  fi
}

# ── JVM ───────────────────────────────────────────────────────────────────────
if [[ $RUN_JVM -eq 1 ]]; then
  tasks=()
  for m in "${MODULES[@]}"; do tasks+=("$m:jvmTest"); done
  run_gradle "jvmTest" "${tasks[@]}"
fi

# ── JS ────────────────────────────────────────────────────────────────────────
if [[ $RUN_JS -eq 1 ]]; then
  tasks=()
  for m in "${MODULES[@]}"; do tasks+=("$m:jsTest"); done
  run_gradle "jsTest" "${tasks[@]}"
fi

# ── iOS (macOS only) ──────────────────────────────────────────────────────────
if [[ $RUN_IOS -eq 1 ]]; then
  if [[ "$(uname -s)" != "Darwin" ]]; then
    echo "Skipping ios: not on macOS" >&2
  else
    tasks=()
    for m in "${MODULES[@]}"; do tasks+=("$m:iosSimulatorArm64Test"); done
    run_gradle "iosSimulatorArm64Test" "${tasks[@]}"
  fi
fi

# ── Android (needs emulator/device) ───────────────────────────────────────────
maybe_boot_emulator() {
  # Returns 0 if a device is visible after this function returns.
  if adb devices 2>/dev/null | awk 'NR>1 && $2=="device"' | grep -q .; then
    return 0
  fi
  local sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
  local emu="$sdk/emulator/emulator"
  [[ -x "$emu" ]] || { echo "Skipping android: no emulator at $emu" >&2; return 1; }
  local avd
  avd="$("$emu" -list-avds 2>/dev/null | head -1)"
  [[ -n "$avd" ]] || { echo "Skipping android: no AVDs available" >&2; return 1; }
  echo "Booting AVD: $avd"
  "$emu" -avd "$avd" -no-snapshot -no-audio -no-boot-anim > /tmp/run-tests-emulator.log 2>&1 &
  local pid=$!
  # Wait up to 120s for boot_completed.
  for _ in $(seq 1 60); do
    sleep 2
    if adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' | grep -q 1; then
      EMULATOR_PID=$pid
      return 0
    fi
  done
  echo "Skipping android: emulator failed to boot in 120s (see /tmp/run-tests-emulator.log)" >&2
  kill "$pid" 2>/dev/null || true
  return 1
}

EMULATOR_PID=""
if [[ $RUN_ANDROID -eq 1 ]]; then
  if ! command -v adb >/dev/null 2>&1; then
    echo "Skipping android: adb not on PATH" >&2
  elif maybe_boot_emulator; then
    tasks=()
    for m in "${MODULES[@]}"; do tasks+=("$m:connectedDebugAndroidTest"); done
    run_gradle "connectedDebugAndroidTest" "${tasks[@]}"
    if [[ -n "$EMULATOR_PID" ]]; then
      # Only kill the emulator we started; leave a pre-existing one alone.
      adb emu kill >/dev/null 2>&1 || true
    fi
  else
    EXIT=1
  fi
fi

echo "────────"
if [[ $EXIT -eq 0 ]]; then
  echo "All selected tests passed."
else
  echo "Some tests failed — see output above." >&2
fi
exit $EXIT
