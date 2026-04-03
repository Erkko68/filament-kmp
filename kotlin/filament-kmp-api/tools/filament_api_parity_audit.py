#!/usr/bin/env python3
"""Audit Android Filament Java API parity against KMP expect APIs.

The script compares public Java methods in:
- filament-main/android/filament-android/src/main/java/com/google/android/filament/*.java

against expect declarations in:
- kotlin/filament-kmp-api/src/commonMain/kotlin/dev/filament/kmp/*.kt

Comparison key is method name + parameter count (loose parity), with optional
strict signature details included in the report for manual triage.
"""

from __future__ import annotations

import argparse
import json
import re
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Dict, List, Tuple

JAVA_ANNOT_RE = re.compile(r"@\w+(?:\([^)]*\))?\s*")
COMMENT_BLOCK_RE = re.compile(r"/\*.*?\*/", re.DOTALL)
COMMENT_LINE_RE = re.compile(r"//.*")
STRING_RE = re.compile(r'"(?:\\.|[^"\\])*"')
CHAR_RE = re.compile(r"'(?:\\.|[^'\\])*'")

CLASS_DECL_RE = re.compile(
    r"\b(?:public\s+)?(?:static\s+)?(?:final\s+)?(class|interface|enum)\s+([A-Za-z_][A-Za-z0-9_]*)"
)

JAVA_METHOD_RE = re.compile(
    r"\bpublic\s+(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?(?:native\s+)?"
    r"(?:<[^>{}]*>\s*)?[A-Za-z_][A-Za-z0-9_<>\[\].?\s,]*\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)"
)

KOTLIN_EXPECT_CLASS_RE = re.compile(r"\bexpect\s+(?:open\s+)?(?:class|object)\s+([A-Za-z_][A-Za-z0-9_]*)")
KOTLIN_NESTED_CLASS_RE = re.compile(r"\b(?:class|object|enum\s+class)\s+([A-Za-z_][A-Za-z0-9_]*)")
KOTLIN_FUN_RE = re.compile(r"\bfun\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(([^)]*)\)")

EXCLUDED_JAVA_FILES = {
    "AndroidPlatform.java",
    "AndroidPlatform21.java",
    "Platform.java",
    "NativeSurface.java",
}


@dataclass(frozen=True)
class MethodSig:
    owner: str
    name: str
    arity: int
    detail: str

    @property
    def key(self) -> str:
        return f"{self.name}/{self.arity}"


@dataclass
class ClassParity:
    class_name: str
    java_method_count: int
    expect_method_count: int
    missing_in_expect: List[str]
    only_in_expect: List[str]


@dataclass
class ParityReport:
    java_class_count: int
    expect_class_count: int
    compared_class_count: int
    missing_expect_classes: List[str]
    only_expect_classes: List[str]
    class_reports: List[ClassParity]


def strip_comments_and_literals(text: str) -> str:
    text = COMMENT_BLOCK_RE.sub("", text)
    text = COMMENT_LINE_RE.sub("", text)
    text = STRING_RE.sub('""', text)
    text = CHAR_RE.sub("''", text)
    return text


def split_params(raw: str) -> List[str]:
    raw = raw.strip()
    if not raw:
        return []
    out: List[str] = []
    cur: List[str] = []
    depth_angle = 0
    depth_paren = 0
    depth_square = 0
    for ch in raw:
        if ch == "<":
            depth_angle += 1
        elif ch == ">":
            depth_angle = max(0, depth_angle - 1)
        elif ch == "(":
            depth_paren += 1
        elif ch == ")":
            depth_paren = max(0, depth_paren - 1)
        elif ch == "[":
            depth_square += 1
        elif ch == "]":
            depth_square = max(0, depth_square - 1)

        if ch == "," and depth_angle == 0 and depth_paren == 0 and depth_square == 0:
            token = "".join(cur).strip()
            if token:
                out.append(token)
            cur = []
            continue
        cur.append(ch)

    tail = "".join(cur).strip()
    if tail:
        out.append(tail)
    return out


def parse_java_method_detail(name: str, params_raw: str) -> str:
    params = split_params(params_raw)
    norm: List[str] = []
    for p in params:
        p = JAVA_ANNOT_RE.sub("", p).strip()
        p = re.sub(r"\s+", " ", p)
        parts = p.split(" ")
        if len(parts) > 1:
            p = " ".join(parts[:-1])
        norm.append(p)
    return f"{name}({', '.join(norm)})"


def parse_kotlin_param_types(params_raw: str) -> List[str]:
    params = split_params(params_raw)
    out: List[str] = []
    for p in params:
        # param form: name: Type = default
        if ":" not in p:
            continue
        rhs = p.split(":", 1)[1].strip()
        rhs = rhs.split("=", 1)[0].strip()
        out.append(rhs)
    return out


def collect_java_methods(java_file: Path) -> Dict[str, List[MethodSig]]:
    text = strip_comments_and_literals(java_file.read_text(encoding="utf-8", errors="ignore"))
    # Remove annotations to avoid parsing issues with params like @Size(min = 16).
    text = JAVA_ANNOT_RE.sub("", text)
    methods_by_owner: Dict[str, List[MethodSig]] = {}

    stack: List[Tuple[str, int]] = []
    pending_class: List[str] = []
    brace_depth = 0

    lines = text.splitlines()
    java_decl_buffer = ""
    java_decl_owner = ""
    for line in lines:
        for m in CLASS_DECL_RE.finditer(line):
            pending_class.append(m.group(2))

        if java_decl_buffer:
            java_decl_buffer += " " + line.strip()
            if ")" in line:
                for m in JAVA_METHOD_RE.finditer(java_decl_buffer):
                    name = m.group(1)
                    detail = parse_java_method_detail(name, m.group(2))
                    arity = len(split_params(m.group(2)))
                    methods_by_owner.setdefault(java_decl_owner, []).append(
                        MethodSig(owner=java_decl_owner, name=name, arity=arity, detail=detail)
                    )
                java_decl_buffer = ""
                java_decl_owner = ""
        elif stack:
            stripped = line.strip()
            if (
                "public" in stripped
                and "(" in stripped
                and " class " not in f" {stripped} "
                and " interface " not in f" {stripped} "
                and " enum " not in f" {stripped} "
            ):
                owner = ".".join(name for name, _ in stack)
                if ")" in stripped:
                    for m in JAVA_METHOD_RE.finditer(stripped):
                        name = m.group(1)
                        detail = parse_java_method_detail(name, m.group(2))
                        arity = len(split_params(m.group(2)))
                        methods_by_owner.setdefault(owner, []).append(
                            MethodSig(owner=owner, name=name, arity=arity, detail=detail)
                        )
                else:
                    java_decl_buffer = stripped
                    java_decl_owner = owner

        opens = line.count("{")
        closes = line.count("}")

        for _ in range(opens):
            if pending_class:
                class_name = pending_class.pop(0)
                stack.append((class_name, brace_depth + 1))
            brace_depth += 1

        for _ in range(closes):
            while stack and stack[-1][1] == brace_depth:
                stack.pop()
            brace_depth = max(0, brace_depth - 1)

    return methods_by_owner


def collect_expect_methods(expect_file: Path) -> Dict[str, List[MethodSig]]:
    text = strip_comments_and_literals(expect_file.read_text(encoding="utf-8", errors="ignore"))
    methods_by_owner: Dict[str, List[MethodSig]] = {}

    stack: List[Tuple[str, int]] = []
    pending_class: List[str] = []
    brace_depth = 0

    lines = text.splitlines()
    kt_decl_buffer = ""
    kt_decl_owner = ""
    for line in lines:
        m_expect = KOTLIN_EXPECT_CLASS_RE.search(line)
        if m_expect:
            pending_class.append(m_expect.group(1))
        else:
            for m in KOTLIN_NESTED_CLASS_RE.finditer(line):
                if stack:
                    pending_class.append(m.group(1))

        if kt_decl_buffer:
            kt_decl_buffer += " " + line.strip()
            if ")" in line:
                for m in KOTLIN_FUN_RE.finditer(kt_decl_buffer):
                    name = m.group(1)
                    types = parse_kotlin_param_types(m.group(2))
                    arity = len(types)
                    detail = f"{name}({', '.join(types)})"
                    methods_by_owner.setdefault(kt_decl_owner, []).append(
                        MethodSig(owner=kt_decl_owner, name=name, arity=arity, detail=detail)
                    )
                kt_decl_buffer = ""
                kt_decl_owner = ""
        elif stack:
            stripped = line.strip()
            if "fun " in stripped and "(" in stripped:
                owner = ".".join(name for name, _ in stack)
                if ")" in stripped:
                    for m in KOTLIN_FUN_RE.finditer(stripped):
                        name = m.group(1)
                        types = parse_kotlin_param_types(m.group(2))
                        arity = len(types)
                        detail = f"{name}({', '.join(types)})"
                        methods_by_owner.setdefault(owner, []).append(
                            MethodSig(owner=owner, name=name, arity=arity, detail=detail)
                        )
                else:
                    kt_decl_buffer = stripped
                    kt_decl_owner = owner

        opens = line.count("{")
        closes = line.count("}")

        for _ in range(opens):
            if pending_class:
                class_name = pending_class.pop(0)
                stack.append((class_name, brace_depth + 1))
            brace_depth += 1

        for _ in range(closes):
            while stack and stack[-1][1] == brace_depth:
                stack.pop()
            brace_depth = max(0, brace_depth - 1)

    return methods_by_owner


def flatten_owner(root: str, owner: str) -> str:
    if owner == root:
        return root
    if owner.startswith(root + "."):
        return owner
    return f"{root}.{owner}"


def build_report(java_dir: Path, expect_dir: Path) -> ParityReport:
    java_methods: Dict[str, List[MethodSig]] = {}
    expect_methods: Dict[str, List[MethodSig]] = {}

    java_top_level: set[str] = set()
    expect_top_level: set[str] = set()

    for java_file in sorted(java_dir.glob("*.java")):
        if java_file.name in EXCLUDED_JAVA_FILES:
            continue
        root = java_file.stem
        java_top_level.add(root)
        owners = collect_java_methods(java_file)
        for owner, methods in owners.items():
            java_methods[flatten_owner(root, owner)] = methods

    for kt_file in sorted(expect_dir.glob("*.kt")):
        text = strip_comments_and_literals(kt_file.read_text(encoding="utf-8", errors="ignore"))
        m = KOTLIN_EXPECT_CLASS_RE.search(text)
        if m:
            expect_top_level.add(m.group(1))
        owners = collect_expect_methods(kt_file)
        for owner, methods in owners.items():
            expect_methods[owner] = methods

    java_classes = set(java_methods.keys())
    expect_classes = set(expect_methods.keys())
    common = sorted(java_classes & expect_classes)

    class_reports: List[ClassParity] = []
    for cls in common:
        j = java_methods.get(cls, [])
        k = expect_methods.get(cls, [])

        j_keys = sorted({m.key for m in j})
        k_keys = sorted({m.key for m in k})

        missing = sorted(set(j_keys) - set(k_keys))
        only = sorted(set(k_keys) - set(j_keys))

        class_reports.append(
            ClassParity(
                class_name=cls,
                java_method_count=len(j_keys),
                expect_method_count=len(k_keys),
                missing_in_expect=missing,
                only_in_expect=only,
            )
        )

    return ParityReport(
        java_class_count=len(java_classes),
        expect_class_count=len(expect_classes),
        compared_class_count=len(common),
        missing_expect_classes=sorted(java_top_level - expect_top_level),
        only_expect_classes=sorted(expect_top_level - java_top_level),
        class_reports=class_reports,
    )


def format_markdown(report: ParityReport) -> str:
    lines: List[str] = []
    lines.append("# Filament Java vs KMP Expect API Parity")
    lines.append("")
    lines.append(f"- Java classes scanned: **{report.java_class_count}**")
    lines.append(f"- Expect classes scanned: **{report.expect_class_count}**")
    lines.append(f"- Compared classes: **{report.compared_class_count}**")
    lines.append("")

    lines.append("## Java Classes Missing In Expect")
    lines.append("")
    if report.missing_expect_classes:
        for c in report.missing_expect_classes:
            lines.append(f"- `{c}`")
    else:
        lines.append("- None")
    lines.append("")

    lines.append("## Expect Classes Not Found In Java")
    lines.append("")
    if report.only_expect_classes:
        for c in report.only_expect_classes:
            lines.append(f"- `{c}`")
    else:
        lines.append("- None")
    lines.append("")

    lines.append("## Method Parity By Class (name/arity)")
    lines.append("")
    for item in report.class_reports:
        lines.append(
            f"### `{item.class_name}` (java: {item.java_method_count}, expect: {item.expect_method_count})"
        )
        if item.missing_in_expect:
            lines.append("")
            lines.append("Missing in expect:")
            for m in item.missing_in_expect:
                lines.append(f"- `{m}`")
        if item.only_in_expect:
            lines.append("")
            lines.append("Only in expect:")
            for m in item.only_in_expect:
                lines.append(f"- `{m}`")
        if not item.missing_in_expect and not item.only_in_expect:
            lines.append("")
            lines.append("- No method parity gaps.")
        lines.append("")

    return "\n".join(lines)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Audit Filament Android Java public API parity against KMP expect API."
    )
    parser.add_argument(
        "--java-dir",
        type=Path,
        default=Path(
            "/Users/eric/IdeaProjects/filament-kmp-core/filament-main/android/filament-android/src/main/java/com/google/android/filament"
        ),
        help="Path to Android Filament Java API directory.",
    )
    parser.add_argument(
        "--expect-dir",
        type=Path,
        default=Path(
            "/Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/src/commonMain/kotlin/dev/filament/kmp"
        ),
        help="Path to commonMain expect Kotlin API directory.",
    )
    parser.add_argument(
        "--out-json",
        type=Path,
        default=Path(
            "/Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_java_expect_parity_report.json"
        ),
        help="Output JSON report path.",
    )
    parser.add_argument(
        "--out-md",
        type=Path,
        default=Path(
            "/Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_java_expect_parity_report.md"
        ),
        help="Output Markdown report path.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()

    if not args.java_dir.is_dir():
        print(f"error: java dir not found: {args.java_dir}")
        return 1
    if not args.expect_dir.is_dir():
        print(f"error: expect dir not found: {args.expect_dir}")
        return 1

    report = build_report(args.java_dir, args.expect_dir)

    args.out_json.parent.mkdir(parents=True, exist_ok=True)
    args.out_md.parent.mkdir(parents=True, exist_ok=True)

    args.out_json.write_text(json.dumps(asdict(report), indent=2) + "\n", encoding="utf-8")
    args.out_md.write_text(format_markdown(report) + "\n", encoding="utf-8")

    print(f"Wrote JSON report: {args.out_json}")
    print(f"Wrote Markdown report: {args.out_md}")
    print(f"Compared classes: {report.compared_class_count}")
    print(f"Java classes missing in expect: {len(report.missing_expect_classes)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

