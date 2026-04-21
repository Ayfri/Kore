---
root: .components.layouts.MarkdownLayout
title: "Contributing: Arguments Internals"
nav-title: Arguments Internals
description: Contributor-facing overview of Kore's argument system, including Argument, literals, resource-location wrappers, and tag-aware abstractions.
keywords: minecraft, datapack, kore, arguments, internals, contributors, resources, tags, commands
date-created: 2026-04-21
date-modified: 2026-04-21
routeOverride: /docs/contributing/arguments
---

# Contributing: Arguments Internals

This page is mostly for contributors and advanced readers who want to understand how Kore models command inputs
internally.

If you are just using Kore to build a datapack, you usually do **not** need to learn the full argument architecture.
The more practical user-facing pages are [Selectors](/docs/concepts/selectors), [Functions](/docs/commands/functions),
and the [Cookbook](/docs/guides/cookbook).

## Why the argument layer exists

Kore models most command inputs as typed Kotlin values that implement the `Argument` interface. Instead of assembling
raw command strings everywhere, builders pass values that know how to serialize themselves through `asString()`.

This is one of the core reasons the DSL stays readable and safe to refactor.

Typical examples:

- `allPlayers()` serializes to `@a`
- `Items.DIAMOND_SWORD` serializes to `minecraft:diamond_sword`
- a tag wrapper serializes to `#minecraft:planks`
- `literal("replace")` serializes to a raw command token

## The pieces contributors should know

### `Argument` is the common contract

At the bottom, Kore uses the `Argument` interface as the shared abstraction for command-safe values.

This gives the command layer a uniform way to serialize:

- selectors
- generated resource enums
- tag wrappers
- literals
- some range-like and time-like values

When adding a new command wrapper or argument family, prefer plugging into that existing contract rather than passing
plain strings around.

### Literal helpers keep raw tokens explicit

Literal helpers are used when a command slot expects a keyword-like token instead of a resource wrapper.

Common examples include:

- `literal("value")`
- `bool(true)` / `bool(false)`
- `all()` for wildcard score holders
- numeric wrappers such as `int(...)` and `float(...)`

They make raw command assembly explicit while still fitting the same typed pipeline.

### Selectors are just another argument family

Selectors are user-facing, but internally they matter because they show the expected Kore pattern well: build a typed
object first, then serialize late.

```kotlin
val nearestZombie = allEntities(limitToOne = true) {
	type = EntityTypes.ZOMBIE
	sort = Sort.NEAREST
}

function("cleanup") {
	kill(nearestZombie)
}
```

For selector usage itself, see [Selectors](/docs/concepts/selectors).

### `ResourceLocationArgument` is the main user-facing thing worth knowing

Even though most users do not need the architecture details, one concept *is* worth knowing: a large part of Kore is
generated as enums or wrappers that implement resource-location-based argument interfaces.

This is why you can write things like:

- `Items.DIAMOND_SWORD`
- `Blocks.STONE`
- `Sounds.Entity.Player.LEVELUP`
- generated predicates, loot tables, functions, and tags

Those values serialize to the namespaced IDs Minecraft expects, which means users usually do not need to handwrite
`minecraft:...` strings.

```kotlin
function("starter_kit") {
	give(allPlayers(), Items.DIAMOND_SWORD)
	playsound(Sounds.Entity.Player.LEVELUP, self())
}
```

When documenting Kore for users, this is usually the part to emphasize: generated enums and wrappers already model many
Minecraft registries.

### Tag-aware parent interfaces preserve vanilla flexibility

Some command slots accept either a concrete resource or a tag. Kore models this with dedicated parent interfaces such
as:

- `BlockOrTagArgument`
- `ItemOrTagArgument`
- `FunctionOrTagArgument`

When the tag variant is used, it serializes with the vanilla `#namespace:name` syntax.

This is an important contributor pattern: if vanilla accepts both a resource and its tag counterpart, Kore usually wants
an umbrella interface instead of two unrelated overloads.

## When raw strings are still appropriate

Raw strings still make sense when:

- a new Minecraft command has not been wrapped yet
- a mod command has no Kore DSL yet
- you deliberately use `addLine(...)` or `command(...)`

Even then, contributors should prefer composing raw command assembly with existing typed arguments rather than reverting
to fully handwritten command strings.

## Practical contributor checklist

When you add or modify argument-related APIs, check the following:

1. Can this reuse `Argument` instead of a plain `String`?
2. Should the value be a literal helper, a resource wrapper, or a tag-aware parent type?
3. Does vanilla allow both resource and tag forms?
4. Is there already a generated enum or wrapper that should be reused instead of inventing a new string API?
5. Do docs for regular users really need this detail, or should it stay in contributor-facing documentation?

## See also

- [Selectors](/docs/concepts/selectors) - user-facing selector builders built on the same typed model
- [Functions](/docs/commands/functions) - function builders that consume many argument types
- [Architecture and Patterns](/docs/contributing/architecture-and-patterns) - broader contributor-facing project
  patterns
- [Cookbook](/docs/guides/cookbook) - practical usage patterns rather than internals
- [Minecraft Wiki: Argument types](https://minecraft.wiki/w/Argument_types) - vanilla command argument reference