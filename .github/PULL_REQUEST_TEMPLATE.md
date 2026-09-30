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
issue/PR and add the `upstream-filament` label. Otherwise write "N/A". -->

## Checklist

- [ ] Follows the API-parity / binding conventions (CONTRIBUTING.md); regenerated the C API and
      externals (`./gradlew generateCApi generateKotlinExternals`) and ran `./gradlew apiGaps` if I
      touched bindings or bumped `filaVersion`.
- [ ] Ran `./gradlew apiDump` if the public API changed, and added a CHANGELOG line.
- [ ] Updated docs/samples if needed.
- [x] I understand CI runs only the platform jobs my changed paths touch, and that `ci-gate`
      must be green before merge.
