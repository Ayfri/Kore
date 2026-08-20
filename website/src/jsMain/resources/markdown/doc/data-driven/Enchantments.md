---
root: .components.layouts.MarkdownLayout
title: Enchantments
nav-title: Enchantments
description: Create custom Minecraft enchantments using Kore's type-safe Kotlin DSL with support for all vanilla effect components and level-based values.
keywords: minecraft, datapack, kore, enchantments, effects, custom enchantments
date-created: 2025-03-02
date-modified: 2026-08-20
routeOverride: /docs/data-driven/enchantments
---

# Enchantments

Enchantments are data-driven definitions that modify item behavior, apply effects, change damage calculations, and alter various game mechanics. In Minecraft Java Edition 1.21+, enchantments are fully customizable through data packs, allowing you to create entirely new enchantments with unique effects.

## Overview

Custom enchantments have several key characteristics:

- **Data-driven**: Defined as JSON files in data packs, not hardcoded
- **Effect components**: Modular system of 30+ effect types
- **Level-based scaling**: Values can scale with enchantment level
- **Slot-aware**: Effects apply based on equipment slot configuration
- **Conditional**: Effects can have predicate requirements

### Enchantment Properties

Every enchantment defines these core properties:

| Property                | Description                                                  |
|-------------------------|--------------------------------------------------------------|
| `description`           | Text component displayed on items                            |
| `supported_items`       | Items or item tags that can receive the enchantment          |
| `primary_items`         | Items or item tags offered by the enchanting table           |
| `exclusive_set`         | Incompatible enchantments                                    |
| `weight`                | Probability weight (1-1024)                                  |
| `max_level`             | Maximum level (1-255)                                        |
| `min_cost` / `max_cost` | Enchanting power window, as `base` + `per_level_above_first` |
| `anvil_cost`            | Base cost for anvil application                              |
| `slots`                 | Equipment slots where effects apply                          |
| `effects`               | Effect components that define behavior                       |

## File Structure

Enchantments are stored as JSON files in data packs at:

```
data/<namespace>/enchantment/<name>.json
```

For complete JSON specification, see the [Minecraft Wiki - Enchantment definition](https://minecraft.wiki/w/Enchantment_definition).

## Creating Enchantments

Use the `enchantment` builder function to create enchantments in Kore:

```kotlin
dataPack("my_datapack") {
	enchantment("fire_aspect_plus") {
		description("Fire Aspect+")
		supportedItems(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD)
		primaryItems(Tags.Item.SWORDS)
		exclusiveSet(Enchantments.FIRE_ASPECT)
		weight = 2
		maxLevel = 3
		minCost(15, 10)  // base 15, +10 per level
		maxCost(65, 10)
		anvilCost = 4
		slots(EquipmentSlot.MAINHAND)

		effects {
			// Define effects here
		}
	}
}
```

This generates `data/my_datapack/enchantment/fire_aspect_plus.json`.

## Basic Properties

### Description

The text shown on enchanted items:

```kotlin
enchantment("test") {
	// Simple string
	description("Test Enchantment")

	// Or with a text component for formatting
	description = textComponent("Test") { color = Color.GOLD }
}
```

### Supported and Primary Items

Both accept items and item tags, mixed freely:

```kotlin
enchantment("bow_enchant") {
	// Items the enchantment can be applied to on an anvil or with /enchant
	supportedItems(Items.BOW, Items.CROSSBOW)

	// Subset offered by the enchanting table
	primaryItems(Tags.Item.ENCHANTABLE_BOW)
}
```

### Exclusive Set

Enchantments that cannot coexist:

```kotlin
enchantment("protection_variant") {
	exclusiveSet(Tags.Enchantment.EXCLUSIVE_SET_ARMOR)
	// Or individual enchantments
	exclusiveSet(Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION)
}
```

### Cost and Weight

```kotlin
enchantment("rare_enchant") {
	weight = 1  // Very rare (compare to Mending: 2, Unbreaking: 5)
	maxLevel = 5

	// Enchanting power formula: base + (level - 1) * perLevelAboveFirst
	minCost(base = 1, perLevelAboveFirst = 11)   // 1, 12, 23, 34, 45
	maxCost(base = 21, perLevelAboveFirst = 11)  // 21, 32, 43, 54, 65

	anvilCost = 8  // Expensive to combine
}
```

### Equipment Slots

Where the enchantment's effects apply:

```kotlin
enchantment("armor_enchant") {
	slots(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
}

enchantment("weapon_enchant") {
	slots(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
}
```

Available slots: `ANY`, `HAND`, `MAINHAND`, `OFFHAND`, `ARMOR`, `FEET`, `LEGS`, `CHEST`, `HEAD`, `BODY`, `SADDLE`.

## Effect Components

Effects define what the enchantment actually does. Kore supports all vanilla effect components.

### Value Effect Components

These components change a number the game computes, and share the `add`, `multiply`, `set`, `removeBinomial` and
`allOf` builders:

| Component                   | Description                        |
|-----------------------------|------------------------------------|
| `ammoUse`                   | Ammunition consumption             |
| `armorEffectiveness`        | Armor effectiveness multiplier     |
| `blockExperience`           | XP from breaking blocks            |
| `crossbowChargeTime`        | Crossbow charge time               |
| `damage`                    | Bonus attack damage                |
| `damageProtection`          | Damage reduction (max 80% total)   |
| `equipmentDrops`            | Equipment drop chance              |
| `fishingLuckBonus`          | Fishing luck bonus                 |
| `fishingTimeReduction`      | Fishing speed bonus                |
| `itemDamage`                | Durability loss multiplier         |
| `knockback`                 | Knockback strength                 |
| `mobExperience`             | XP from killing mobs               |
| `projectileCount`           | Projectiles fired                  |
| `projectilePiercing`        | Targets pierced                    |
| `projectileSpread`          | Accuracy spread in degrees         |
| `repairWithXp`              | Durability repaired per XP         |
| `smashDamagePerFallenBlock` | Mace bonus damage per block fallen |
| `tridentReturnAcceleration` | Trident return speed               |
| `tridentSpinAttackStrength` | Riptide attack strength            |

```kotlin
effects {
	// Simple damage bonus
	damage {
		add(linearLevelBased(2f, 0.5f))  // +2 base, +0.5 per level
	}

	// Protection with conditions
	damageProtection {
		add(4) {
			requirements {
				weatherCheck(raining = true)
			}
		}
	}

	// Several operations, applied in order
	armorEffectiveness {
		allOf {
			add(2)
			multiply(1.2f)
			removeBinomial(0.5f)  // 50% chance to remove one unit
		}
	}
}
```

`equipmentDrops` names which side of the fight has to carry the enchantment, so its effects go inside an `on` block:

```kotlin
effects {
	equipmentDrops {
		on(EquipmentDropsSpecifier.ATTACKER) {
			add(0.05f)
		}
	}
}
```

`crossbowChargeTime` and `tridentSpinAttackStrength` hold a single effect instead of a list, so they take no
`requirements`. Use an `allOf` block to apply several operations:

```kotlin
effects {
	crossbowChargeTime {
		add(linearLevelBased(-0.25f, -0.25f))
	}
}
```

### Entity Effect Components

These components run actions on an entity, and share the entity effect builders listed below:

| Component            | Description                                     |
|----------------------|-------------------------------------------------|
| `hitBlock`           | After hitting a block with the enchanted item   |
| `locationChanged`    | When the holder moves, equips the item or loads |
| `postAttack`         | After damaging an entity                        |
| `postPiercingAttack` | After a piercing projectile goes through it     |
| `projectileSpawned`  | When a projectile is created                    |
| `tick`               | Every game tick while equipped                  |

```kotlin
effects {
	// Apply effects when hitting blocks
	hitBlock {
		applyMobEffect(Effects.SPEED) {
			minDuration(5)
			maxDuration(10)
			maxAmplifier(2)
		}
	}

	// Periodic effects
	tick {
		damageEntity(DamageTypes.MAGIC, 1, 2)
	}
}
```

`postAttack` names both the side carrying the enchantment and the side taking the effect, so its effects go inside an
`on` block:

```kotlin
effects {
	// Thorns-like retaliation
	postAttack {
		on(PostAttackSpecifier.VICTIM, PostAttackSpecifier.ATTACKER) {
			damageEntity(DamageTypes.THORNS, 1, 3)
		}
	}
}
```

### Special Effect Components

| Component                | Description                          |
|--------------------------|--------------------------------------|
| `attributes`             | Applies attribute modifiers          |
| `crossbowChargingSounds` | Custom crossbow sounds               |
| `damageImmunity`         | Grants immunity to matching hits     |
| `preventArmorChange`     | Prevents removing from armor slot    |
| `preventEquipmentDrop`   | Prevents item from dropping on death |
| `tridentSound`           | Custom trident sounds                |

```kotlin
effects {
	// Immunity to matching hits, one entry per condition set
	damageImmunity {
		requirements {
			weatherCheck(raining = true)
		}
	}

	// Curse-like effects
	preventEquipmentDrop()
	preventArmorChange()

	// Attribute modifiers
	attributes {
		attribute(
			"bonus_speed",
			name,
			Attributes.MOVEMENT_SPEED,
			AttributeModifierOperation.ADD_MULTIPLIED_BASE,
			constantLevelBased(0.1f)  // +10% speed
		)
	}

	// One sound entry per enchantment level
	crossbowChargingSounds {
		level {
			start(SoundEvents.Item.Crossbow.QUICK_CHARGE_1)
			mid(SoundEvents.Item.Crossbow.QUICK_CHARGE_2)
			end(SoundEvents.Item.Crossbow.QUICK_CHARGE_3)
		}
	}
}
```

## Entity Effects

The builders below are available in every entity effect component, in `allOf` blocks and inside a `postAttack`
`on` block.

### All Of

```kotlin
allOf {
	requirements { weatherCheck(raining = true) }  // only on the outermost allOf
	ignite(5)
	damageItem(1)
}
```

### Apply Exhaustion

```kotlin
applyExhaustion(5)
```

### Apply Impulse

```kotlin
applyImpulse(
	coordinateScale = Vec3f(1f, 1f, 1f),  // factor applied to the current motion, per axis
	direction = Vec3f(y = 1f),            // direction the entity is pushed towards
	magnitude = constantLevelBased(2)     // strength of the push, in blocks per tick
)
```

### Apply Mob Effect

```kotlin
applyMobEffect(Effects.SLOWNESS) {
	toApply(Effects.SLOWNESS, Effects.WEAKNESS)  // one is picked at random
	minDuration(5)
	maxDuration = linearLevelBased(5, 5)
	amplifier(1)  // sets both bounds at once
}
```

### Damage Entity

```kotlin
damageEntity(DamageTypes.MAGIC, minDamage = 1, maxDamage = 5)
```

### Damage Item

```kotlin
damageItem(1)  // durability removed from the enchanted item
```

### Explode

`smallParticle`, `largeParticle` and `sound` are required; everything else has a vanilla default:

```kotlin
explode(
	smallParticle = Particles.GUST_EMITTER_SMALL,
	largeParticle = Particles.GUST_EMITTER_LARGE,
	sound = SoundEvents.Entity.WindCharge.WIND_BURST,
) {
	blockInteraction = BlockInteraction.TRIGGER
	radius(3.5f)
	knockbackMultiplier(linearLevelBased(1.5f, 0.35f))
	immuneBlocks(Tags.Block.BLOCKS_WIND_CHARGE_EXPLOSIONS)

	blockParticles {
		particle(2, Particles.ASH, scaling = 0.5f)
	}
}
```

### Ignite

```kotlin
ignite(linearLevelBased(4, 4))  // seconds
```

### Play Sound

```kotlin
playSound(SoundEvents.Entity.FireworkRocket.LAUNCH, range = 16f) {
	volume(1f)
	pitch(1.2f)
}
```

### Replace Block / Disk

```kotlin
replaceBlock {
	blockState = simpleStateProvider(Blocks.FIRE)
	offset(0, 1, 0)
	triggerGameEvent = GameEvents.BLOCK_PLACE
}

replaceDisk {
	blockState = simpleStateProvider(Blocks.FROSTED_ICE)
	radius = clampedLevelBased(linearLevelBased(3, 1), 0f, 16f)
	height(1)
	offset(0, -1, 0)
	predicate { matchingBlocks(Blocks.WATER) }
}
```

### Set Block Properties

```kotlin
setBlockProperties {
	properties("lit" to "true")
	offset(0, -1, 0)
}
```

### Spawn Particles

```kotlin
spawnParticles(
	Particles.FLAME,
	horizontalPositionType = ParticlePositionType.IN_BOUNDING_BOX,
	verticalPositionType = ParticlePositionType.IN_BOUNDING_BOX
) {
	horizontalVelocity(base = 0.1f, movementScale = 0f)
	verticalVelocity(base = 0.5f, movementScale = 0f)
}
```

Particles carrying options take a builder instead of a plain `ParticleTypeArgument`: `blockParticleType`, `dustParticleType`,
`dustColorTransitionParticleType`, `itemParticleType`, `geyserParticleType`, `geyserBaseParticleType`, `geyserPlumeParticleType` and
`geyserPoofParticleType`.

```kotlin
spawnParticles(
	geyserBaseParticleType(Particles.GEYSER_BASE, burstImpulseBase = 0.5f, waterBlocks = 3),
	horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
	verticalPositionType = ParticlePositionType.ENTITY_POSITION
)
```

### Run Function

```kotlin
runFunction(FunctionArgument("on_hit", "my_datapack"))
```

### Summon Entity

```kotlin
summonEntity(EntityTypes.LIGHTNING_BOLT, joinTeam = true)
```

## Level-Based Values

Level-based values allow effects to scale with enchantment level. Every one of them is a float, so fractional results
are expressible, and every builder is scoped to the block that accepts a value.

| Type                                 | Description       | Example                                       |
|--------------------------------------|-------------------|-----------------------------------------------|
| `clampedLevelBased(value, min, max)` | Clamped range     | `clampedLevelBased(linear, 1f, 10f)`          |
| `constantLevelBased(value)`          | Fixed value       | `constantLevelBased(5)`                       |
| `exponentLevelBased(base, power)`    | Exponential       | `exponentLevelBased(1, 5)`                    |
| `fractionLevelBased(num, denom)`     | Fractional        | `fractionLevelBased(1, 2)`                    |
| `levelsSquaredLevelBased(added)`     | Quadratic scaling | `levelsSquaredLevelBased(1)` → 1, 4, 9...     |
| `linearLevelBased(base, perLevel)`   | Linear scaling    | `linearLevelBased(2f, 0.5f)` → 2, 2.5, 3...   |
| `lookupLevelBased(values, fallback)` | Lookup table      | `lookupLevelBased(1, 3, 7, fallback = 10)`    |

Outside a DSL block, `LevelBased` is a scope of its own, so `LevelBased.linearLevelBased(1, 1)` builds a value anywhere.

```kotlin
effects {
	damage {
		// Linear: 2 + 0.5 per level → 2, 2.5, 3, 3.5, 4 for levels 1-5
		add(linearLevelBased(2f, 0.5f))
	}

	blockExperience {
		allOf {
			add(clampedLevelBased(linearLevelBased(1, 2), 0f, 10f))
			multiply(levelsSquaredLevelBased(0.1f))
		}
	}
}
```

## Requirements (Conditions)

Every effect accepts a `requirements` block holding [predicate](/docs/data-driven/predicates) conditions, lifted next
to the effect in the generated JSON:

```kotlin
effects {
	damage {
		add(5) {
			requirements {
				weatherCheck(raining = true)
			}
		}
	}

	postAttack {
		on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.VICTIM) {
			applyMobEffect(Effects.POISON) {
				requirements {
					entityProperties {
						type(Tags.EntityType.UNDEAD)
					}
				}
			}
		}
	}
}
```

## Full Example

```kotlin
dataPack("custom_enchants") {
	enchantment("vampiric") {
		description = textComponent("Vampiric") { color = Color.DARK_RED }
		supportedItems(Tags.Item.SWORDS)
		primaryItems(Tags.Item.ENCHANTABLE_SWORD)
		exclusiveSet(Enchantments.MENDING)
		weight = 2
		maxLevel = 3
		minCost(20, 15)
		maxCost(50, 15)
		anvilCost = 8
		slots(EquipmentSlot.MAINHAND)

		effects {
			// Lifesteal on hit
			postAttack {
				on(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.ATTACKER) {
					applyMobEffect(Effects.INSTANT_HEALTH) {
						duration(1)

						requirements {
							randomChance(0.3f)
						}
					}
				}
			}

			// Bonus damage to undead
			damage {
				add(linearLevelBased(2, 1)) {
					requirements {
						entityProperties {
							type(Tags.EntityType.UNDEAD)
						}
					}
				}
			}
		}
	}
}
```

## Enchantment Providers

Enchantment providers pick the enchantments an item receives outside the enchanting table, such as the gear mobs spawn
with or the crossbow of a raid pillager. They generate `data/<namespace>/enchantment_provider/<name>.json`, and a name
holding slashes lands in subfolders.

```kotlin
dataPack("custom_enchants") {
	enchantmentProviders {
		// One enchantment, at a level rolled from an int provider
		single("pillager_spawn_crossbow", Enchantments.PIERCING, uniform(1, 3))

		// An enchanting power budget spent on a set of enchantments
		byCost("mob_spawn_equipment", Tags.Enchantment.ON_MOB_SPAWN_EQUIPMENT, cost = uniform(5, 25))

		// The same, scaled with the regional difficulty
		byCostWithDifficulty("raid/pillager_post_wave_3", Enchantments.PIERCING, minCost = 10, maxCostSpan = 15)

		// Villager trade providers follow the <biome>_<profession>_<level> naming
		single(villagerTradeName(Biomes.PLAINS, VillagerProfessions.ARMORER, 2), Enchantments.PROTECTION)
	}
}
```

## Best Practices

1. **Balance carefully** - Test enchantment power at all levels; use appropriate weights
2. **Use exclusive sets** - Prevent overpowered combinations with incompatible enchantments
3. **Scale appropriately** - Use level-based values that provide meaningful progression
4. **Add requirements** - Use conditions to create situational bonuses
5. **Consider slots** - Ensure effects only apply in appropriate equipment slots
6. **Test thoroughly** - Verify effects work correctly in all contexts (PvP, PvE, etc.)

## See Also

- [Predicates](/docs/data-driven/predicates) - Conditions for enchantment effect requirements
- [Components](/docs/concepts/components) - Item components and matchers
- [Tags](/docs/data-driven/tags) - Use enchantment and item tags
- [Villager Trades](/docs/data-driven/villager-trades) - `doubleTradePriceEnchantments` and enchanting villager trade
  outputs

### External Resources

- [Minecraft Wiki: Enchantment definition](https://minecraft.wiki/w/Enchantment_definition) - Official JSON format reference
- [Minecraft Wiki: Enchantment provider](https://minecraft.wiki/w/Enchantment_provider) - Provider JSON format reference
- [Minecraft Wiki: Enchanting](https://minecraft.wiki/w/Enchanting) - Enchanting mechanics overview
