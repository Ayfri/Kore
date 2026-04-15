---
root: .components.layouts.MarkdownLayout
title: "Contributing: Creating a New Generator"
nav-title: "Creating a New Generator"
description: Step-by-step contributor guide for adding a new data-driven generator to Kore, from model and registration to tests and documentation.
keywords: contributing, datapack, generator, kore, patterns, tests
date-created: 2026-04-15
date-modified: 2026-04-15
routeOverride: /docs/contributing/creating-a-new-generator
---

# Contributing: Creating a New Generator

This page is the end-to-end recipe for adding a new data-driven generator in `kore`.

The examples below use a *fictional* `custom_reward` resource so the shape stays easy to transpose to real Minecraft
schemas.

## 1) Start from the closest existing feature

Before creating files, pick the nearest generator already in Kore and mirror its structure.

Good references, in alphabetical order:

- [`DamageType`][damage-type]
- [`Instrument`][instrument-feature]
- [`PaintingVariant`][painting-variant]

What you want to mirror:

- `DataPack` registration.
- `DataPack` extension function.
- `Generator` inheritance and path behavior.
- Package layout.
- Test shape.

If the feature needs a brand-new registry argument type, add it through the generation pipeline first. Do **not**
hand-edit `kore/src/main/generated` or `build/generated/...`.

## 2) Create the feature class

Place the feature in the matching package under `kore/src/main/kotlin/io/github/ayfri/kore/features/...`.

```kotlin
package io.github.ayfri.kore.features.customrewards

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class CustomReward(
	@Transient
	override var fileName: String = "starter_kit",
	var displayName: ChatComponents = textComponent(),
	var lootTable: LootTableArgument,
	var cooldown: Int = 0,
) : Generator("custom_reward") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}
```

Checklist for the class itself:

- Extend [`Generator`][generator-kt] with the correct `resourceFolder`.
- Keep `generateJson(dataPack)` thin and delegate to the shared encoder when the structure is straightforward.
- Mark `fileName` as `@Transient`.
- Make the feature `@Serializable`.
- Only override `getPathFromDataDir(...)` when the resource path is genuinely special.

## 3) Register the generator in `DataPack`

Add the new list in [`DataPack.kt`][datapack-kt], in alphabetical order with the other registered generators.

```kotlin
val customRewards = registerGenerator<CustomReward>()
```

This is what makes the feature participate in the normal datapack generation lifecycle.

## 4) Add the `DataPack` extension function

The extension function is the public builder entry point.

```kotlin
fun DataPack.customReward(
	fileName: String = "starter_kit",
	lootTable: LootTableArgument,
	displayName: ChatComponents = textComponent(),
	cooldown: Int = 0,
	init: CustomReward.() -> Unit = {},
): CustomRewardArgument {
	val customReward = CustomReward(fileName, displayName, lootTable, cooldown).apply(init)
	customRewards += customReward
	return CustomRewardArgument(fileName, customReward.namespace ?: name)
}
```

Keep it consistent with the rest of Kore:

- Add the instance to the registered list.
- Apply `init` last.
- Instantiate the feature once.
- Return the matching typed argument.

If the resource can be referenced by tags, preserve the corresponding `*OrTagArgument` pattern as well.

## 5) Reuse serializers and argument types

Before adding custom serialization logic, check [Contributing: Architecture and Patterns][architecture].

In practice, the decision order is:

1. Reuse an existing serializer.
2. Reshape the model if that makes an existing serializer work.
3. Add a new serializer only when the first two options fail.

This keeps JSON output aligned with the rest of Kore instead of creating one-off conventions.

## 6) Add tests immediately

Generator changes in `kore` should ship with targeted tests under [
`kore/src/test/kotlin/io/github/ayfri/kore`][kore-tests].

For a simple resource, the test usually follows this shape:

```kotlin
package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.assertions.assertGeneratorsGenerated
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.features.customrewards.customReward
import io.github.ayfri.kore.generated.LootTables
import io.github.ayfri.kore.utils.pretty
import io.github.ayfri.kore.utils.testDataPack
import io.kotest.core.spec.style.FunSpec

fun DataPack.customRewardTests() {
	customReward("starter_kit", lootTable = LootTables.Chests.SPAWN_BONUS_CHEST) {
		cooldown = 200
	}

	customRewards.last() assertsIs """
		{
			"display_name": "",
			"loot_table": "minecraft:chests/spawn_bonus_chest",
			"cooldown": 200
		}
	""".trimIndent()
}

class CustomRewardTests : FunSpec({
	test("custom reward") {
		testDataPack("custom_reward") {
			pretty()
			customRewardTests()
		}.apply {
			assertGeneratorsGenerated()
			generate()
		}
	}
})
```

At minimum, validate:

- Edge cases or regressions specific to the feature.
- The emitted JSON shape.
- The generated file path.

## 7) Update documentation in the same PR

Every user-visible feature needs docs under [`website/src/jsMain/resources/markdown/doc`][docs-root].

Typical documentation work includes:

- A contributor note when the implementation pattern is non-obvious.
- A user-facing usage page when the feature becomes public API.
- Updated links from entry pages when navigation changes.

For page placement, routing, and required frontmatter keys, reuse the documentation contract described
in [Contributing: Architecture and Patterns][architecture].

## 8) Validate before opening the PR

For `kore` changes, the baseline check is:

```bash
./gradlew :kore:runUnitTests
```

Before opening the PR, verify that:

- `DataPack` registration remains alphabetically ordered.
- Docs reflect the final API shape.
- No generated file was hand-edited.
- Tests cover the feature.

## 9) Done checklist

You are usually done when the feature includes all of the following:

- A `DataPack` extension function returning the typed argument.
- A `DataPack` registration entry.
- A `Generator` subclass with the correct path behavior.
- Documentation updates in `website/.../markdown/doc`.
- Tests in `kore/src/test`.

## See also

- [Contributing: Architecture and Patterns][architecture]
- [Contributing: Contributing][contributing]
- [Contributing: Workflow][workflow]

[architecture]: /docs/contributing/architecture-and-patterns

[contributing]: /docs/contributing/contributing

[damage-type]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/features/damagetypes/DamageType.kt

[datapack-kt]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/DataPack.kt

[docs-root]: https://github.com/ayfri/kore/tree/master/website/src/jsMain/resources/markdown/doc

[generator-kt]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/Generator.kt

[instrument-feature]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/features/instruments/Instrument.kt

[kore-tests]: https://github.com/ayfri/kore/tree/master/kore/src/test/kotlin/io/github/ayfri/kore

[painting-variant]: https://github.com/ayfri/kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/features/paintingvariant/PaintingVariant.kt

[workflow]: /docs/contributing/contributing-workflow