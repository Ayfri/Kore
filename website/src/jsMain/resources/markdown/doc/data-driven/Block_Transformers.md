---
root: .components.layouts.MarkdownLayout
title: Block Transformers - Strip, Scrape and Till Blocks with Items in Kore
nav-title: Block Transformers
description: Define block transformer files for Minecraft 26.3+ with Kore's Kotlin DSL. Turn the clicked block into another one with rules, sounds, loot and particles, and link them to item components.
keywords: minecraft block transformer, datapack block_transformer, kore block transformer, custom axe strip logs datapack, block_transformer item component, custom hoe shovel datapack
date-created: 2026-10-23
date-modified: 2026-10-23
routeOverride: /docs/data-driven/block-transformers
---

# Block Transformers

A block transformer is a data-driven JSON file listing the rules that turn the block an item is used on into another one,
like axes stripping logs, hoes tilling dirt, shovels flattening paths or honeycombs waxing copper. An item opts in through
the [`block_transformer` component](/docs/concepts/components#block-transformer), which points to the file by ID.

## File Structure

```
data/<namespace>/block_transformer/<name>.json
```

The file is a list of 1 to 200 rules, tried in order until one gives a block state. Vanilla ships `axe`, `hoe` and `shovel`.

## Creating a Block Transformer

Use `blockTransformer` on your `DataPack` and add rules with `rule`. Each rule takes a
[block state provider](/docs/data-driven/worldgen/features#block-state-providers) picking the new state, usually a
`ruleBasedStateProvider` checking the clicked block, and an optional block setting its options.
The function returns the ID of the file, ready to give to an item:

```kotlin
val strip = blockTransformer("strip_oak") {
	rule(ruleBasedStateProvider {
		rule(copyPropertiesProvider(simpleStateProvider(Blocks.STRIPPED_OAK_LOG, mapOf("axis" to "y")))) {
			matchingBlocks(Blocks.OAK_LOG)
		}
	}) {
		sound = SoundEvents.Item.Axe.STRIP
	}
	rule(simpleStateProvider(Blocks.DIRT_PATH)) {
		disallowedFaces = listOf(Direction.DOWN)
	}
}

val axe = Items.IRON_AXE {
	blockTransformer(strip)
}
```

Generated `data/<namespace>/block_transformer/strip_oak.json`:

```json
[
	{
		"block_state_provider": {
			"type": "minecraft:rule_based_state_provider",
			"rules": [
				{
					"if_true": {
						"type": "minecraft:matching_blocks",
						"blocks": "minecraft:oak_log"
					},
					"then": {
						"type": "minecraft:copy_properties_provider",
						"source": {
							"type": "minecraft:simple_state_provider",
							"state": {
								"id": "minecraft:stripped_oak_log",
								"properties": {
									"axis": "y"
								}
							}
						}
					}
				}
			]
		},
		"sound": "minecraft:item.axe.strip"
	},
	{
		"block_state_provider": {
			"type": "minecraft:simple_state_provider",
			"state": "minecraft:dirt_path"
		},
		"disallowed_faces": [
			"down"
		]
	}
]
```

A file without any rule, or with more than 200, throws when the pack is generated.
Inline rule lists on the item component aren't possible because the game only reads a block transformer from a file.

## Rule Options

| Option                | Type                         | Description                                                                                                             |
|-----------------------|------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `consumeOnUse`        | `Boolean?`                   | Whether the item is consumed on use, for stackable items only. Defaults to `true`.                                      |
| `disallowedFaces`     | `List<Direction>?`           | The clicked faces the rule ignores, the next rule is tried instead. Defaults to none, so every face works.              |
| `dropStrategy`        | `BlockTransformDropStrategy?` | `CLICKED_FACE` or `FROM_MIDDLE`, where `loot` drops. Defaults to `FROM_MIDDLE`.                                         |
| `itemDamagePerUse`    | `Int?`                       | The durability lost per use, at least `0`, for unstackable items only. Defaults to `0`.                                 |
| `loot`                | `LootTableArgument?`         | The loot table dropped on success. Defaults to nothing.                                                                 |
| `particle`            | `BlockTransformParticle?`    | `NONE`, `SCRAPE`, `WAX_OFF` or `WAX_ON`, spawned on success. Defaults to `NONE`.                                        |
| `sound`               | `SoundEventArgument?`        | The sound played on success. Defaults to none.                                                                          |
| `transformType`       | `BlockTransformType?`        | `SINGLE_BLOCK` or `COPPER_CHEST`, which also transforms the other half of a double copper chest. Defaults to `SINGLE_BLOCK`. |
| `updateFromNeighbors` | `Boolean?`                   | Whether the transformed block updates its shape from its neighbors. Defaults to `true`.                                 |

## See Also

- [Components](/docs/concepts/components#block-transformer) - The `blockTransformer` item component
- [Features](/docs/data-driven/worldgen/features#block-state-providers) - Block state providers picking the new block state
- [Loot Tables](/docs/data-driven/loot-tables) - The `loot` dropped by a rule

### External Resources

- [Minecraft Wiki: Data component format](https://minecraft.wiki/w/Data_component_format#block_transformer)
