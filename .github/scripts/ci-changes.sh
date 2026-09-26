#!/usr/bin/env bash
#
# Prints `jvm=… web=… ios=… android=…` (true/false) for ci.yml: which platform jobs the changed
# files need. Any path not listed below runs everything, so a new directory is safe by default.
#
# CI env: EVENT, PR, BEFORE, ONLY (dispatch input), GITHUB_REPOSITORY, GITHUB_SHA, GH_TOKEN.
# Local check: printf '%s\n' docs/x.md c/foo.cpp | .github/scripts/ci-changes.sh -

set -euo pipefail

jvm=false web=false ios=false android=false
all() { jvm=true web=true ios=true android=true; }

classify() {
    case "$1" in
        # Nothing to build.
        *.md|docs/*|LICENSE*|.github/ISSUE_TEMPLATE/*|.github/dependabot.yml) ;;
        .github/workflows/pages.yml|.github/workflows/publish.yml|.github/workflows/status-*) ;;
        scripts/dev/build-wasm-libs.sh|scripts/dev/setup-emsdk.sh) web=true ;;
        scripts/dev/*) ;;
        # c/ is the C API every non-Android target binds to (FFM, cinterop, emcc).
        c/*) jvm=true web=true ios=true ;;
        java/*|kotlin/*/src/jvm*|kotlin/*/api/*|samples/desktopApp/*) jvm=true ;;
        web/*|kotlin/*/src/web*|kotlin/*/src/js*|kotlin/*/src/wasmJs*|samples/webApp/*|gradle/karma/*|kotlin-js-store/*|.github/actions/setup-wasm/*) web=true ;;
        kotlin/*/src/native*|kotlin/*/src/ios*|samples/iosApp/*|samples/shared/src/iosMain/*) ios=true ;;
        kotlin/*/src/android*|samples/androidApp/*) android=true ;;
        *) all ;;
    esac
}

files() {
    case "${EVENT:-}" in
        -) cat ;;
        pull_request) gh api "repos/$GITHUB_REPOSITORY/pulls/$PR/files" --paginate -q '.[].filename' ;;
        # compare lists at most 300 files; a longer push falls back to everything below.
        push) gh api "repos/$GITHUB_REPOSITORY/compare/$BEFORE...$GITHUB_SHA" -q '.files[].filename' ;;
        *) return 1 ;;
    esac
}

[[ "${1:-}" == - ]] && EVENT=-
if [[ -n "${ONLY:-}" ]]; then
    [[ "$ONLY" == all ]] && all || printf -v "$ONLY" true
elif [[ "${BEFORE:-}" =~ ^0+$ ]] || ! list="$(files)" || [[ $(wc -l <<<"$list") -ge 300 ]]; then
    all
else
    while IFS= read -r f; do [[ -n "$f" ]] && classify "$f"; done <<<"$list"
fi

echo "jvm=$jvm"
echo "web=$web"
echo "ios=$ios"
echo "android=$android"
