---
root: .components.layouts.MarkdownLayout
title: Contributing to Kore
nav-title: Contributing
description: Entry point for contributors who want to work on Kore architecture, workflows, and project quality.
keywords: architecture, contributing, kore, patterns, quality, workflow
date-created: 2026-04-10
date-modified: 2026-04-15
routeOverride: /docs/contributing/contributing
---

# Contributing to Kore

This page is the *entry point* for contributors who do not know the codebase yet.

Use it to identify the right guide before touching code, tests, or documentation.

## Choose the right guide

- **Add a new data-driven feature:** read [Contributing: Creating a New Generator][new-generator] before editing `kore`.
- **Add or update DSL features:** start with [Contributing: Architecture and Patterns][architecture], then
  use [Contributing: Workflow][workflow].
- **Prepare an issue or a pull request:** go straight to [Contributing: Workflow][workflow].
- **Ship a release or update versions:** use [Contributing: CI/CD and Releases][releases].

## Repository map at a glance

- `bindings/`: datapack importer and Kotlin bindings code generation.
- `build-logic/`: shared Gradle conventions and project metadata.
- `generation/`: Minecraft source-data processing and generated Kotlin/resource pipelines.
- `helpers/`: optional higher-level helpers built on top of the core DSL.
- `kore/`: core DSL, arguments, commands, serializers, and generators.
- `oop/`: object-oriented abstractions layered on top of Kore primitives.
- `website/`: documentation content, docs navigation, and generated doc indexes.

## Contribution principles

- Keep generated files generated: fix the generator or source pipeline, not `build/generated/...` or
  `kore/src/main/generated`.
- Prefer existing serializers, typed arguments, and command helpers before introducing new abstractions.
- Ship tests and docs with behavior changes, especially in `bindings/` and `kore/`.
- Stay within one clear module boundary when possible; use [Architecture and Patterns][architecture] to decide where a
  change belongs.

## Suggested first pass for a newcomer

1. Read [Contributing: Architecture and Patterns][architecture] to understand module boundaries.
2. Read [Contributing: Workflow][workflow] to understand tests, docs, and PR expectations.
3. Open one existing feature in `kore` end to end: feature class, `DataPack` registration, tests, then docs.
4. If the change is data-driven, mirror the closest feature
   with [Contributing: Creating a New Generator][new-generator].

[architecture]: /docs/contributing/architecture-and-patterns

[new-generator]: /docs/contributing/creating-a-new-generator

[releases]: /docs/contributing/ci-cd-and-releases

[workflow]: /docs/contributing/contributing-workflow
