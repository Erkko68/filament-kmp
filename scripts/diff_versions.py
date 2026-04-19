#!/usr/bin/env python3
"""
Compare Filament Android Java API between two versions.

Downloads source tarballs from GitHub and diffs the Java files for the
4 modules: filament, filamat, gltfio, filament-utils.

Usage:
    python3 diff_versions.py 1.71.0 1.72.0
    python3 diff_versions.py 1.71.0 1.72.0 --module filament
    python3 diff_versions.py 1.71.0 1.72.0 --output diff_report.txt
"""

import argparse
import difflib
import sys
import tarfile
import urllib.request
from pathlib import Path

SCRIPTS_DIR    = Path(__file__).parent
GITHUB_ARCHIVE = "https://github.com/google/filament/archive/refs/tags/v{version}.tar.gz"

MODULES = {
    "filament":        "android/filament-android/src/main/java/com/google/android/filament",
    "filamat":         "android/filamat-android/src/main/java/com/google/android/filament/filamat",
    "gltfio":          "android/gltfio-android/src/main/java/com/google/android/filament/gltfio",
    "filament-utils":  "android/filament-utils-android/src/main/java/com/google/android/filament/utils",
}


def download_source(version: str, cache_dir: Path) -> Path:
    dest = cache_dir / f"filament-{version}"
    if dest.exists():
        print(f"  [cache] {dest}")
        return dest

    url = GITHUB_ARCHIVE.format(version=version)
    archive = cache_dir / f"filament-{version}.tar.gz"
    print(f"  Downloading v{version} from {url} …")
    try:
        urllib.request.urlretrieve(url, archive)
    except Exception as e:
        print(f"ERROR: could not download v{version}: {e}", file=sys.stderr)
        sys.exit(1)

    print(f"  Extracting …")
    with tarfile.open(archive, "r:gz") as tar:
        tar.extractall(cache_dir)

    # GitHub names the root dir "filament-{version}"
    extracted = cache_dir / f"filament-{version}"
    if not extracted.exists():
        # Try to find the actual extracted dir name
        candidates = [d for d in cache_dir.iterdir() if d.is_dir() and version in d.name]
        if candidates:
            extracted = candidates[0]
        else:
            print(f"ERROR: could not find extracted directory for v{version}", file=sys.stderr)
            sys.exit(1)

    archive.unlink()  # save disk space
    return extracted


def collect_java_files(root: Path, module: str) -> dict[str, str]:
    """Return {relative_filename: content} for all .java files in the module path."""
    module_path = root / MODULES[module]
    files = {}
    if not module_path.exists():
        return files
    for java_file in sorted(module_path.glob("**/*.java")):
        rel = java_file.relative_to(module_path)
        files[str(rel)] = java_file.read_text(errors="replace")
    return files


def diff_module(_name: str, old_files: dict, new_files: dict, context: int = 5) -> list[str]:
    lines = []
    all_names = sorted(set(old_files) | set(new_files))

    added   = [f for f in all_names if f not in old_files]
    removed = [f for f in all_names if f not in new_files]
    changed = [f for f in all_names if f in old_files and f in new_files
               and old_files[f] != new_files[f]]

    if not added and not removed and not changed:
        lines.append(f"  (no changes)")
        return lines

    if added:
        lines.append(f"\n  NEW FILES ({len(added)}):")
        for f in added:
            lines.append(f"    + {f}")

    if removed:
        lines.append(f"\n  REMOVED FILES ({len(removed)}):")
        for f in removed:
            lines.append(f"    - {f}")

    if changed:
        lines.append(f"\n  CHANGED FILES ({len(changed)}):")
        for f in changed:
            old_lines = old_files[f].splitlines(keepends=True)
            new_lines = new_files[f].splitlines(keepends=True)
            diff = list(difflib.unified_diff(
                old_lines, new_lines,
                fromfile=f"old/{f}", tofile=f"new/{f}",
                n=context,
            ))
            if diff:
                lines.append(f"\n    --- {f} ---")
                lines.extend(l.rstrip("\n") for l in diff)

    return lines


def main():
    parser = argparse.ArgumentParser(description="Diff Filament Android API between two versions")
    parser.add_argument("old_version", help="Old version (e.g. 1.71.0)")
    parser.add_argument("new_version", help="New version (e.g. 1.72.0)")
    parser.add_argument("--module", choices=list(MODULES) + ["all"], default="all",
                        help="Which module to compare (default: all)")
    parser.add_argument("--context", type=int, default=5,
                        help="Lines of context around each change (default: 5)")
    parser.add_argument("--output", help="Write report to file instead of stdout")
    parser.add_argument("--cache", default=str(SCRIPTS_DIR),
                        help="Directory to cache downloaded sources (default: scripts/)")
    args = parser.parse_args()

    cache_dir = Path(args.cache)
    cache_dir.mkdir(parents=True, exist_ok=True)

    modules = list(MODULES) if args.module == "all" else [args.module]

    print(f"Fetching v{args.old_version} …")
    old_root = download_source(args.old_version, cache_dir)
    print(f"Fetching v{args.new_version} …")
    new_root = download_source(args.new_version, cache_dir)

    output_lines = [
        f"Filament API diff: v{args.old_version} → v{args.new_version}",
        "=" * 60,
    ]

    for mod in modules:
        output_lines.append(f"\n{'='*60}")
        output_lines.append(f"MODULE: {mod}")
        output_lines.append("=" * 60)
        old_files = collect_java_files(old_root, mod)
        new_files = collect_java_files(new_root, mod)

        if not old_files and not new_files:
            output_lines.append("  (module not found in either version)")
            continue

        output_lines.extend(diff_module(mod, old_files, new_files, args.context))

    report = "\n".join(output_lines)

    if args.output:
        Path(args.output).write_text(report)
        print(f"\nReport written to {args.output}")
    else:
        print(report)


if __name__ == "__main__":
    main()
