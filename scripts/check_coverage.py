#!/usr/bin/env python3
"""
Check which Android Filament API methods are missing from our KMP commonMain expects.

Compares the public Java API in scripts/filament-{version}/ against the Kotlin expect
declarations in kotlin/*/src/commonMain/ for all 4 modules.

Run `python3 download_filament.py <version>` first to fetch the sources.

Usage:
    python3 check_coverage.py
    python3 check_coverage.py --version 1.71.0
    python3 check_coverage.py --module filament
    python3 check_coverage.py --show-covered
    python3 check_coverage.py --output coverage_report.txt
"""

import argparse
import re
import sys
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional

SCRIPTS_DIR = Path(__file__).parent
REPO_ROOT   = SCRIPTS_DIR.parent

# Sub-paths inside scripts/filament-{version}/ for each module's Java sources
ANDROID_SUBPATHS = {
    "filament":       "android/filament-android/src/main/java/com/google/android/filament",
    "filamat":        "android/filamat-android/src/main/java/com/google/android/filament/filamat",
    "gltfio":         "android/gltfio-android/src/main/java/com/google/android/filament/gltfio",
    "filament-utils": "android/filament-utils-android/src/main/java/com/google/android/filament/utils",
}

KOTLIN_PATHS = {
    "filament":       REPO_ROOT / "kotlin/filament/src/commonMain/kotlin/io/github/erkko68/filament",
    "filamat":        REPO_ROOT / "kotlin/filamat/src/commonMain/kotlin/io/github/erkko68/filament/filamat",
    "gltfio":         REPO_ROOT / "kotlin/gltfio/src/commonMain/kotlin/io/github/erkko68/filament/gltfio",
    "filament-utils": REPO_ROOT / "kotlin/filament-utils/src/commonMain/kotlin/io/github/erkko68/filament/utils",
}


def find_latest_version() -> Optional[str]:
    """Return the most recently downloaded version in scripts/, or None."""
    dirs = sorted(
        d.name.removeprefix("filament-")
        for d in SCRIPTS_DIR.iterdir()
        if d.is_dir() and d.name.startswith("filament-")
    )
    return dirs[-1] if dirs else None


def resolve_version(requested: Optional[str]) -> str:
    if requested:
        path = SCRIPTS_DIR / f"filament-{requested}"
        if not path.exists():
            print(f"ERROR: scripts/filament-{requested}/ not found.", file=sys.stderr)
            print(f"Run:  python3 scripts/download_filament.py {requested}", file=sys.stderr)
            sys.exit(1)
        return requested
    latest = find_latest_version()
    if latest is None:
        print("ERROR: No Filament version downloaded yet.", file=sys.stderr)
        print("Run:  python3 scripts/download_filament.py <version>", file=sys.stderr)
        sys.exit(1)
    return latest


def make_modules(version: str) -> dict:
    base = SCRIPTS_DIR / f"filament-{version}"
    return {
        mod: {
            "android": base / subpath,
            "kotlin":  KOTLIN_PATHS[mod],
        }
        for mod, subpath in ANDROID_SUBPATHS.items()
    }


MODULES = {}  # populated at runtime after version is resolved

# Java methods/fields to ignore — internal/reflection plumbing not part of the public API
IGNORE_PATTERNS = [
    re.compile(r"getNativeObject"),
    re.compile(r"nativeObject"),
    re.compile(r"^n[A-Z]"),      # nCreateFoo, nSetBar — JNI native stubs
    re.compile(r"^access\$"),    # Kotlin-generated accessors
    re.compile(r"finalize"),     # Internal Java lifecycle
]

# Java types that map to utility/infra, not API surface
SKIP_CLASSES = {"NioUtils", "AndroidPlatform", "NativeSurface", "SwapChainFlags"}


@dataclass
class JavaMethod:
    name: str
    return_type: str
    params: str
    is_static: bool
    is_deprecated: bool
    enclosing_class: str


@dataclass
class ClassCoverage:
    name: str
    java_methods: list[str] = field(default_factory=list)
    kotlin_methods: list[str] = field(default_factory=list)
    kotlin_file_exists: bool = False

    @property
    def missing(self) -> list[str]:
        kt = set(self.kotlin_methods)
        return [m for m in self.java_methods if m not in kt]

    @property
    def covered(self) -> list[str]:
        kt = set(self.kotlin_methods)
        return [m for m in self.java_methods if m in kt]


# ---------------------------------------------------------------------------
# Java parsing
# ---------------------------------------------------------------------------

# Strip block comments and line comments from Java source
_BLOCK_COMMENT = re.compile(r"/\*.*?\*/", re.DOTALL)
_LINE_COMMENT  = re.compile(r"//.*")
_ANNOTATION    = re.compile(r"@\w+(?:\([^)]*\))?")

def strip_java_noise(src: str) -> str:
    src = _BLOCK_COMMENT.sub("", src)
    src = _LINE_COMMENT.sub("", src)
    return src


_JAVA_CLASS = re.compile(
    r"(?:public\s+)?(?:static\s+)?(?:final\s+)?(?:abstract\s+)?"
    r"(?:class|interface|enum)\s+(\w+)"
)
_JAVA_METHOD = re.compile(
    r"(?P<deprecated>@Deprecated\s+)?"
    r"public\s+(?P<static>static\s+)?"
    r"(?:(?:final|synchronized|native|abstract)\s+)*"
    r"(?P<ret>[\w<>\[\],\s@]+?)\s+"
    r"(?P<name>\w+)\s*\("
    r"(?P<params>[^)]*)\)"
)
_JAVA_ENUM_CONST = re.compile(r"^\s*([A-Z][A-Z0-9_]*)(?:\s*[,(;{]|$)", re.MULTILINE)


def normalise_type(t: str) -> str:
    """Strip annotations and extra whitespace from a Java type string."""
    t = _ANNOTATION.sub("", t)
    return re.sub(r"\s+", " ", t).strip()


def extract_java_methods(java_src: str, class_name: str) -> list[str]:
    """Extract public method names from a Java source file, normalised for comparison."""
    src = strip_java_noise(java_src)
    methods = []
    seen = set()

    for m in _JAVA_METHOD.finditer(src):
        name = m.group("name")
        # Skip constructor-shaped matches where name is the class name
        if name == class_name:
            continue
        # Skip Java keywords that look like method names
        if name in {"if", "while", "for", "switch", "return", "new", "class",
                    "interface", "enum", "import", "package", "void"}:
            continue
        if any(p.search(name) for p in IGNORE_PATTERNS):
            continue
        key = name
        if key not in seen:
            seen.add(key)
            methods.append(name)

    return sorted(methods)


def extract_java_enums(java_src: str) -> list[str]:
    """Extract top-level enum names declared in the file."""
    src = strip_java_noise(java_src)
    enums = []
    for m in re.finditer(r"\benum\s+(\w+)", src):
        enums.append(m.group(1))
    return enums


def parse_android_module(android_path: Path) -> dict[str, ClassCoverage]:
    """Parse all .java files in the Android module, return {ClassName: ClassCoverage}."""
    result: dict[str, ClassCoverage] = {}
    if not android_path.exists():
        return result

    for java_file in sorted(android_path.glob("*.java")):
        class_name = java_file.stem
        if class_name in SKIP_CLASSES:
            continue

        src = java_file.read_text(errors="replace")
        methods = extract_java_methods(src, class_name)
        enums   = extract_java_enums(src)

        cov = ClassCoverage(name=class_name)
        cov.java_methods = methods

        result[class_name] = cov

    return result


# ---------------------------------------------------------------------------
# Kotlin parsing
# ---------------------------------------------------------------------------

_KT_FUN    = re.compile(r"\bfun\s+(\w+)\s*[(<]")
_KT_ENUM   = re.compile(r"\benum\s+class\s+(\w+)")
_KT_CLASS  = re.compile(r"\bclass\s+(\w+)")
_KT_OBJECT = re.compile(r"\bobject\s+(\w+)")
_KT_PROP   = re.compile(r"\b(val|var)\s+(\w+)")


def extract_kotlin_methods(kt_src: str) -> list[str]:
    names = set()
    for m in _KT_FUN.finditer(kt_src):
        name = m.group(1)
        if name not in {"main", "get", "set", "invoke"}:
            names.add(name)
    
    # Map properties to potential Java getters/setters
    for m in _KT_PROP.finditer(kt_src):
        kind = m.group(1)
        prop = m.group(2)
        
        # Capitalize for camelCase methods
        cap = prop[0].upper() + prop[1:] if len(prop) > 0 else prop
        
        if prop.startswith("is"):
            names.add(prop) # isFoo -> isFoo
        else:
            names.add(f"get{cap}")
            names.add(prop) # some KMP APIs use the property name directly as the method name
            
        if kind == "var":
            names.add(f"set{cap}")

    return sorted(names)


def parse_kotlin_module(kotlin_path: Path) -> dict[str, dict]:
    """Parse .kt files, return {ClassName: {'methods': [names], 'exists': True}}."""
    result = {}
    if not kotlin_path.exists():
        return result

    for kt_file in sorted(kotlin_path.glob("*.kt")):
        class_name = kt_file.stem
        src = kt_file.read_text(errors="replace")
        methods = extract_kotlin_methods(src)
        result[class_name] = {
            "methods": methods,
            "exists": True
        }

    return result


# ---------------------------------------------------------------------------
# Report generation
# ---------------------------------------------------------------------------

def analyse_module(module_name: str, paths: dict) -> list[ClassCoverage]:
    android_data = parse_android_module(paths["android"])
    kotlin_data  = parse_kotlin_module(paths["kotlin"])

    coverages = []
    for class_name, cov in android_data.items():
        kt_info = kotlin_data.get(class_name, {"methods": [], "exists": False})
        cov.kotlin_methods = kt_info["methods"]
        cov.kotlin_file_exists = kt_info["exists"]
        coverages.append(cov)

    # Also note Android classes that have no Kotlin counterpart at all
    for class_name in sorted(set(android_data) - set(kotlin_data)):
        pass  # already handled above (kotlin_methods will be [])

    return sorted(coverages, key=lambda c: c.name)


def print_module_report(module_name: str, coverages: list[ClassCoverage],
                        show_covered: bool, out) -> None:
    total_java    = sum(len(c.java_methods) for c in coverages)
    total_missing = sum(len(c.missing)      for c in coverages)
    total_covered = total_java - total_missing

    pct = (total_covered / total_java * 100) if total_java else 0

    print(f"\n{'='*60}", file=out)
    print(f"MODULE: {module_name}  —  {total_covered}/{total_java} methods covered ({pct:.0f}%)", file=out)
    print("=" * 60, file=out)

    if not coverages:
        print("  (Android module path not found — module may not exist yet)", file=out)
        return

    for cov in coverages:
        if not cov.java_methods:
            continue

        missing_pct = (len(cov.covered) / len(cov.java_methods) * 100) if cov.java_methods else 0
        status = "✓" if not cov.missing else "✗"
        kt_note = "" if cov.kotlin_file_exists else " [NO KOTLIN FILE]"

        print(f"\n  {status} {cov.name}{kt_note}  "
              f"({len(cov.covered)}/{len(cov.java_methods)} covered)", file=out)

        if cov.missing:
            print(f"    MISSING:", file=out)
            for m in cov.missing:
                print(f"      - {m}", file=out)

        if show_covered and cov.covered:
            print(f"    covered:", file=out)
            for m in cov.covered:
                print(f"      + {m}", file=out)


def main():
    all_module_names = list(ANDROID_SUBPATHS)

    parser = argparse.ArgumentParser(
        description="Check KMP commonMain coverage against Android Filament Java API"
    )
    parser.add_argument("--version", help="Filament version to check (default: latest downloaded)")
    parser.add_argument("--module", choices=all_module_names + ["all"], default="all",
                        help="Which module to check (default: all)")
    parser.add_argument("--show-covered", action="store_true",
                        help="Also list methods that ARE covered (not just missing)")
    parser.add_argument("--output", help="Write report to file instead of stdout")
    args = parser.parse_args()

    version = resolve_version(args.version)
    modules_map = make_modules(version)
    module_items = list(modules_map.items()) if args.module == "all" \
                   else [(args.module, modules_map[args.module])]

    out_file = open(args.output, "w") if args.output else sys.stdout

    try:
        print(f"Filament KMP API Coverage Report  (v{version})", file=out_file)
        print(f"Repo: {REPO_ROOT}", file=out_file)

        grand_java = grand_covered = 0

        for mod_name, paths in module_items:
            coverages = analyse_module(mod_name, paths)
            print_module_report(mod_name, coverages, args.show_covered, out_file)
            grand_java    += sum(len(c.java_methods) for c in coverages)
            grand_covered += sum(len(c.covered)      for c in coverages)

        if args.module == "all":
            pct = (grand_covered / grand_java * 100) if grand_java else 0
            print(f"\n{'='*60}", file=out_file)
            print(f"TOTAL: {grand_covered}/{grand_java} methods covered ({pct:.0f}%)", file=out_file)
            print("=" * 60, file=out_file)

    finally:
        if args.output:
            out_file.close()
            print(f"Report written to {args.output}")


if __name__ == "__main__":
    main()
