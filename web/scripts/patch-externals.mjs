#!/usr/bin/env node
// Post-processes Karakum's generated Kotlin externals so they compile for BOTH
// the `js` and `wasmJs` targets (a shared `webMain` source set). Karakum has no
// wasmJs emission mode; its raw output already uses multiplatform kotlin-wrappers
// types (js.*, web.*, Double), but several of its codegen patterns violate the
// stricter Kotlin/Wasm JS-interop rules. We rewrite exactly those:
//
//   1. Enum value-holders. Karakum emits TS enums as
//          sealed external interface Backend { companion object { val DEFAULT: Backend } }
//      wasmJs forbids a nested object inside an external interface. All of these are
//      opaque value holders never used as supertypes, so we lower them to
//          external class Backend { companion object { val DEFAULT: Backend } }
//      keeping `Backend.DEFAULT` call sites working on both targets.
//
//   2. `: JsAny` supertype. On wasmJs an external class/interface is only usable as a
//      JsAny subtype (e.g. as a `JsArray<T>` element) if it explicitly extends JsAny.
//      Add it to every external class/interface that has no supertype.
//
//   3. `Any` at the interop boundary. wasmJs rejects kotlin.Any; Karakum emits it for
//      TS unions it can't model (typealias mat4 = Any?) and untyped option bags
//      (options: Any = definedExternally). Replace with JsAny/JsAny?.
//
//   4. Unbounded type parameters. `<T>` defaults to an `Any?` bound, illegal for a
//      wasmJs interop type parameter. Constrain declaration sites to `<T : JsAny?>`.
//
//   5. Unparameterized TypedArrays. kotlin-wrappers made these generic over the
//      backing buffer (Float32Array<B : ArrayBufferLike>); Karakum doesn't supply the
//      argument. Default it to <js.buffer.ArrayBuffer>.
//
//   6. Primitive `ReadonlyArray` elements. A JsArray element must be a JsAny subtype,
//      so Double/String can't be elements — box them to JsNumber/JsString.
//
//   7. `Void?` callback returns. wasmJs can't use Nothing? as a function-type return;
//      a void callback is `() -> Unit`.
//
// Everything else (external classes, js.core.*, js.promise.*, web.*, Double scalars,
// js.array.ReadonlyArray of object types) is left as raw kotlin-wrappers types.
//
// Usage: node patch-externals.mjs <generated-root-dir>

import { readFileSync, writeFileSync, readdirSync, statSync } from "node:fs";
import { join } from "node:path";

const root = process.argv[2];
if (!root) {
  console.error("usage: patch-externals.mjs <generated-root-dir>");
  process.exit(1);
}

function walk(dir) {
  const out = [];
  for (const name of readdirSync(dir)) {
    const p = join(dir, name);
    if (statSync(p).isDirectory()) out.push(...walk(p));
    else if (p.endsWith(".kt")) out.push(p);
  }
  return out;
}

const TYPED_ARRAYS =
  "Float32Array|Float64Array|Int8Array|Int16Array|Int32Array|Uint8Array|Uint8ClampedArray|Uint16Array|Uint32Array";

function patch(src) {
  return src
    // (1) enum value-holders: sealed external interface -> external class
    .replace(/\bsealed external interface\b/g, "external class")
    // (2) add `: JsAny` to external class/interface headers that have no supertype.
    //     Matches `external (class|interface) Name[<...>] {` — a header with a body and
    //     no `: Super` before the brace. Runs before (4) so generics are still bare.
    .replace(
      /\bexternal (class|interface) (\w+)(<[^>]*>)?\s*\{/g,
      (_m, kind, name, generics = "") => `external ${kind} ${name}${generics} : JsAny {`,
    )
    // (3) `Any` at the interop boundary -> JsAny. `Any`/`Any?` as a whole type token
    //     (not `Any<…>` nor an identifier like `AnyFoo`); trailing `//` comments allowed.
    .replace(/(\btypealias\s+\w+\s*=\s*)Any(\?)?/g, "$1JsAny$2")
    .replace(/:\s*Any(\?)?(?![\w<])/g, ": JsAny$1")
    // (3b) `Number` (kotlin.Number, from the overlay's stencil methods) is rejected by
    //      wasmJs interop; the underlying JS value is a plain number -> Double.
    .replace(/: Number\b/g, ": Double")
    .replace(/-> Number\b/g, "-> Double")
    // (4) constrain unbounded type-parameter declaration sites to `<T : JsAny?>`.
    //     Declaration positions only: `fun <T>` and `(class|interface) Name<T>`.
    .replace(/\bfun <([A-Z]\w*)>/g, "fun <$1 : JsAny?>")
    .replace(/\b((?:class|interface) \w+)<([A-Z]\w*)>/g, "$1<$2 : JsAny?>")
    // (5) default TypedArray backing-buffer type argument
    .replace(
      new RegExp(`js\\.typedarrays\\.(${TYPED_ARRAYS})\\b(?!\\s*<)`, "g"),
      "js.typedarrays.$1<js.buffer.ArrayBuffer>",
    )
    // (6) box primitive type arguments of Js generic containers (a JsArray/Vector/Record
    //     element or key must be a JsAny subtype, so Double/String can't appear there).
    .replace(/\b(ReadonlyArray|Vector)<Double>/g, "$1<JsNumber>")
    .replace(/\b(ReadonlyArray|Vector)<String>/g, "$1<JsString>")
    .replace(/\bReadonlyRecord<String,/g, "ReadonlyRecord<JsString,")
    // (7) void callback returns
    .replace(/->\s*js\.core\.Void\?/g, "-> Unit");
}

let changed = 0;
for (const file of walk(root)) {
  const before = readFileSync(file, "utf8");
  const after = patch(before);
  if (after !== before) {
    writeFileSync(file, after);
    changed++;
  }
}
console.log(`patch-externals: patched ${changed} file(s) under ${root}`);
