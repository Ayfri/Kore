---
root: .components.layouts.MarkdownLayout
title: Fabric Resource Conditions
nav-title: Fabric Resource Conditions
description: "Load Kore-generated recipes, advancements and loot tables conditionally in Fabric mods, gated on mods, tags, registries or feature flags."
keywords: minecraft, datapack, kore, fabric, resource conditions, datagen, load conditions, mod
date-created: 2026-07-21
date-modified: 2026-07-21
routeOverride: /docs/guides/fabric-resource-conditions
position: 4
---

# Fabric Resource Conditions

[Fabric API resource conditions](https://docs.fabricmc.net/develop/resource-conditions) let the game load a data file
only when some runtime condition holds: another mod is present, a tag is populated, a registry contains an entry, or a
feature flag is enabled. Fabric reads them from a `fabric:load_conditions` array at the root of the JSON.

Kore can attach these conditions to **any generator** (recipes, advancements, loot tables, predicates, item modifiers,
worldgen, ...). The array is injected at the JSON root during generation, so the same Kore code produces a plain
datapack for vanilla and a conditionally-loaded one when shipped inside a Fabric mod.

This pairs well with generating straight into Fabric Loom's datagen folder, see
[Creating a DataPack](/docs/guides/creating-a-datapack#separating-the-output-folder-name-from-the-namespace).

## Attaching conditions

Call `fabricLoadConditions { }` inside any generator builder. Each factory both creates a condition and adds it:

```kotlin
dataPack("mymod") {
	advancement("secret") {
		fabricLoadConditions {
			allModsLoaded("create")
			not(anyModsLoaded("badmod"))
		}
		// ... advancement content
	}
}
```

Produces:

```json
{
	"fabric:load_conditions": [
		{
			"condition": "fabric:all_mods_loaded",
			"values": ["create"]
		},
		{
			"condition": "fabric:not",
			"value": {
				"condition": "fabric:any_mods_loaded",
				"values": ["badmod"]
			}
		}
	],
	"criteria": {}
}
```

## Available conditions

| DSL call | Condition | Succeeds when |
|----------|-----------|---------------|
| `allModsLoaded("a", "b")` | `fabric:all_mods_loaded` | every listed mod is loaded |
| `anyModsLoaded("a", "b")` | `fabric:any_mods_loaded` | at least one listed mod is loaded |
| `tagsPopulated("minecraft:logs")` | `fabric:tags_populated` | every tag is populated (in `minecraft:item` by default) |
| `tagsPopulated("stone", registry = "minecraft:block")` | `fabric:tags_populated` | every tag is populated in the given registry |
| `registryContains("mymod:custom")` | `fabric:registry_contains` | the registry (`minecraft:item` by default) contains every id |
| `registryContains("mymod:tree", registry = "worldgen/configured_feature")` | `fabric:registry_contains` | the given registry contains every id |
| `featuresEnabled("minecraft:vanilla")` | `fabric:features_enabled` | every feature flag is enabled |
| `conditionTrue()` | `fabric:true` | always |

### Combinators

`not(...)` inverts a single condition. `and { }` and `or { }` open nested scopes that combine the conditions declared
inside them:

```kotlin
lootTable("rare_drop") {
	fabricLoadConditions {
		and {
			tagsPopulated("c:ingots/copper")
			featuresEnabled("minecraft:vanilla")
		}
		or {
			registryContains("minecraft:diamond")
			registryContains("mymod:custom_block", registry = "minecraft:block")
		}
	}
	// ... loot table content
}
```

The `registry` argument is optional and defaults to `minecraft:item` on the Fabric side, so it is omitted from the JSON
when you leave it out.

## Limitation: object-form resources only

Conditions are injected at the JSON **root**, which must be an object. That covers recipes, advancements, loot tables,
and single-entry predicates or item modifiers.

Predicates and item modifiers with **multiple** entries serialize as a top-level JSON array, which has no root to attach
`fabric:load_conditions` to. Kore emits a warning and generates the file without conditions in that case. Wrap the logic
differently (for example a single `allOf`/`anyOf` predicate) if you need conditions on it.
