---
root: .components.layouts.MarkdownLayout
title: Contributing to Kore
nav-title: Contributing
description: Entry point and workflow for contributors to Kore - choose the right guide, qualify issues, test, document and open reviewable pull requests.
keywords: architecture, contributing, kore, patterns, quality, workflow, docs, issues, pull-request, tests
date-created: 2026-04-10
date-modified: 2026-11-11
routeOverride: /docs/contributing/contributing
---

# Contributing to Kore

This page is the *entry point* for contributors who do not know the codebase yet, and the *process reference* for carrying a
change from issue to PR.

Use it to identify the right guide before touching code, tests, or documentation. Once you know **what** you want to change,
the workflow sections below cover **how** to carry it: issues, implementation, tests, docs, pull request, and commit messages.

## Choose the right guide

- **Add a new data-driven feature:** read [Contributing: Creating a New Generator](/docs/contributing/creating-a-new-generator) before editing `kore`.
- **Add or update DSL features:** start with [Contributing: Architecture and Patterns](/docs/contributing/architecture-and-patterns), then
  follow the workflow below.
- **Add or fix a generated enum/registry/argument type:** read [Contributing: The Generation Pipeline](/docs/contributing/generation-pipeline).
- **Ship a release or update versions:** use [Contributing: CI/CD and Releases](/docs/contributing/ci-cd-and-releases).

## Repository map at a glance

- `bindings/`: datapack importer and Kotlin bindings code generation.
- `build-logic/`: shared Gradle conventions and project metadata.
- `generation/`: Minecraft source-data processing and generated Kotlin/resource pipelines.
- `helpers/`: optional higher-level helpers built on top of the core DSL.
- `kore/`: core DSL, arguments, commands, serializers, and generators.
- `oop/`: object-oriented abstractions layered on top of Kore primitives.
- `website/`: documentation content, docs navigation, and generated doc indexes.

## Suggested first pass for a newcomer

Open one existing feature in `kore` end to end before writing your own: feature class, `DataPack` registration, tests, then docs.

## Qualify the issue or proposal first

Before writing code, make sure the scope is explicit.

If you are opening an issue:

- Pick the closest template in [`.github/ISSUE_TEMPLATE`](https://github.com/ayfri/kore/tree/master/.github/ISSUE_TEMPLATE).
- Search for duplicates first.
- Include a minimal reproduction or explicit reproduction steps.
- Include expected behavior, actual behavior, and the Kore + Minecraft versions you tested.

If you are starting from an existing issue or drafting a PR directly:

- Keep the first pass focused on one behavior change.
- Pick the narrowest module scope possible.
- Read one similar merged change before adding new abstractions.

Useful scope labels when triaging work:

| Scope             | Use it for                                           |
|-------------------|------------------------------------------------------|
| `bindings`        | Datapack importer and generated bindings output      |
| `dsl`             | Command DSL and data-driven `kore` APIs              |
| `generation`      | Source-data processing and code generation pipelines |
| `helpers` / `oop` | Higher-level APIs built on top of the core DSL       |
| `update`          | Minecraft version tracking and update work           |
| `website`         | Documentation content and docs site behavior         |

## Trace the change before editing

Before implementation:

- Confirm the target module in [Contributing: Architecture and Patterns](/docs/contributing/architecture-and-patterns).
- Identify symbol usages and nearby call paths.
- Reuse an existing helper, serializer, or feature pattern when one already exists.

This is the fastest way to avoid duplicate APIs and hidden coupling.

If the change is a new data-driven feature in `kore`, use [Contributing: Creating a New Generator](/docs/contributing/creating-a-new-generator)
instead of inventing a new shape from scratch.

## Implement with tests and docs in the same pass

For `bindings` and `kore`, tests are expected alongside code changes.

Test locations, per module (all four modules are Kotlin Multiplatform - see
[Multiplatform Support](/docs/advanced/multiplatform)):

- `commonTest` for specs that build a `dataPack { }` in memory and assert JSON/command output; these run on the JVM,
  Node.js, and a headless browser.
- `jvmTest` for specs that need real file I/O (`generate()`, `generateZip()`) or a JVM-only dependency.

For example, [`bindings/src/commonTest/kotlin/io/github/ayfri/kore/bindings`](https://github.com/ayfri/kore/tree/master/bindings/src/commonTest/kotlin/io/github/ayfri/kore/bindings) and
[`kore/src/commonTest/kotlin/io/github/ayfri/kore`](https://github.com/ayfri/kore/tree/master/kore/src/commonTest/kotlin/io/github/ayfri/kore).

At minimum, validate:

- Output behavior for commands or generated resources.
- Regression behavior for bugs.
- Serialization or deserialization shapes when JSON/NBT is involved.

Any user-visible behavior change should update docs in [`website/src/jsMain/resources/markdown/doc`](https://github.com/ayfri/kore/tree/master/website/src/jsMain/resources/markdown/doc).

Documentation updates usually include:

- A usage-oriented example when the feature is public.
- A migration note or caveat when behavior changed.
- Updated internal links when contributor navigation should change.

## Validate locally before opening the PR

Run the checks that match the modules you changed.

Typical commands:

- `./gradlew :bindings:jvmTest` for `bindings` changes, fast JVM-only iteration.
- `./gradlew :kore:jvmTest` for core DSL changes, fast JVM-only iteration.
- `./gradlew :oop:jvmTest` for OOP module changes, fast JVM-only iteration.
- `./gradlew :helpers:jvmTest` for helpers module changes, fast JVM-only iteration.
- `./gradlew testAll` before opening the PR, to also run every module's Node.js and browser (Karma) test tasks.
- Your local website workflow when you touched docs navigation or page structure.

Validation checklist:

- Docs reflect the final API or DSL shape.
- Generated outputs were **not** edited by hand.
- Manual lists and registries still follow project ordering conventions.
- Tests cover the changed behavior.

## Prepare a reviewable pull request

A good Kore PR is easy to review because it stays narrow and links the right context.

In the PR body, link:

- The docs pages updated in the same PR.
- The issue or proposal being solved.
- The tests that cover the change.
- Any migration impact or behavior caveat for existing users.

For the title and commit messages, follow conventional commits. Common examples:

- `chore(minecraft): Increase Minecraft version to X.Y.Z.`
- `chore(project): Increase project version to X.Y.Z.`
- `feat(code): Update project to Minecraft version X.Y.Z.`

## Review blockers and contribution principles

- Editing generated output instead of updating the generator or source pipeline: keep generated files generated, so fix the
  generator or source pipeline, not `build/generated/...` or `kore/src/commonMain/kotlin/io/github/ayfri/kore/generated`.
- Re-implementing a serializer or helper that already exists: prefer existing serializers, typed arguments, and command
  helpers before introducing new abstractions.
- Shipping a behavior change without tests and matching documentation, especially in `bindings/` and `kore/`.
- Mixing refactors and feature behavior changes in one large PR.
- Spreading a change across several module boundaries: stay within one clear module boundary when possible, and use
  [Architecture and Patterns](/docs/contributing/architecture-and-patterns) to decide where a change belongs.

## See also

- [Contributing: Architecture and Patterns](/docs/contributing/architecture-and-patterns)
- [Contributing: Creating a New Generator](/docs/contributing/creating-a-new-generator)
- [Contributing: CI/CD and Releases](/docs/contributing/ci-cd-and-releases)
- [Multiplatform Support](/docs/advanced/multiplatform)
