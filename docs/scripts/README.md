# Automation & Utility Scripts

The `scripts/` directory contains Python utilities used for project maintenance, automated library management, and API coverage tracking.

## Script Overview

### 1. `download_filament.py`
Automates the downloading and extraction of prebuilt Filament binaries for different platforms.
- **Usage**: `python3 download_filament.py <version>`
- **Role**: Fetches the specified version of Filament from the official releases and prepares the `include` and `prebuilts` directories for the build system.

### 2. `check_coverage.py`
A core maintenance tool that compares the implemented Kotlin `expect` declarations against the official Filament Android Java API.
- **Usage**: `python3 check_coverage.py`
- **Role**: It scans the Java sources of the target Filament version and the Kotlin source sets to identify missing methods or classes, helping maintain parity with the upstream engine.
- **Output**: Generates detailed reports of covered vs. missing APIs.

### 3. `diff_versions.py`
Compares different versions of Filament to identify API changes, additions, or removals.
- **Role**: Useful when upgrading to a newer Filament version to quickly see what needs to be updated in the KMP wrapper.

## Documentation Artifacts

### `API_COVERAGE_ROADMAP.md`
A living document that tracks the current state of API coverage. It is typically updated by running the `check_coverage.py` script. It includes a "Skip List" for APIs that are either internal to Android or not applicable to this multiplatform wrapper.

---

## Requirements
- Python 3.x
- `pathlib` and `argparse` (standard libraries)
