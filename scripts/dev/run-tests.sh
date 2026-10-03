#!/usr/bin/env bash
#
# Run the test suites of every target this repo supports: what `.github/workflows/ci.yml` runs, on this host.
# Each task is its own Gradle invocation with two workers: several Kotlin compilers plus GPU tests at once
# is more than a dev machine takes.
#
# Targets (each can be skipped via flag, see below):
#   * jvm     — :desktop:test, then jvmTest on every module (the host's prebuilt only; CI covers the other OSes)
#   * js      — jsTest on every module and :web (needs Chrome for Karma)
#   * wasm    — wasmJsTest on every module and :web (needs Chrome for Karma)
#   * ios     — iosSimulatorArm64Test on every module (macOS only; boots a simulator)
#   * android — connectedAndroidDeviceTest on every module (boots the first AVD when no device is attached)
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
  ":kotlin:filament-compose"
)

# Defaults: run every target. Disable on non-macOS for iOS.
RUN_JVM=1
RUN_JS=1
RUN_WASM=1
RUN_IOS=1
RUN_ANDROID=1
[[ "$(uname -s)" != "Darwin" ]] && RUN_IOS=0

# If specific targets are listed, only run those.
explicit_selection=0
for arg in "$@"; do
  case "$arg" in
    --no-jvm)     RUN_JVM=0 ;;
    --no-js)      RUN_JS=0 ;;
    --no-wasm)    RUN_WASM=0 ;;
    --no-ios)     RUN_IOS=0 ;;
    --no-android) RUN_ANDROID=0 ;;
    jvm|js|wasm|ios|android)
      if [[ $explicit_selection -eq 0 ]]; then
        RUN_JVM=0; RUN_JS=0; RUN_WASM=0; RUN_IOS=0; RUN_ANDROID=0
        explicit_selection=1
      fi
      case "$arg" in
        jvm)     RUN_JVM=1 ;;
        js)      RUN_JS=1 ;;
        wasm)    RUN_WASM=1 ;;
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
  if ! ./gradlew "$@" --max-workers=2 --no-configuration-cache; then
    echo "✗ $label failed" >&2
    EXIT=1
  else
    echo "✓ $label passed"
  fi
}

# run_tests <task> [gradle args…]: <task> on every module, one invocation each.
run_tests() {
  local task="$1"; shift
  for m in "${MODULES[@]}"; do run_gradle "${m}:${task}" "${m}:${task}" "$@"; done
}

if [[ $RUN_JVM -eq 1 ]]; then
  run_gradle ":desktop:test" :desktop:test
  run_tests jvmTest
fi

if [[ $RUN_JS -eq 1 ]]; then
  run_tests jsTest
  run_gradle ":web:jsTest" :web:jsTest
fi

if [[ $RUN_WASM -eq 1 ]]; then
  run_tests wasmJsTest
  run_gradle ":web:wasmJsTest" :web:wasmJsTest
fi

# ── iOS (macOS only) ──────────────────────────────────────────────────────────
# Real-backend (Metal) tests need a booted simulator with a graphics context;
# Kotlin/Native's default --standalone mode has none. Boot a device and run the
# tests inside it (see filament-kmp-module.gradle.kts standalone=false).
SIM_DEVICE="${IOS_SIM_DEVICE:-iPhone 17}"
maybe_boot_simulator() {
  # Reuse an already-booted iPhone if present.
  local booted
  booted="$(xcrun simctl list devices booted 2>/dev/null | grep -oE 'iPhone[^(]*' | head -1 | sed 's/ *$//')"
  [[ -n "$booted" ]] && SIM_DEVICE="$booted"
  # Fall back to the first available iPhone if the preferred one doesn't exist.
  if ! xcrun simctl list devices available 2>/dev/null | grep -q "$SIM_DEVICE ("; then
    SIM_DEVICE="$(xcrun simctl list devices available 2>/dev/null | grep -oE 'iPhone[^(]*' | head -1 | sed 's/ *$//')"
  fi
  [[ -n "$SIM_DEVICE" ]] || { echo "Skipping ios: no iPhone simulator available" >&2; return 1; }
  echo "Simulator: $SIM_DEVICE"
  # A simulator listed as booted can have a dead session; bootstatus -b boots it if needed and waits (5 min at most:
  # it waits forever on a wedged one, and macOS has no timeout).
  perl -e 'alarm 300; exec @ARGV' xcrun simctl bootstatus "$SIM_DEVICE" -b >/dev/null 2>&1 && return 0
  xcrun simctl shutdown "$SIM_DEVICE" 2>/dev/null || true
  perl -e 'alarm 300; exec @ARGV' xcrun simctl bootstatus "$SIM_DEVICE" -b >/dev/null 2>&1 && return 0
  echo "Skipping ios: simulator '$SIM_DEVICE' failed to boot" >&2
  return 1
}

if [[ $RUN_IOS -eq 1 ]]; then
  if [[ "$(uname -s)" != "Darwin" ]]; then
    echo "Skipping ios: not on macOS" >&2
  elif maybe_boot_simulator; then
    run_tests iosSimulatorArm64Test "-PiosSimulatorDevice=$SIM_DEVICE"
  else
    EXIT=1
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
  "$emu" -avd "$avd" -no-window -no-snapshot -no-audio -no-boot-anim > /tmp/run-tests-emulator.log 2>&1 &
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
    run_tests connectedAndroidDeviceTest
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
