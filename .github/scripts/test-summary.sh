#!/usr/bin/env bash
#
# Lists the failed tests from the JUnit XML reports in the job summary, so a red job names its
# tests without opening the log. Local check: .github/scripts/test-summary.sh

set -euo pipefail

failed="$(find kotlin desktop \( -path '*/build/test-results/*' -o -path '*/build/outputs/androidTest-results/*' \) \
    -name '*.xml' -exec awk '
    /<testcase / { tc = $0 }
    /<(failure|error)[ >]/ && tc != "" {
        name = tc; sub(/.* name="/, "", name); sub(/".*/, "", name)
        cls = tc; sub(/.* classname="/, "", cls); sub(/".*/, "", cls)
        msg = ""
        if ($0 ~ / message="/) { msg = $0; sub(/.* message="/, "", msg); sub(/".*/, "", msg); msg = ": " substr(msg, 1, 160) }
        print "- `" cls "." name "`" msg
        tc = ""
    }' {} + | sort -u)"

{
    echo "### Failed tests"
    # A crash or a build failure leaves no report entry.
    echo "${failed:-None in the test reports: a build failure or a crashed test process, see the log.}"
} >> "${GITHUB_STEP_SUMMARY:-/dev/stdout}"
