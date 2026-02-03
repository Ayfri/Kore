---
root: .components.layouts.MarkdownLayout
title: Enchantments
nav-title: Enchantments
description: Create custom Minecraft enchantments using Kore's type-safe Kotlin DSL with support for all vanilla effect components and level-based values.
keywords: minecraft, datapack, kore, enchantments, effects, custom enchantments
date-created: 2025-03-02
date-modified: 2026-02-03
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

| Property                | Description                                         |
|-------------------------|-----------------------------------------------------|
| `description`           | Text component displayed on items                   |
| `supported_items`       | Items that can receive the enchantment              |
| `primary_items`         | Items where enchantment appears in enchanting table |
| `exclusive_set`         | Incompatible enchantments                           |
| `weight`                | Probability weight (1-1024)                         |
| `max_level`             | Maximum level (1-255)                               |
| `min_cost` / `max_cost` | Enchanting table level requirements                 |
| `anvil_cost`            | Base cost for anvil application                     |
| `slots`                 | Equipment slots where effects apply                 |
| `effects`               | Effect components that define behavior              |

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

	// Or with text component for formatting
	description(textComponent("Test") { color = Color.GOLD })
}
```

### Supported and Primary Items

```kotlin
enchantment("bow_enchant") {
	// Items that can have this enchantment (anvil/commands)
	supportedItems(Items.BOW, Items.CROSSBOW)

	// Items where it appears in enchanting table (subset of supported)
	primaryItems(Tags.Item.BOW_ENCHANTABLE)
}
```

### Exclusive Set

Enchantments that cannot coexist:

```kotlin
enchantment("protection_variant") {
	exclusiveSet(Tags.Enchantment.ARMOR_EXCLUSIVE)
	// Or individual enchantments
	exclusiveSet(Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION)
}
```

### Cost and Weight

```kotlin
enchantment("rare_enchant") {
	weight = 1  // Very rare (compare to Mending: 2, Unbreaking: 5)
	maxLevel = 5

	// Level cost formula: base + (level - 1) * per_level_above_first
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

These components modify numeric values with level-based scaling:

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
		add(linearLevelBased(2, 0.5))  // +2 base, +0.5 per level
	}

	// Protection with conditions
	damageProtection {
		add(constantLevelBased(4)) {
			requirements {
				damageType(DamageTypes.IN_FIRE)
			}
		}
	}

	// Multiple value modifications
	armorEffectiveness {
		add(5)
		multiply(1.5)
		set(10)
		removeBinomial(0.5)  // 50% chance to remove 1
		allOf {
			add(2)
			multiply(1.2)
        }
    }
}
```

### Entity Effect Components

These components trigger actions on entities:

| Component           | Description                                   |
|---------------------|-----------------------------------------------|
| `hitBlock`          | After hitting a block with the enchanted item |
| `tick`              | Every game tick while equipped                |
| `projectileSpawned` | When a projectile is created                  |
| `postAttack`        | After damaging an entity                      |

```kotlin
effects {
	// Apply effects when hitting blocks
	hitBlock {
		applyMobEffect(Effects.SPEED) {
			minDuration(5)
			maxDuration(10)
			minAmplifier(0)
			maxAmplifier(2)
		}
	}

	// Periodic effects
	tick {
		damageEntity(DamageTypes.MAGIC, 0.5, 1.0)
	}

	// Post-attack effects (like Thorns)
	postAttack {
		damageEntity(
			PostAttackSpecifier.ATTACKER,
			PostAttackSpecifier.VICTIM,
			DamageTypes.THORNS,
			1, 3
		)
    }
}
```

### Special Effect Components

| Component                | Description                          |
|--------------------------|--------------------------------------|
| `attributes`             | Applies attribute modifiers          |
| `crossbowChargingSounds` | Custom crossbow sounds               |
| `damageImmunity`         | Grants immunity to damage types      |
| `preventArmorChange`     | Prevents removing from armor slot    |
| `preventEquipmentDrop`   | Prevents item from dropping on death |
| `tridentSound`           | Custom trident sounds                |

```kotlin
effects {
	// Damage immunity (like totems)
	damageImmunity {
		sound {
			requirements {
				damageType(DamageTypes.FALLING_BLOCK)
			}
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
			0.1  // +10% speed
		)
	}

	// Custom sounds
	crossbowChargingSounds {
		crossbowChargingSound {
			start(SoundEvents.Item.Crossbow.QUICK_CHARGE_1)
			mid(SoundEvents.Item.Crossbow.QUICK_CHARGE_2)
			end(SoundEvents.Item.Crossbow.QUICK_CHARGE_3)
        }
    }
}
```

## Entity Effects

Entity effects are actions that can be triggered by effect components:

### Apply Mob Effect

```kotlin
applyMobEffect(Effects.SLOWNESS, Effects.WEAKNESS) {
	minDuration(5)
	maxDuration(linearLevelBased(5, 5))
	minAmplifier(0)
	maxAmplifier(constantLevelBased(1))
}
```

### Damage Entity

```kotlin
damageEntity(DamageTypes.MAGIC, minDamage = 1, maxDamage = 5)
```

### Explode

```kotlin
explode(
	attributeToUser = true,
	createFire = false,
	blockInteraction = BlockInteraction.TNT,
	smallParticle = Particles.EXPLOSION,
	largeParticle = Particles.EXPLOSION_EMITTER,
	sound = Sounds.Entity.Generic.EXPLODE
) {
	radius(linearLevelBased(2, 1))
}
```

### Ignite

```kotlin
ignite(duration = linearLevelBased(4, 4))  // seconds
```

### Play Sound

```kotlin
playSound(SoundEvents.Entity.Firework.LAUNCH, volume = 1f)
```

### Replace Block/Disk

```kotlin
replaceBlock(simpleStateProvider(Blocks.FIRE)) {
	offset(0, 1, 0)
	triggerGameEvent = GameEvents.BLOCK_PLACE
}

replaceDisk(simpleStateProvider(Blocks.ICE)) {
	radius(linearLevelBased(2, 1))
	height(1)
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
	speed(0.5f)
}
```

### Run Function

```kotlin
runFunction(FunctionArgument("on_hit", "my_datapack"))
```

### Summon Entity

```kotlin
summonEntity(EntityTypes.LIGHTNING_BOLT)
```

### Change Item Damage

```kotlin
changeItemDamage(linearLevelBased(1, 1))  // Durability consumed
```

## Level-Based Values

Level-based values allow effects to scale with enchantment level:

| Type                                 | Description       | Example                                     |
|--------------------------------------|-------------------|---------------------------------------------|
| `constantLevelBased(value)`          | Fixed value       | `constantLevelBased(5)`                     |
| `linearLevelBased(base, perLevel)`   | Linear scaling    | `linearLevelBased(2, 0.5)` → 2, 2.5, 3...   |
| `levelsSquaredLevelBased(base)`      | Quadratic scaling | `levelsSquaredLevelBased(1)` → 1, 4, 9...   |
| `clampedLevelBased(value, min, max)` | Clamped range     | `clampedLevelBased(linear, 1.0, 10.0)`      |
| `fractionLevelBased(num, denom)`     | Fractional        | `fractionLevelBased(1, 2)` → 0.5, 1, 1.5... |
| `lookupLevelBased(list, fallback)`   | Lookup table      | `lookupLevelBased(listOf(1, 3, 7), 10)`     |

```kotlin
effects {
	damage {
		// Linear: 2 + 0.5 per level → 2, 2.5, 3, 3.5, 4 for levels 1-5
		add(linearLevelBased(2, 0.5))
	}

	blockExperience {
		// Complex combination
		allOf {
			add(clampedLevelBased(linearLevelBased(1, 2), 0.0, 10.0))
			multiply(levelsSquaredLevelBased(0.1))
        }
    }
}
```

## Requirements (Conditions)

Effect components can have requirements that must be met:

```kotlin
effects {
	damage {
		add(5) {
			requirements {
				// Only in rain
				weatherCheck(raining = true)
			}
		}
	}

	damageProtection {
		add(4) {
			requirements {
				// Only against fire damage
				damageType(DamageTypes.IN_FIRE, DamageTypes.ON_FIRE)
			}
		}
	}

	postAttack {
		applyMobEffect(
			PostAttackSpecifier.ATTACKER,
			PostAttackSpecifier.VICTIM,
			Effects.POISON
		) {
			requirements {
				// Only against undead
				entityProperties {
					type(EntityTypes.ZOMBIE, EntityTypes.SKELETON)
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
		description(textComponent("Vampiric") { color = Color.DARK_RED })
		supportedItems(Tags.Item.SWORDS)
		primaryItems(Tags.Item.SWORD_ENCHANTABLE)
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
				applyMobEffect(
					PostAttackSpecifier.ATTACKER,
					PostAttackSpecifier.ATTACKER,
					Effects.INSTANT_HEALTH
				) {
					minAmplifier(0)
					maxAmplifier(0)
					minDuration(1)
					maxDuration(1)

					requirements {
						randomChance(linearLevelBased(0.1, 0.1))  // 10/20/30% chance
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

			// Visual feedback
			hitBlock {
				spawnParticles(
					Particles.CRIMSON_SPORE,
					horizontalPositionType = ParticlePositionType.ENTITY_POSITION,
					verticalPositionType = ParticlePositionType.ENTITY_POSITION
				) {
					speed(0.2f)
				}
			}
		}
    }
}
```

### Generated JSON

```json
{
	"description": {
		"text": "Vampiric",
		"color": "dark_red"
	},
	"supported_items": "#minecraft:swords",
	"primary_items": "#minecraft:sword_enchantable",
	"exclusive_set": "minecraft:mending",
	"weight": 2,
	"max_level": 3,
	"min_cost": {
		"base": 20,
		"per_level_above_first": 15
	},
	"max_cost": {
		"base": 50,
		"per_level_above_first": 15
	},
	"anvil_cost": 8,
	"slots": [
		"mainhand"
	],
	"effects": {
		"minecraft:post_attack": [
			{
				"enchanted": "attacker",
				"affected": "attacker",
				"effect": {
					"type": "minecraft:apply_mob_effect",
					"to_apply": "minecraft:instant_health",
					"min_amplifier": 0,
					"max_amplifier": 0,
					"min_duration": 1,
					"max_duration": 1
				},
				"requirements": {
					"condition": "minecraft:random_chance",
					"chance": {
						"type": "minecraft:linear",
						"base": 0.1,
						"per_level_above_first": 0.1
					}
				}
			}
		],
		"minecraft:damage": [
			{
				"effect": {
					"type": "minecraft:add",
					"value": {
						"type": "minecraft:linear",
						"base": 2,
						"per_level_above_first": 1
					}
				},
				"requirements": {
					"condition": "minecraft:entity_properties",
					"predicate": {
						"type": "#minecraft:undead"
					}
				}
			}
		]
	}
}
```

## Enchantment Providers

Enchantment providers are used by enchanting tables and loot functions to select enchantments:

```kotlin
enchantmentProvider("custom_table") {
	single {
		enchantment = Enchantments.SHARPNESS
    }
}

enchantmentProvider("cost_based") {
	byCost {
		// Configuration for cost-based selection
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

- [Predicates](./predicates) - Conditions for enchantment effect requirements
- [Components](../concepts/components) - Item components and matchers
- [Loot Tables](./loot-tables) - Apply enchantments via loot functions
- [Item Modifiers](./item-modifiers) - Add enchantments at runtime
- [Tags](./tags) - Use enchantment and item tags

### External Resources

- [Minecraft Wiki: Enchantment definition](https://minecraft.wiki/w/Enchantment_definition) - Official JSON format reference
- [Minecraft Wiki: Enchanting](https://minecraft.wiki/w/Enchanting) - Enchanting mechanics overview
