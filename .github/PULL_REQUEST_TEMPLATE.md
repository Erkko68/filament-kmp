<!-- Title: Conventional Commits with a scope, e.g. fix(js): …  feat(compose)!: … -->

## What & why

<!-- Link the issue (Closes #…). If it works around an engine bug, link the google/filament issue. -->

## Checklist

<!-- Tick what applies, delete what doesn't. -->

- [ ] Bindings or `filaVersion` changed: ran `./gradlew generateCApi generateKotlinExternals apiGaps`
- [ ] Public API changed: ran `./gradlew apiDump`
- [ ] User-visible: added a line under `[Unreleased]` in CHANGELOG.md, updated docs/samples
