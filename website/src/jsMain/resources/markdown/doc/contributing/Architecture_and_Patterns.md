---
root: .components.layouts.MarkdownLayout
title: "Contributing: Architecture and Patterns"
nav-title: "Architecture and Patterns"
description: Detailed internal architecture, project layout, module responsibilities, and recurring implementation patterns for Kore contributors.
keywords: architecture, bindings, commands, generator, kore, patterns, serializers, website
date-created: 2026-04-10
date-modified: 2026-04-15
routeOverride: /docs/contributing/architecture-and-patterns
---

# Contributing: Architecture and Patterns

This page documents the contributor-facing architecture of Kore and the implementation patterns worth reusing.

It is intentionally focused on the parts that help you answer two questions quickly:

1. *Which module should I edit?*
2. *Which project pattern should I follow instead of inventing a new one?*

## Project tree at a glance

```text
Kore/
├─ bindings/
├─ build-logic/
├─ generation/
├─ helpers/
├─ kore/
├─ oop/
└─ website/
```

This tree is intentionally small. It reflects the modules contributors should reason about first; local-only or
git-excluded sandboxes are omitted on purpose.

## How the main modules relate

- `bindings/` imports datapacks and emits Kotlin bindings for resources and registries.
- `build-logic/` centralizes shared Gradle conventions and project metadata.
- `generation/` transforms upstream Minecraft data into generated Kotlin/resources consumed by `kore/`.
- `helpers/` and `oop/` layer higher-level APIs on top of the core DSL.
- `kore/` is the main DSL and runtime surface most contributors touch first.
- `website/` documents both the public API and contributor workflows.

## Module boundaries and edit surfaces

### [`bindings/`][bindings-root]

- Purpose: datapack importer and Kotlin binding generator.
- Typical flow: explorer -> normalized entities -> writer output.
- Common edit surface: [`explorer.kt`][bindings-explorer], [`entities.kt`][bindings-entities], [
  `writer.kt`][bindings-writer], then tests under [`bindings/src/test`][bindings-tests].
- Pattern to preserve: single-namespace packs stay compact, multi-namespace packs become namespace-nested objects, and
  worldgen content is grouped under `Worldgen`.

### [`build-logic/`][build-logic-root]

- Purpose: shared Gradle conventions, publishing logic, and project metadata.
- Common edit surface: convention plugins and [`Project.kt`][project-kt].
- Edit here when changing build behavior, publication rules, or project versioning.

### [`generation/`][generation-root]

- Purpose: source-data processing and generated Kotlin/resource output.
- Edit here when a generated enum, registry wrapper, or source-derived structure is wrong.
- **Never** fix a generation issue by editing `kore/src/main/generated` or `build/generated/...` directly.

### [`helpers/`][helpers-root]

- Purpose: optional higher-level helpers built on the core DSL.
- Edit here only when the issue explicitly targets helper abstractions or reusable convenience APIs.
- Mirror core DSL patterns instead of creating a parallel architecture.

### [`kore/`][kore-root]

- Purpose: core DSL, typed arguments, command wrappers, serializers, worldgen builders, and data-driven resources.
- Common edit surface: feature classes, `DataPack` registration, `Function` extensions, serializers, and tests under [
  `kore/src/test`][kore-tests].
- A typical change in this module touches one feature family end to end: model, registration, builder entry point,
  tests, and docs.

### [`oop/`][oop-root]

- Purpose: object-oriented abstractions layered on top of core Kore primitives.
- Edit here when the issue is specifically about that façade, not when the underlying DSL itself is wrong.
- Keep naming and behavior aligned with `kore/` to avoid divergent APIs.

### [`website/`][website-root]

- Purpose: documentation markdown, docs navigation, and frontend rendering.
- Docs live in [`website/src/jsMain/resources/markdown/doc`][docs-root].
- Edit this module in the same PR as any user-visible behavior change.

## Core patterns to reuse

### Argument wrappers and literals

Kore prefers typed wrappers over raw strings.

- `Argument` is the base abstraction for command-safe value types.
- Generated argument wrappers are the preferred representation for registry references.
- Tag variants encode `#namespace:name` conventions.
- Literal helpers such as `literal()`, `int()`, and `float()` keep command assembly explicit and safe.

This improves autocomplete quality, reduces malformed command strings, and keeps serialization predictable.

### Command wrappers

Command APIs generally live as `Function` extension functions.

- Build lines with `addLine(command("name", args...))`.
- Accept typed arguments directly when possible.
- Keep wrappers thin: syntax composition belongs here, domain state belongs in arguments or data classes.

This keeps generated commands deterministic and test-friendly.

### Generator pattern

Most data-driven resources in `kore` follow one consistent model:

1. A serializable feature class extends [`Generator`][generator-kt].
2. The class defines its `resourceFolder`, transient `fileName`, and `generateJson(dataPack)` implementation.
3. [`DataPack`][datapack-kt] registers a typed generator list through `registerGenerator<T>()`.
4. A `DataPack` extension function instantiates and registers the feature.
5. The extension returns a typed `*Argument` for later references.

Concrete references:

- [`Generator.kt`][generator-kt]
- [`DataPack.kt`][datapack-kt]
- [`Instrument.kt`][instrument-kt]
- [`InstrumentTests.kt`][instrument-tests]

For the procedural version of this pattern, use [Contributing: Creating a New Generator][new-generator].

### Serializer strategy

Prefer an existing serializer before creating a new one.

Frequently reused serializers, in alphabetical order:

- [`EitherInlineSerializer`][serializer-either-inline]
- [`InlineAutoSerializer`][serializer-inline-auto]
- [`InlinableListSerializer`][serializer-inlinable-list]
- [`LowercaseSerializer`][serializer-lowercase]
- [`NamespacedPolymorphicSerializer`][serializer-namespaced]
- [`NbtAsJsonSerializer`][serializer-nbt-as-json]
- [`ProviderSerializer`][serializer-provider]
- [`SinglePropertySimplifierSerializer`][serializer-single-property]
- [`ToStringSerializer`][serializer-to-string]

Decision rule:

1. Check whether an existing serializer already matches the target JSON shape.
2. If not, see whether the model can be expressed with an existing pattern.
3. Only then add a new serializer, with focused tests.

## Fast heuristics when you are unsure where a change belongs

- **Build, publishing, or versioning behavior** -> `build-logic/` and root Gradle metadata.
- **Contributor or user-facing explanation** -> `website/` in the same PR.
- **Datapack import output shape** -> `bindings/` pipeline and tests.
- **Generated enum or registry wrapper shape** -> `generation/`, then regenerate and update `kore/` consumers.
- **New registry-backed DSL resource** -> usually `kore/`, plus generation if a new argument wrapper is required.

## Testing patterns that speed up contribution

- Feature tests usually assert emitted JSON payloads and generated command/resource lines.
- Module-specific `testDataPack(...)` helpers provide compact generation setups.
- Serializer tests should focus on roundtrip behavior and JSON or SNBT shape checks.

When you touch generators, the usual path is: model -> `DataPack` registration -> builder entry point -> tests -> docs.

## Documentation pages

Contributor pages are easier to maintain when they link to a single source of truth instead of restating the same rules.

Required frontmatter keys, in alphabetical order:

- `date-created`
- `date-modified`
- `description`
- `keywords`
- `nav-title`
- `root`
- `routeOverride`
- `title`

Keep routes stable, keep navigation intentional, and update entry pages when a new doc should become discoverable.

## Why Kore uses these technical choices

- **Centralized serializers** keep JSON and NBT output stable across modules and make diffs easier to review.
- **Generator-first data-driven features** keep feature additions mechanical instead of bespoke.
- **Tests close to feature families** help catch schema drift when Minecraft updates resource definitions.
- **Thin command wrappers** preserve command predictability while still covering vanilla syntax broadly.
- **Type-safe arguments over raw strings** reduce command regressions and improve IDE discoverability.

## See also

- [Contributing: Contributing][contributing]
- [Contributing: Creating a New Generator][new-generator]
- [Contributing: Workflow][workflow]

[bindings-entities]: https://github.com/ayfri/kore/blob/master/bindings/src/main/kotlin/io/github/ayfri/kore/bindings/entities.kt

[bindings-explorer]: https://github.com/ayfri/kore/blob/master/bindings/src/main/kotlin/io/github/ayfri/kore/bindings/explorer.kt

[bindings-root]: https://github.com/ayfri/kore/tree/master/bindings

[bindings-tests]: https://github.com/ayfri/kore/tree/master/bindings/src/test/kotlin/io/github/ayfri/kore/bindings

[bindings-writer]: https://github.com/ayfri/kore/blob/master/bindings/src/main/kotlin/io/github/ayfri/kore/bindings/writer.kt

[build-logic-root]: https://github.com/ayfri/kore/tree/master/build-logic

[contributing]: /docs/contributing/contributing

[datapack-kt]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/DataPack.kt

[docs-root]: https://github.com/ayfri/kore/tree/master/website/src/jsMain/resources/markdown/doc

[generation-root]: https://github.com/ayfri/kore/tree/master/generation

[generator-kt]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/Generator.kt

[helpers-root]: https://github.com/ayfri/kore/tree/master/helpers

[instrument-kt]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/features/instruments/Instrument.kt

[instrument-tests]: https://github.com/ayfri/kore/blob/master/kore/src/test/kotlin/io/github/ayfri/kore/features/InstrumentTests.kt

[kore-root]: https://github.com/ayfri/kore/tree/master/kore

[kore-tests]: https://github.com/ayfri/kore/tree/master/kore/src/test/kotlin/io/github/ayfri/kore

[new-generator]: /docs/contributing/creating-a-new-generator

[oop-root]: https://github.com/ayfri/kore/tree/master/oop

[project-kt]: https://github.com/ayfri/kore/blob/master/build-logic/convention/src/main/kotlin/Project.kt

[serializer-either-inline]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/EitherInlineSerializer.kt

[serializer-inline-auto]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/InlineAutoSerializer.kt

[serializer-inlinable-list]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/InlinableListSerializer.kt

[serializer-lowercase]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/LowercaseSerializer.kt

[serializer-namespaced]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/NamespacedPolymorphicSerializer.kt

[serializer-nbt-as-json]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/NbtAsJsonSerializer.kt

[serializer-provider]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/ProviderSerializer.kt

[serializer-single-property]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/SinglePropertySimplifierSerializer.kt

[serializer-to-string]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/ToStringSerializer.kt

[website-root]: https://github.com/ayfri/kore/tree/master/website

[workflow]: /docs/contributing/contributing-workflow
