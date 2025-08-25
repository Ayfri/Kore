---
root: .components.layouts.MarkdownLayout
title: Item Modifiers
nav-title: Item Modifiers
description: Apply loot functions to items using Kore’s DSL, with predicates and components support.
keywords: minecraft, datapack, kore, item modifiers, loot functions, /item modify, components, predicates
date-created: 2025-08-11
date-modified: 2025-08-11
routeOverride: /docs/item-modifiers
---

# Item Modifiers

Item modifiers (also called loot functions) let you transform item stacks: change counts, add components, set names/lore, copy data, and more. Kore exposes a concise DSL over the vanilla format so you can define modifiers in code and use them in commands and loot tables.

For the vanilla reference, see the [Minecraft Wiki – Item modifier](https://minecraft.wiki/w/Item_modifier).

## Quick start

Create a modifier and apply it with the `/item modify` command:

```kotlin
import io.github.ayfri.kore.arguments.WEAPON
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.features.itemmodifiers.itemModifier
import io.github.ayfri.kore.features.itemmodifiers.functions.*
import io.github.ayfri.kore.features.predicates.conditions.*
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.generated.MapDecorationTypes
import io.github.ayfri.kore.generated.Tags

val modifier = itemModifier("explorer_horn") {
	// Vanilla function: minecraft:exploration_map
	explorationMap {
		destination = Tags.Worldgen.Structure.MINESHAFT
		decoration = MapDecorationTypes.BANNER_BLACK
		skipExistingChunks = true

		// Predicates: only apply when raining and a coin-flip passes
		conditions {
			randomChance(0.5f)
			weatherCheck(raining = true)
		}
	}

	// Vanilla function: minecraft:set_instrument
	setInstrument(Tags.Instrument.GOAT_HORNS)
}

load {
	items {
		modify(self(), WEAPON.MAINHAND, modifier)
	}
}
```

This produces the exact JSON the game expects, while keeping everything type-safe and composable in Kotlin.

## Where modifiers live in Kore

- Builder entry-point: `dataPack.itemModifier(..)` returns an `ItemModifierArgument` you can reference elsewhere.
- Internal data: `ItemModifier` stores an inlinable list of `ItemFunction` objects and serialises directly to a JSON array when saved.
- Each `ItemFunction` accepts optional `conditions { .. }`, which are regular Predicates.

See `kore/features/itemmodifiers/ItemModifier.kt` for the container type and `kore/features/itemmodifiers/functions/*` for all functions.

## Working with components

Many transformations map 1:1 to item components. A few common examples:

```kotlin
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color

// Add or remove components atomically
itemModifier("components_patch") {
	setComponents {
		customName(textComponent("Legendary", Color.AQUA))
		!damage(5)          // remove the damage component
		setToRemove("foo")  // remove custom component by id
	}
}

// Replace or edit contents (bundles, containers, charged projectiles)
itemModifier("contents") {
	setContents(ContentComponentTypes.BUNDLE_CONTENTS) {
		entries {
			// full Loot Table entry DSL
		}
	}
}

// Lore with insert/append/replace modes
itemModifier("lore") {
	setLore {
		lore("Line 1", Color.BLACK)
		lore("Line 2")
		mode(Mode.INSERT, offset = 0)
	}
}

// Custom-model-data lists, with per-list modes
itemModifier("cmd") {
	setCustomModelData(
		colors = listOf(Color.RED, Color.BLUE),
		flags = listOf(true, false)
	) {
		colors?.mode(Mode.REPLACE_ALL)
		flags?.mode(Mode.APPEND)
	}
}
```

For a general overview of item components and their builders, see [Components](/docs/components).

## Conditions on functions (Predicates)

Every item function can define `conditions { .. }`. These are standard Kore Predicates and support all the same helpers (random chance, weather, scores, entity checks, etc.). This mirrors the vanilla “conditions” array on loot functions.

```kotlin
itemModifier("conditional") {
	setName("Storm Blade") {
		target = SetNameTarget.ITEM_NAME
		conditions { weatherCheck(raining = true) }
	}
}
```

Learn more in [Predicates](/docs/predicates).

## Composition and nesting

- `filtered { itemFilter { .. } modifiers { .. } }` – Run nested modifier list if the item matches an `ItemStack` filter.
- `modifyContents(..) { modifiers { .. } }` – Apply nested modifiers to items inside a component (e.g., bundle contents).
- `sequence { .. }` – Inline a sequence of functions as a single step.
- `reference(myModifier)` – Reuse another item modifier by name.

## Selected function examples

Alphabetical sampling of common functions. All names match vanilla functions and are available in the `functions` package.

- `applyBonus(enchantment, formula)`
- `copyComponents(include/exclude, source)`
- `copyCustomData(source, ops)`
- `copyName(source)`
- `copyState(block, properties)`
- `enchantRandomly(options, onlyCompatible)`
- `enchantWithLevels(options, levels)`
- `enchantedCountIncrease(enchantment, count, limit)`
- `explorationMap(destination, decoration, zoom, searchRadius, skipExistingChunks)`
- `explosionDecay()`
- `fillPlayerHead(entity)`
- `filtered { .. }`
- `furnaceSmelt()`
- `limitCount(limit)`
- `modifyContents(component) { modifiers { .. } }`
- `reference(name | ItemModifierArgument)`
- `sequence { .. }`
- `setAttributes { attribute(..) }`
- `setBannerPattern(patterns, append)`
- `setBookCover(title, author, generation)`
- `setComponents { .. }`
- `setContents(component) { entries { .. } }`
- `setCount(count, add)`
- `setCustomData(nbt)`
- `setCustomModelData(colors | flags | floats | strings)`
- `setDamage(damage, add)`
- `setEnchantments { map }`
- `setFireworkExplosion(shape) { .. }`
- `setFireworks(flightDuration) { explosions { explosion(..) } }`
- `setInstrument(tag)`
- `setItem(item)`
- `setLore(..)`
- `setLootTable(type, name | LootTableArgument, seed)`
- `setOminousBottleAmplifier(amplifier)`
- `setPotion(potion)`
- `setStewEffect { potionEffect(..) }`
- `setWritableBookPages(pages, generation)`
- `setWrittenBookPages(pages)`
- `toggleTooltips(toggles)`

See the test suite [`ItemModifierTests.kt`](https://github.com/Ayfri/Kore/blob/master/kore/src/test/kotlin/io/github/ayfri/kore/features/ItemModifierTests.kt) for end-to-end JSON expectations that mirror vanilla.

## Using modifiers in commands

Use inside a function (e.g., in your load/setup) and call `/item modify` via the DSL:

```kotlin
import io.github.ayfri.kore.commands.items
import io.github.ayfri.kore.arguments.types.literals.self

load {
	items {
		modify(self(), WEAPON.MAINHAND, modifier)
	}
}
```

## Tips and best practices

1. Keep modifiers focused and reusable; prefer composing small helpers with `reference(..)`.
2. Use `conditions { .. }` to guard expensive or situational changes.
3. Prefer component-based edits (`setComponents`, `setContents`, `setFireworks`, etc.) for clarity and future-proofing.
4. When editing lists, pick the right mode: `REPLACE_ALL`, `APPEND`, or `INSERT` with offsets.

## See also

- [Predicates](/docs/predicates) – conditions for item functions
- [Components](/docs/components) – building and understanding item components
- [Inventory Manager](/docs/helpers/inventory-manager) – maintain item states in GUIs/slots
- Vanilla reference: [Minecraft Wiki – Item modifier](https://minecraft.wiki/w/Item_modifier)
