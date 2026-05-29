---
root: .components.layouts.MarkdownLayout
title: Known Issues
nav-title: Known Issues
description: Kore-focused limitations, documented DSL constraints, and generation rough edges with links to docs and source.
keywords: kore, guide, documentation, known issues, compatibility, limitations, workarounds
date-created: 2025-08-27
date-modified: 2026-04-26
routeOverride: /docs/advanced/known-issues
---

# Known issues and compatibility

This page lists **limitations and rough edges that touch Kore directly**: the `kore` module, **`bindings`**, and *
*`helpers`** where Kore documents behavior. It is not a general Minecraft gameplay reference.

Each item is tagged:

| Category                        | Meaning                                                                                                                                  |
|---------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| **Kore limitation**             | Kore or a dependency Kore wires in (for example [knbt](https://github.com/BenWoodworth/knbt)) cannot express or round-trip the case yet. |
| **Documented DSL / helper cap** | Behavior is spelled out in Kore’s own command or helper docs (often because the vanilla command format or text API is narrow).           |
| **Generation rough edge**       | `generate()` / `generateZip()` / merge paths behave as implemented; surprising until you read the generation docs.                       |

For the high-level “what Kore does not include,”
see [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore#what-kore-does-not-give-you-or-not-yet).

---

## Kore limitations

### NBT and SNBT (via knbt)

**Category:** Kore limitation

Kore builds NBT and SNBT through **[knbt](https://github.com/BenWoodworth/knbt)** (`StringifiedNbt` and builders). Entry
points include [
`NbtTagUtils.kt`](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/utils/NbtTagUtils.kt)
and the `nbt { }` / `stringifiedNbt(...)` helpers used across commands and components.

For normal day-to-day usage and examples, start with [NBTs](/docs/concepts/nbts); this section focuses on limitations
and
workarounds.

Implications:

- **Typed NBT lists:** In knbt, `NbtList` is **homogeneous** (one element type per list). That matches Java edition NBT,
  but it means you cannot build a single list that mixes unrelated tag kinds through the normal DSL. See
  the [knbt README](https://github.com/BenWoodworth/knbt/blob/main/README.md) (`NbtList` description).
- **Exotic SNBT text:** Some SNBT spellings or parser extensions you might see in external tools are **not** necessarily
  available through Kore’s knbt-backed path. Prefer compounds, homogeneous lists, and literals Kore’s builders support.

**Workaround:** Emit a **`literal("...")`** only when you must hand-craft a fragment, or post-process generated files.
Upgrading or patching **[knbt](https://github.com/BenWoodworth/knbt)** (Kore pins a version in [
`gradle/libs.versions.toml`](https://github.com/Ayfri/Kore/blob/master/gradle/libs.versions.toml)) is the right place
for format-level fixes.

### Resource packs

**Category:** Kore limitation

Kore generates **datapacks** only (`data/`, functions, JSON registries, `pack.mcmeta`). It does **not** emit **resource
packs** (`assets/`). That scope is stated on the site (for example [Home](/docs/home)) and
in [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore#what-kore-does-not-give-you-or-not-yet).

**Workaround:** Maintain a separate resource-pack project or tooling; you can still keep both in one Gradle repo.

### Bindings importer

**Category:** Kore limitation

The **`bindings`** module is **experimental**. Official docs: [Bindings](/docs/advanced/bindings). Roadmap and design
discussion: [GitHub #176](https://github.com/Ayfri/Kore/issues/176).

Notable constraints from Kore’s own docs and README:

- Generated APIs may **change** between Kore versions; pin versions and review diffs.
- **Unknown resource types** are **skipped** so codegen stays
  valid ([Bindings troubleshooting](/docs/advanced/bindings#troubleshooting)).
- **Macros** in imported `.mcfunction` files are detected via **`$()`-style patterns**, not a full Minecraft
  parser ([Bindings](/docs/advanced/bindings)).
- **CurseForge** downloads need a **CurseForge API key**; **GitHub** may need a token for rate
  limits ([Download sources](/docs/advanced/bindings#download-sources)).

**Workaround:** Treat imports as a typed starting point; simplify or remap packs that confuse the importer (
`remappings { }`, `includes` / `excludes` in [Configuration](/docs/advanced/bindings#configuration)).

### JSON / NBT serialization (generate vs decode)

**Category:** Kore limitation

Many serializers are **write-only** for Kore’s use case: they **encode** what you build in Kotlin and **refuse to decode
** from arbitrary vanilla JSON/SNBT. Examples in the codebase include **`ComponentsSerializer`** (“deserialization is
not supported”), **`NbtAsJsonSerializer`**, book **page** serializers, and **chat component** decode paths that throw.

**Workaround:** Treat your Kotlin project as the **source of truth**; inspect **generated files** under `path` / zip.
For contributors extending serializers, see [Arguments](/docs/contributing/arguments) and the relevant feature docs.

### Custom components and `@SerialName`

**Category:** Kore limitation

When you define **[custom components](/docs/concepts/components)** backed by NBT, property names sometimes need an
explicit **`@SerialName("vanilla_json_key")`** because the **KNBT** stack does not always follow the same naming path as
Kore’s JSON `namingStrategy` for that shape. The guide shows this in context (for example the `UseComponent` example
with `durability_damages`).

**Workaround:** Copy patterns from [Components](/docs/concepts/components) and match the keys your target **pack format
** expects (use Minecraft’s registry JSON or Kore’s generators as reference, not guesswork).

---

## Documented DSL / helper caps

These are **not Kore bugs**; Kore documents them so authors know what the DSL will and will not do.

### Command macros

**Category:** Documented DSL / helper cap

[Macros](/docs/commands/macros) require **Minecraft 1.20.2+** for the feature itself. Kore’s doc lists DSL limits:
macros only in **functions**, **no type checking**, and macros are **not** threaded through every possible command
argument slot.

**Workaround:** Prefer normal Kotlin command builders where types matter; keep macro surfaces small and **test in-game
**.

### Markdown renderer (`helpers`)

**Category:** Documented DSL / helper cap

The [Markdown renderer](/docs/helpers/markdown-renderer) turns Markdown into **text components**. The helper lists
features Minecraft **cannot** represent (images, tables, fenced code blocks, etc.)
in [Not supported (Minecraft limitations)](/docs/helpers/markdown-renderer#not-supported-minecraft-limitations).

**Workaround:** Stay in the **supported** subset documented on that page, or build **`textComponent`** trees by hand.

## Quick reference

| Need                  | Where to look                                                                            |
|-----------------------|------------------------------------------------------------------------------------------|
| Tune JSON output      | [Configuration](/docs/guides/configuration) (`prettyPrint`, comments, paths)             |
| Folder vs zip vs jar  | [Creating A Datapack — Generation](/docs/guides/creating-a-datapack#generation)          |
| Import external packs | [Bindings](/docs/advanced/bindings)                                                      |
| Module layout         | [From Datapacks to Kore](/docs/guides/from-datapacks-to-kore)                            |
| Report a bug or gap   | [Kore on GitHub](https://github.com/Ayfri/Kore/issues) with pack format and Kore version |
