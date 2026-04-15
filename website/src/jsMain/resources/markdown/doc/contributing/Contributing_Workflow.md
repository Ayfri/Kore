---
root: .components.layouts.MarkdownLayout
title: "Contributing: Workflow"
nav-title: "Contributing Workflow"
description: End-to-end workflow for opening issues, implementing changes, validating them, and preparing pull requests in Kore.
keywords: contributing, docs, issues, kore, pull-request, quality, tests, workflow
date-created: 2026-04-10
date-modified: 2026-04-15
routeOverride: /docs/contributing/contributing-workflow
---

# Contributing: Workflow

This page is the *process reference* for changes in Kore.

Use it when you already know **what** you want to change and need to validate **how** to carry that change from issue to
PR.

## 1) Qualify the issue or proposal first

Before writing code, make sure the scope is explicit.

If you are opening an issue:

- Pick the closest template in [`.github/ISSUE_TEMPLATE`][issue-templates].
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

## 2) Trace the change before editing

Before implementation:

- Confirm the target module in [Contributing: Architecture and Patterns][architecture].
- Identify symbol usages and nearby call paths.
- Reuse an existing helper, serializer, or feature pattern when one already exists.

This is the fastest way to avoid duplicate APIs and hidden coupling.

If the change is a new data-driven feature in `kore`, use [Contributing: Creating a New Generator][new-generator]
instead of inventing a new shape from scratch.

## 3) Implement with tests and docs in the same pass

For `bindings` and `kore`, tests are expected alongside code changes.

Test locations:

- [`bindings/src/test/kotlin/io/github/ayfri/kore/bindings`][bindings-tests]
- [`kore/src/test/kotlin/io/github/ayfri/kore`][kore-tests]

At minimum, validate:

- Output behavior for commands or generated resources.
- Regression behavior for bugs.
- Serialization or deserialization shapes when JSON/NBT is involved.

Any user-visible behavior change should update docs in [`website/src/jsMain/resources/markdown/doc`][docs-root].

Documentation updates usually include:

- A usage-oriented example when the feature is public.
- A migration note or caveat when behavior changed.
- Updated internal links when contributor navigation should change.

## 4) Validate locally before opening the PR

Run the checks that match the modules you changed.

Typical commands:

- `./gradlew :bindings:runUnitTests` for `bindings` changes.
- `./gradlew :kore:runUnitTests` for core DSL changes.
- Your local website workflow when you touched docs navigation or page structure.

Validation checklist:

- Docs reflect the final API or DSL shape.
- Generated outputs were **not** edited by hand.
- Manual lists and registries still follow project ordering conventions.
- Tests cover the changed behavior.

## 5) Prepare a reviewable pull request

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

## 6) Frequent review blockers

- Editing generated output instead of updating the generator or source pipeline.
- Mixing refactors and feature behavior changes in one large PR.
- Re-implementing a serializer or helper that already exists.
- Shipping an API change without matching documentation.

## See also

- [Contributing: Architecture and Patterns][architecture]
- [Contributing: Contributing][contributing]
- [Contributing: Creating a New Generator][new-generator]
- [Contributing: CI/CD and Releases][releases]

[architecture]: /docs/contributing/architecture-and-patterns

[bindings-tests]: https://github.com/ayfri/kore/tree/master/bindings/src/test/kotlin/io/github/ayfri/kore/bindings

[contributing]: /docs/contributing/contributing

[docs-root]: https://github.com/ayfri/kore/tree/master/website/src/jsMain/resources/markdown/doc

[issue-templates]: https://github.com/ayfri/kore/tree/master/.github/ISSUE_TEMPLATE

[kore-tests]: https://github.com/ayfri/kore/tree/master/kore/src/test/kotlin/io/github/ayfri/kore

[new-generator]: /docs/contributing/creating-a-new-generator

[releases]: /docs/contributing/ci-cd-and-releases
