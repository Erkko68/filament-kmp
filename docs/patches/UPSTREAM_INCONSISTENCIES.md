# Upstream Filament inconsistencies (WebGL/JS surface)

Cases where this repo's JS actuals (`kotlin/filament/src/jsMain/.../*.js.kt`)
can't faithfully implement the common API because of a gap in Filament's
upstream WebAssembly bindings. Each entry records the symptom, the upstream
cause, and the workaround we shipped.

Refresh by running `scripts/dev/check-js-bindings.sh` against a new `filaVersion`.

---

## `LightManager.Builder.shadowOptions(...)` — unbound type for `transform`

**Symptom:** calling `.shadowOptions(LightManager.ShadowOptions())` from JS
throws `Cannot call LightManager$Builder._shadowOptions due to unbound types`.

**Cause:** `web/filament-js/jsbindings.cpp` registers the value_object as

```cpp
value_object<LightManager::ShadowOptions>("LightManager$ShadowOptions")
    ...
    .field("transform", &LightManager::ShadowOptions::transform);
```

`ShadowOptions::transform` is a `filament::math::mat4f`, but the only mat4
ever registered in embind's type registry is `flatmat4` — a wrapper struct
exposed as JS `"mat4"`. embind looks up the field's RTTI hash, finds nothing
for `mat4f`, and throws "unbound types" at call time. No JS value can satisfy
the lookup; the upstream `Filament.shadowOptions(overrides)` helper in
`extensions.js` deliberately omits `transform` (alongside `lispsm` and
`shadowBulbRadius`) from its defaults for exactly this reason.

**Workaround:** [`LightManager.js.kt`'s
`Builder.shadowOptions`](../kotlin/filament/src/jsMain/kotlin/io/github/erkko68/filament/LightManager.js.kt)
is a documented no-op on JS. The Builder chain stays callable so callers can
still configure other light properties; shadow settings simply don't reach
native on the JS target.

**Upstream fix would be:** either register `mat4f` directly (the simplest
path is to expose it under the same conversion that `flatmat4` uses) or
change `LightManager::ShadowOptions::transform` to a type the binding
already knows how to marshal.
