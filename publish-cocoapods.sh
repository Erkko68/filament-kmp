#!/bin/bash
set -e

# Parse versions from gradle.properties
FILA_VERSION=$(grep "filaVersion" kotlin/gradle.properties | cut -d'=' -f2)
LIB_VERSION=$(grep "libVersion" kotlin/gradle.properties | cut -d'=' -f2)
VERSION="${FILA_VERSION}-${LIB_VERSION}"

# Or allow overriding via argument
VERSION=${1:-$VERSION}
REPO_URL="https://github.com/Erkko68/filament-kmp"
RELEASE_URL="${REPO_URL}/releases/download/v${VERSION}"

echo "🚀 Starting CocoaPods distribution build for version ${VERSION}"

# 1. Build XCFrameworks
echo "📦 Building XCFrameworks..."
./kotlin/gradlew -p kotlin :filament:assembleFilamentReleaseXCFramework \
          :filamat:assembleFilamatReleaseXCFramework \
          :filament-utils:assembleFilamentUtilsReleaseXCFramework

# 2. Setup output directory
OUTPUT_DIR="build/cocoapods-dist"
rm -rf "$OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR"

# 3. Zip XCFrameworks
function package_xcframework() {
    local module=$1
    local name=$2
    local framework_path="kotlin/${module}/build/XCFrameworks/release/${name}.xcframework"
    
    echo "  - Packaging ${name} from ${module}..."
    if [ -d "$framework_path" ]; then
        (cd "$(dirname "$framework_path")" && zip -r "../../../../../build/cocoapods-dist/${name}.xcframework.zip" "${name}.xcframework")
    else
        echo "  ⚠️ Error: ${framework_path} not found!"
        exit 1
    fi
}

# Note: Adjusting paths relative to script location if needed
# Assuming script is run from project root

package_xcframework "filament" "Filament"
package_xcframework "filamat" "Filamat"
package_xcframework "filament-utils" "FilamentUtils"

# 4. Generate Podspecs
echo "📝 Generating Podspecs..."

function generate_podspec() {
    local name=$1
    local summary=$2
    local deps=$3
    local spec_file="${OUTPUT_DIR}/${name}.podspec"

    cat <<EOF > "$spec_file"
Pod::Spec.new do |spec|
    spec.name                     = '${name}'
    spec.version                  = '${VERSION}'
    spec.homepage                 = '${REPO_URL}'
    spec.source                   = { :http => "${RELEASE_URL}/${name}.xcframework.zip" }
    spec.authors                  = 'Filament KMP Authors'
    spec.license                  = 'Apache License, Version 2.0'
    spec.summary                  = '${summary}'
    spec.vendored_frameworks      = '${name}.xcframework'
    spec.libraries                = 'c++', 'z'
    spec.ios.deployment_target = '15.0'

    spec.pod_target_xcconfig = {
        'KOTLIN_TARGET' => 'ios_arm64',
    }
EOF

    # Add dependencies if any
    for dep in $deps; do
        echo "    spec.dependency '${dep}', '${VERSION}'" >> "$spec_file"
    done

    echo "end" >> "$spec_file"
    echo "  - Generated ${spec_file}"
}

generate_podspec "Filament" "Filament Kotlin Multiplatform Wrapper" ""
generate_podspec "Filamat" "Filamat Kotlin Multiplatform Wrapper" "Filament"
generate_podspec "FilamentUtils" "Filament Utils Kotlin Multiplatform Wrapper" "Filament"

echo "✅ Done! Artifacts are in ${OUTPUT_DIR}"
echo "Next steps:"
echo "1. Create a GitHub Release v${VERSION}"
echo "2. Upload all files from ${OUTPUT_DIR} to the release"
echo "3. Update your Podfile to use these podspecs"
