<!--
Title: use Conventional Commits with a platform scope, e.g.
  fix(js): ...   feat(compose)!: ...   chore(release): ...   refactor(c): ...
-->

## What & why

<!-- What does this change and why? Link any related issue (Closes #…). -->

## Platforms affected

- [ ] Android
- [ ] iOS
- [ ] JVM/Desktop
- [ ] Web/JS
- [ ] Common / all

## Upstream Filament

<!-- If this works around or depends on an engine-side issue, link the google/filament
issue/PR and add the `upstream-filament` label. If it patches the prebuilts, note it under
js/patches/. Otherwise write "N/A". -->

## Checklist

- [ ] Follows the API-parity / binding conventions (CONTRIBUTING.md); ran the relevant
      `scripts/dev/check-*.sh` if I touched bindings or bumped `filaVersion`.
- [ ] Updated docs/samples if needed.
- [x] I understand the full CI matrix runs only after a maintainer adds the **`ci:run`**
      label, and that `ci-gate` must be green before merge.
