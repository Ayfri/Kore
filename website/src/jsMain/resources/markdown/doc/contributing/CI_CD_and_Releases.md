---
root: .components.layouts.MarkdownLayout
title: "Contributing: CI/CD and Releases"
nav-title: "CI/CD and Releases"
description: Project and Minecraft versioning, CI automation, CodeQL scanning, release naming, and operational release practices for Kore maintainers.
keywords: cd, ci, kore, maintenance, minecraft, releases, versioning
date-created: 2026-04-10
date-modified: 2026-04-15
routeOverride: /docs/contributing/ci-cd-and-releases
---

# Contributing: CI/CD and Releases

This page summarizes the maintenance and release process used for Kore.

## Version ownership

Kore tracks two coordinated version streams:

- Minecraft target version in [`gradle.properties`][gradle-properties].
- Project version in [`build-logic/convention/src/main/kotlin/Project.kt`][project-kt].

Expected increment conventions:

- Breaking/major change: `+0.1`
- Minor fix/addition: `+0.0.1`

## Release naming pattern

Observed release tags combine project and Minecraft targets, for example:

- `2.0.0-1.21.11`
- `1.42.1-1.21.11`
- `1.41.1-1.21.11-rc3`

This pattern keeps compatibility intent visible directly in release identifiers.

## Minecraft update flow

For update cycles:

1. Add/update tests for new snapshot/release behavior.
2. Update project and Minecraft versions.
3. Run relevant module tests.
4. Update docs for API or behavior changes.

## Website release metadata behavior

Website build logic includes GitHub release fetching to generate website-side release metadata.

Maintainer notes:

- `GITHUB_TOKEN` helps avoid API limits and improves reliability.
- Generated outputs are artifacts, not hand-maintained source.

## GitHub Actions automation overview

Current repository automation is split across dedicated workflows under `.github/workflows`:

- `ci.yml`: runs the Gradle test suite on pushes and pull requests targeting `master`.
- `codeql.yml`: runs GitHub CodeQL analysis for `actions` and `java-kotlin` on pushes, pull requests, manual dispatch,
  and a weekly schedule.
- `publish.yml`: performs the manual release publication flow.
- `publish-snapshot.yml`: publishes snapshot artifacts from `master`.

CodeQL is intentionally scoped to the meaningful code in this repository:

- `actions` covers the GitHub workflow files themselves.
- `java-kotlin` covers the Gradle/Kotlin codebase.

The scheduled CodeQL run uses GitHub Actions cron syntax in UTC and is set to once per week to keep regular security
coverage without adding noise to every day.

## Commit message conventions

- `chore(minecraft): Increase Minecraft version to X.Y.Z.`
- `chore(project): Increase project version to X.Y.Z.`
- `feat(code): Update project to Minecraft version X.Y.Z.`

Consistent messages improve changelog scanning and release auditability.

## Where to start for maintainers

- For version bumps: start with test updates, then change version files, then docs.
- For release issues: check module labels and recent update issues for similar patterns.
- For contribution workflow details: use [Contributing: Workflow][workflow].

## See also

- [Contributing][contributing]
- [Contributing: Architecture and Patterns][architecture]
- [Contributing: Workflow][workflow]

[architecture]: /docs/contributing/architecture-and-patterns

[contributing]: /docs/contributing/contributing

[gradle-properties]: https://github.com/ayfri/kore/blob/master/gradle.properties

[project-kt]: https://github.com/ayfri/kore/blob/master/build-logic/convention/src/main/kotlin/Project.kt

[workflow]: /docs/contributing/contributing-workflow
