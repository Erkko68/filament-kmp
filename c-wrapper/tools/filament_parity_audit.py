#!/usr/bin/env python3
"""Audit C-wrapper parity against Filament prebuilt headers.

This script compares recursively:
- C++ Filament headers in filament-prebuilts/include
- C wrapper headers in c-wrapper/include

It reports:
1) Missing wrapper headers.
2) For shared headers, likely-missing methods (heuristic mapping to C symbol names).

The method matching is intentionally conservative and heuristic-based.
"""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Dict, List, Sequence


CPP_HEADER_EXT = ".h"

# Function-like declarations that end with ';'.
DECL_RE = re.compile(
    r"^[^#\n]*\b([A-Za-z_][A-Za-z0-9_]*)\s*\([^;{}]*\)\s*(?:const\s*)?(?:noexcept\s*)?(?:=\s*[^;]+)?;",
    re.MULTILINE,
)

FUNC_RE = re.compile(
    r"^[^#\n]*\b(Fila[A-Za-z0-9_]*)\s*\([^;{}]*\)\s*;",
    re.MULTILINE,
)

CLASS_RE = re.compile(r"\b(?:class|struct)\s+([A-Za-z_][A-Za-z0-9_]*)\b")

SKIP_WORDS = {
    "if",
    "for",
    "while",
    "switch",
    "return",
    "sizeof",
    "alignof",
    "static_assert",
    "new",
    "delete",
    "default",
    "nullptr",
    "true",
    "false",
    "noexcept",
    "constexpr",
    "decltype",
    "typename",
    "template",
    "requires",
    "override",
    "final",
    "friend",
    "using",
    "return_type",
}

LOW_SIGNAL_NAMES = {
    "build",
    "callback",
    "features",
    "platform",
    "void",
    "size",
    "name",
}

# Header families intentionally out-of-scope for this wrapper phase.
IGNORED_MISSING_HEADER_PREFIXES = (
    "backend/platforms/",
    "camutils/",
    "filamat/",
    "filament-generatePrefilterMipmap/",
    "filament-iblprefilter/",
    "ibl/",
    "image/",
    "imageio-lite/",
    "ktxreader/",
    "math/",
    "mathio/",
    "mikktspace/",
    "tsl/",
    "uberz/",
    "viewer/",
)

# Known heuristic false positives that should not count as missing methods.
IGNORED_METHODS_BY_HEADER = {
    "filament/Engine.h": {"getSkyboxeCount", "has_value", "sharedContext"},
    "filament/Material.h": {"strlen"},
    "filament/MaterialInstance.h": {"strlen"},
    "filament/RenderableManager.h": {"bmax", "bmin", "min", "max"},
    "filament/Texture.h": {"move"},
    "filament/View.h": {"move"},
}


def strip_comments(text: str) -> str:
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.DOTALL)
    text = re.sub(r"//.*", "", text)
    return text


def strip_string_literals(text: str) -> str:
    # Avoid regex matches across inline string-heavy declarations.
    text = re.sub(r'"(?:\\.|[^"\\])*"', '""', text)
    text = re.sub(r"'(?:\\.|[^'\\])*'", "''", text)
    return text


def split_tokens(name: str) -> List[str]:
    # Split camelCase / PascalCase / snake_case into lowercase tokens.
    parts = re.findall(r"[A-Z]?[a-z]+|[A-Z]+(?![a-z])|[0-9]+", name.replace("_", " "))
    return [p.lower() for p in parts if p]


def is_subsequence(needle: Sequence[str], haystack: Sequence[str]) -> bool:
    if not needle:
        return False
    i = 0
    for token in haystack:
        if token == needle[i]:
            i += 1
            if i == len(needle):
                return True
    return False


def parse_cpp_methods(header_path: Path) -> List[str]:
    text = header_path.read_text(encoding="utf-8", errors="ignore")
    text = strip_string_literals(strip_comments(text))
    class_names = set(CLASS_RE.findall(text))
    methods = []
    for match in DECL_RE.finditer(text):
        name = match.group(1)
        prefix = text[max(0, match.start() - 192):match.start()]
        if "UTILS_DEPRECATED" in prefix or "@deprecated" in prefix.lower():
            continue
        if name in SKIP_WORDS:
            continue
        if name in class_names:
            continue
        # Skip likely constructors/destructors/operators/template internals.
        if name.startswith("~") or name == "operator":
            continue
        # Skip obvious non-API noise from inline/template internals.
        if name.startswith("_"):
            continue
        if "__" in name:
            continue
        if name.isupper():
            continue
        if len(name) <= 2 and name.islower():
            continue
        methods.append(name)
    return sorted(set(methods))


def parse_wrapper_functions(header_path: Path) -> List[str]:
    text = strip_comments(header_path.read_text(encoding="utf-8", errors="ignore"))
    funcs = [m.group(1) for m in FUNC_RE.finditer(text)]
    return sorted(set(funcs))


def likely_method_covered(method_name: str, wrapper_funcs: Sequence[str]) -> bool:
    if method_name in LOW_SIGNAL_NAMES:
        return True
    method_tokens = split_tokens(method_name)
    if not method_tokens:
        return False
    for func in wrapper_funcs:
        func_tokens = split_tokens(func)
        if is_subsequence(method_tokens, func_tokens):
            return True
    return False


@dataclass
class SharedHeaderReport:
    header: str
    prebuilt_method_count: int
    wrapper_function_count: int
    covered_method_count: int
    missing_methods: List[str]


@dataclass
class AuditReport:
    prebuilt_header_count: int
    wrapper_header_count: int
    missing_headers: List[str]
    shared_headers: List[SharedHeaderReport]


def collect_headers(directory: Path) -> Dict[str, Path]:
    headers: Dict[str, Path] = {}
    for path in sorted(directory.rglob(f"*{CPP_HEADER_EXT}")):
        if not path.is_file():
            continue
        rel = path.relative_to(directory).as_posix()
        headers[rel] = path
    return headers


def build_report(prebuilt_dir: Path, wrapper_dir: Path) -> AuditReport:
    prebuilt_headers = collect_headers(prebuilt_dir)
    wrapper_headers = collect_headers(wrapper_dir)

    missing_headers = sorted(
        [
            h
            for h in prebuilt_headers
            if h not in wrapper_headers
            and not any(h.startswith(prefix) for prefix in IGNORED_MISSING_HEADER_PREFIXES)
        ]
    )
    shared = sorted(set(prebuilt_headers.keys()) & set(wrapper_headers.keys()))

    shared_reports: List[SharedHeaderReport] = []
    for header in shared:
        prebuilt_methods = parse_cpp_methods(prebuilt_headers[header])
        wrapper_funcs = parse_wrapper_functions(wrapper_headers[header])

        missing_methods = [
            m for m in prebuilt_methods if not likely_method_covered(m, wrapper_funcs)
        ]
        ignored_methods = IGNORED_METHODS_BY_HEADER.get(header, set())
        if ignored_methods:
            missing_methods = [m for m in missing_methods if m not in ignored_methods]
        covered = len(prebuilt_methods) - len(missing_methods)

        shared_reports.append(
            SharedHeaderReport(
                header=header,
                prebuilt_method_count=len(prebuilt_methods),
                wrapper_function_count=len(wrapper_funcs),
                covered_method_count=covered,
                missing_methods=missing_methods,
            )
        )

    return AuditReport(
        prebuilt_header_count=len(prebuilt_headers),
        wrapper_header_count=len(wrapper_headers),
        missing_headers=missing_headers,
        shared_headers=shared_reports,
    )


def format_markdown(report: AuditReport) -> str:
    lines: List[str] = []
    lines.append("# Filament C Wrapper Parity Audit")
    lines.append("")
    lines.append(f"- Prebuilt headers scanned: **{report.prebuilt_header_count}**")
    lines.append(f"- Wrapper headers scanned: **{report.wrapper_header_count}**")
    lines.append(f"- Missing wrapper headers: **{len(report.missing_headers)}**")
    lines.append("")

    lines.append("## Missing Headers")
    lines.append("")
    if report.missing_headers:
        for h in report.missing_headers:
            lines.append(f"- `{h}`")
    else:
        lines.append("- None")
    lines.append("")

    lines.append("## Shared Header Method Gaps (Heuristic)")
    lines.append("")
    for item in report.shared_headers:
        lines.append(
            f"### `{item.header}` "
            f"(prebuilt methods: {item.prebuilt_method_count}, "
            f"wrapper funcs: {item.wrapper_function_count}, "
            f"covered: {item.covered_method_count})"
        )
        if item.missing_methods:
            lines.append("")
            lines.append("Missing/Unmatched methods:")
            for m in item.missing_methods:
                lines.append(f"- `{m}`")
        else:
            lines.append("")
            lines.append("- No unmatched methods detected.")
        lines.append("")

    return "\n".join(lines)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Audit Filament prebuilt vs C wrapper header parity.")
    parser.add_argument(
        "--prebuilt-dir",
        type=Path,
        default=Path("/Users/eric/IdeaProjects/filament-kmp-core/filament-prebuilts/include"),
        help="Path to filament-prebuilts include directory.",
    )
    parser.add_argument(
        "--wrapper-dir",
        type=Path,
        default=Path("/Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/include"),
        help="Path to c-wrapper include directory.",
    )
    parser.add_argument(
        "--out-json",
        type=Path,
        default=Path("/Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_report.json"),
        help="Output JSON report path.",
    )
    parser.add_argument(
        "--out-md",
        type=Path,
        default=Path("/Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_report.md"),
        help="Output Markdown report path.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    if not args.prebuilt_dir.is_dir():
        print(f"error: prebuilt dir not found: {args.prebuilt_dir}")
        return 1
    if not args.wrapper_dir.is_dir():
        print(f"error: wrapper dir not found: {args.wrapper_dir}")
        return 1

    report = build_report(args.prebuilt_dir, args.wrapper_dir)

    args.out_json.parent.mkdir(parents=True, exist_ok=True)
    args.out_md.parent.mkdir(parents=True, exist_ok=True)

    args.out_json.write_text(
        json.dumps(
            {
                "prebuilt_header_count": report.prebuilt_header_count,
                "wrapper_header_count": report.wrapper_header_count,
                "missing_headers": report.missing_headers,
                "shared_headers": [asdict(s) for s in report.shared_headers],
            },
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    args.out_md.write_text(format_markdown(report) + "\n", encoding="utf-8")

    print(f"Wrote JSON report: {args.out_json}")
    print(f"Wrote Markdown report: {args.out_md}")
    print(f"Missing headers: {len(report.missing_headers)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

