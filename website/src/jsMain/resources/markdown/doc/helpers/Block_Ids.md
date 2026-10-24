---
root: .components.layouts.MarkdownLayout
title: Block IDs
nav-title: Block IDs
description: Read the block under or at any position as an integer score with Kore, one generated context int provider and one compute block command, no setblock tests.
keywords: minecraft, datapack, kore, helpers, block, id, compute, context provider, match_block, tags, scoreboard
date-created: 2026-10-24
date-modified: 2026-10-24
routeOverride: /docs/helpers/block-ids
---

# Block IDs

`blockIdProvider()` generates a [context int provider](/docs/data-driven/loot-tables#context-provider-files) that turns
the block read by [`compute block`](/docs/commands/commands#compute-command) into a number, so one command stores
"which block is this" in a score instead of one `execute if block` per candidate.

```kotlin
val ids = blockIdProvider()
val below = vec3(0.relativePos, (-1).relativePos, 0.relativePos)

function("check_ground") {
	execute {
		storeResult { score(self(), "ground") }
		run { compute(below, ids) }
	}
	execute {
		ifCondition { score(self(), "ground", rangeOrInt(blockId(Blocks.SLIME_BLOCK))) }
		run { say("Bounce!") }
	}
}
```

```mcfunction
execute store result score @s ground run compute block ~ ~-1 ~ integer my_pack:kore_block_id
execute if score @s ground matches 1005 run say Bounce!
```

> [!FOOTPRINT]
> - **Objectives:** none, the score you store the result in is yours.
> - **Storage:** none.
> - **Entities:** none.
> - **Forceloaded chunks:** none.
> - **Every tick:** no.
> - **Load and files:** the context int provider `kore_block_id` and one block tag per bit (`kore_block_id/bit_0`, `kore_block_id/bit_1`...), generated once per pack however many times `blockIdProvider()` is called.

## API overview

| Function                     | Result                                                                                 |
|------------------------------|----------------------------------------------------------------------------------------|
| `DataPack.blockIdProvider()` | The `ContextIntProviderArgument` of `<pack>:kore_block_id`, created on the first call. |
| `blockId(block)`             | The ID of a block as an `Int` known at build time, to compare the score against.       |
| `blockIdBits`                | The number of bits of an ID, which is the number of generated tags.                    |

An ID is the 1-based position of the block in Kore's generated `Blocks` enum, so `blockId(Blocks.ACACIA_BUTTON)` is `1`.
A block Kore doesn't know, a modded one for instance, has the ID `0`, and block states are ignored: a stair facing any
side, upside down or not, has the ID of `Blocks.OAK_STAIRS`.

IDs follow the block list of the Minecraft version Kore targets, so they change between versions. Compare against
`blockId(...)` instead of writing numbers by hand, and never store an ID across a version update.

## How it works

The provider sums one [`match_block`](/docs/data-driven/predicates) check per bit of the ID. Each check tests the block
against a generated block tag listing every block whose ID has that bit set, and adds `2^bit` when it passes:

```json
{
	"type": "minecraft:add",
	"inputs": [
		{
			"type": "minecraft:conditional",
			"condition": {
				"type": "minecraft:match_block",
				"blocks": "#my_pack:kore_block_id/bit_0"
			},
			"on_true": 1
		},
		...
	]
}
```

With the roughly 1300 blocks of the current versions, an ID takes 11 bits (`blockIdBits`), so each evaluation runs 11 `match_block` checks: use it where
one `compute` per tick or per event is enough, and keep a plain `execute if block` for a single known block.

## Limits

- `match_block` only reads the block of a `compute block <pos>` command. In `compute default` or `compute entity`, no check
  passes and the result is `0`.
- A block that breaks or changes as soon as it is placed (a rail without support, a plant without ground) can't be read
  as the block you placed: the provider reads the block that is really there.

## See also

- [Commands](/docs/commands/commands#compute-command) - The `compute` command and the other context providers.
- [Loot Tables](/docs/data-driven/loot-tables#context-provider-files) - Context provider files.
- [Tags](/docs/data-driven/tags) - The block tags the provider is built from.
