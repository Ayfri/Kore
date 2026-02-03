---
root: .components.layouts.MarkdownLayout
title: Variants
nav-title: Variants
description: Define entity and painting variants with Kore's type-safe DSL
keywords: minecraft, datapack, kore, variants, cat, cow, chicken, frog, pig, wolf, painting
date-created: 2026-02-03
date-modified: 2026-02-03
routeOverride: /docs/data-driven/variants
---

# Variants

Variants allow you to customize the appearance and spawn conditions of various entities and paintings in Minecraft. Kore provides type-safe DSL builders for creating custom variants for cats, cows, chickens, frogs, pigs, wolves, and paintings.

## Entity Variants

### Spawn Conditions

Most entity variants support spawn conditions that control when and where the variant appears. Available condition types:

- **add(priority)**: Base condition with just a priority
- **biome(priority, biomes...)**: Spawn in specific biomes or biome tags
- **moonBrightness(priority, value)**: Spawn based on moon brightness (single value)
- **moonBrightness(priority, min, max)**: Spawn based on moon brightness (range)
- **structures(priority, structures...)**: Spawn near specific structures or structure tags

Higher priority values take precedence when multiple conditions match.

### Cat Variants

Cat variants define the texture and spawn conditions for cats. You can specify biomes, moon brightness, and structures as spawn conditions.

```kotlin
catVariant("test_cat_variant", Textures.Entity.Cat.TABBY) {
	spawnConditions {
		add(10)
		biome(5, Biomes.PLAINS)
		biome(2, Tags.Worldgen.Biome.IS_OVERWORLD)
		moonBrightness(1, 1.0)
		moonBrightness(1, 0.5, 0.75)
		structures(0, ConfiguredStructures.VILLAGE_PLAINS)
	}
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:entity/cat/tabby",
	"spawn_conditions": [
		{
			"priority": 10
		},
		{
			"priority": 5,
			"condition": {
				"type": "minecraft:biome",
				"biomes": "minecraft:plains"
			}
		},
		{
			"priority": 2,
			"condition": {
				"type": "minecraft:biome",
				"biomes": "#minecraft:is_overworld"
			}
		},
		{
			"priority": 1,
			"condition": {
				"type": "minecraft:moon_brightness",
				"range": 1.0
			}
		},
		{
			"priority": 1,
			"condition": {
				"type": "minecraft:moon_brightness",
				"range": {
					"min": 0.5,
					"max": 0.75
				}
			}
		},
		{
			"priority": 0,
			"condition": {
				"type": "minecraft:structure",
				"structures": "minecraft:village_plains"
			}
		}
	]
}
```

### Cow Variants

Cow variants define the texture, model, and spawn conditions for cows.

```kotlin
cowVariant("test_cow_variant", Textures.Entity.Cow.COLD_COW, CowModel.COLD) {
	spawnConditions {
		structures(0, Tags.Worldgen.Structure.MINESHAFT)
	}
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:entity/cow/cold_cow",
	"model": "cold",
	"spawn_conditions": [
		{
			"priority": 0,
			"condition": {
				"type": "minecraft:structure",
				"structures": "#minecraft:mineshaft"
			}
		}
	]
}
```

### Chicken Variants

Chicken variants define the texture, model, and spawn conditions for chickens.

```kotlin
chickenVariant("test_chicken_variant", Textures.Entity.Chicken.TEMPERATE_CHICKEN, ChickenModel.NORMAL) {
	spawnConditions {
		structures(0, Tags.Worldgen.Structure.VILLAGE)
	}
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:entity/chicken/temperate_chicken",
	"model": "normal",
	"spawn_conditions": [
		{
			"priority": 0,
			"condition": {
				"type": "minecraft:structure",
				"structures": "#minecraft:village"
			}
		}
	]
}
```

### Frog Variants

Frog variants define the texture and spawn conditions for frogs.

```kotlin
frogVariant("test_frog_variant", Textures.Entity.Frog.TEMPERATE_FROG) {
	spawnConditions {
		add(10)
		biome(5, Biomes.SNOWY_PLAINS)
		biome(2, Tags.Worldgen.Biome.SPAWNS_COLD_VARIANT_FROGS)
		moonBrightness(1, 1.0)
		moonBrightness(1, 0.5, 0.75)
		structures(0, ConfiguredStructures.END_CITY)
	}
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:entity/frog/temperate_frog",
	"spawn_conditions": [
		{
			"priority": 10
		},
		{
			"priority": 5,
			"condition": {
				"type": "minecraft:biome",
				"biomes": "minecraft:snowy_plains"
			}
		},
		{
			"priority": 2,
			"condition": {
				"type": "minecraft:biome",
				"biomes": "#minecraft:spawns_cold_variant_frogs"
			}
		},
		{
			"priority": 1,
			"condition": {
				"type": "minecraft:moon_brightness",
				"range": 1.0
			}
		},
		{
			"priority": 1,
			"condition": {
				"type": "minecraft:moon_brightness",
				"range": {
					"min": 0.5,
					"max": 0.75
				}
			}
		},
		{
			"priority": 0,
			"condition": {
				"type": "minecraft:structure",
				"structures": "minecraft:end_city"
			}
		}
	]
}
```

### Pig Variants

Pig variants define the texture, model, and spawn conditions for pigs.

```kotlin
pigVariant("test_pig_variant", Textures.Entity.Pig.COLD_PIG, PigModel.COLD) {
	spawnConditions {
		structures(0, Tags.Worldgen.Structure.ON_TREASURE_MAPS)
	}
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:entity/pig/cold_pig",
	"model": "cold",
	"spawn_conditions": [
		{
			"priority": 0,
			"condition": {
				"type": "minecraft:structure",
				"structures": "#minecraft:on_treasure_maps"
			}
		}
	]
}
```

### Wolf Variants

Wolf variants define separate textures for angry, tame, and wild states, along with spawn conditions.

```kotlin
wolfVariant("test_wolf_variant") {
	assets(
		angry = Textures.Entity.Wolf.WOLF_STRIPED,
		tame = Textures.Entity.Wolf.WOLF_RUSTY_ANGRY,
		wild = Textures.Entity.Wolf.WOLF_BLACK,
	)
	spawnConditions {
		biome(5, Biomes.OCEAN, Biomes.SNOWY_SLOPES)
	}
}
```

Produces JSON:

```json
{
	"assets": {
		"angry": "minecraft:entity/wolf/wolf_striped",
		"tame": "minecraft:entity/wolf/wolf_rusty_angry",
		"wild": "minecraft:entity/wolf/wolf_black"
	},
	"spawn_conditions": [
		{
			"priority": 5,
			"condition": {
				"type": "minecraft:biome",
				"biomes": [
					"minecraft:ocean",
					"minecraft:snowy_slopes"
				]
			}
		}
	]
}
```

### Wolf Sound Variants

Wolf sound variants define custom sounds for wolves. Sound variants are independent of color variants and spawning biome. Wolves will make the sounds associated with their variant when they bark, pant, whine, growl, die, or get hurt.

```kotlin
wolfSoundVariant("funny") {
	ambientSound = SoundEvents.Entity.Pig.AMBIENT
	deathSound = SoundEvents.Entity.Creeper.DEATH
	growlSound = SoundEvents.Entity.Player.LEVELUP
	hurtSound = SoundEvents.Entity.Zombie.HURT
	pantSound = SoundEvents.Entity.EnderDragon.FLAP
	whineSound = SoundEvents.Entity.Cat.PURR
}
```

Produces JSON:

```json
{
	"ambient_sound": "minecraft:entity.pig.ambient",
	"death_sound": "minecraft:entity.creeper.death",
	"growl_sound": "minecraft:entity.player.levelup",
	"hurt_sound": "minecraft:entity.zombie.hurt",
	"pant_sound": "minecraft:entity.ender_dragon.flap",
	"whine_sound": "minecraft:entity.cat.purr"
}
```

## Other Variants

### Painting Variants

Painting variants define custom paintings with dimensions and optional metadata like author and title.

```kotlin
// Basic painting variant
paintingVariant(
	assetId = Textures.Painting.KEBAB,
	height = 16,
	width = 16
)
```

Produces JSON:

```json
{
	"asset_id": "minecraft:kebab",
	"height": 16,
	"width": 16
}
```

With default dimensions (1x1):

```kotlin
paintingVariant(
	assetId = Textures.Painting.AZTEC
)
```

Produces JSON:

```json
{
	"asset_id": "minecraft:aztec",
	"height": 1,
	"width": 1
}
```

With author and title:

```kotlin
paintingVariant(
	assetId = Textures.Painting.AZTEC,
) {
	author = textComponent("Ayfri")
	title = textComponent("Aztec")
}
```

Produces JSON:

```json
{
	"asset_id": "minecraft:aztec",
	"height": 1,
	"width": 1,
	"author": "Ayfri",
	"title": "Aztec"
}
```

### See also

- [Chat Components](../concepts/chat_components) - For painting title and author text formatting
