---
root: .components.layouts.MarkdownLayout
title: Enchantments
nav-title: Enchantments
description: Learn how to create custom Minecraft enchantments using Kore – an exhaustive guide covering every available enchantment effect with detailed Kotlin examples.
keywords: minecraft, datapack, kore, enchantments, effects, customization
date-created: 2025-03-02
date-modified: 2026-02-03
routeOverride: /docs/data-driven/enchantments
---

# Enchantments

Minecraft enchantments modify game mechanics by altering item behavior, applying effects or changing damage calculations. Kore provides a fully type-safe, Kotlin-based DSL for creating custom enchantments that can be used in datapacks. This documentation explains how to create enchantments in Kore, and presents a detailed example for every available enchantment effect.

For general concepts on creating datapacks with Kore, please refer to the other documentation pages (e.g. [Predicates](./predicates), [Components](./components)). This article is dedicated exclusively to creating enchantments.

## Basic Enchantment Creation

Creating an enchantment in Kore is as simple as calling the
`enchantment` function inside a data pack. A basic enchantment is defined by its description, what items it supports, and additional properties such as exclusive sets, primary items, and equipment slots.

For example:

```kotlin
dataPack("my_datapack") {
    enchantment("test") {
        description("This is a test enchantment.")
        exclusiveSet(Tags.Enchantment.IN_ENCHANTING_TABLE)
        supportedItems(Items.DIAMOND_SWORD, Items.DIAMOND_AXE)
        primaryItems(Tags.Item.AXES)
        slots(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
    }
}
```

This snippet creates a simple enchantment named “test” that can be applied to a diamond sword or axe, with additional restrictions on the type of items and slots.

## Enchantment Effects

An enchantment’s power is defined in the
`effects` block. Kore supports a wide range of effects – each affecting a different aspect of gameplay. You can combine multiple effects by using functions within the
`effects { ... }` builder. Below is an exhaustive list of every supported enchantment effect along with a precise Kotlin example.

### Ammo Use

Sets the amount of ammunition used when firing a bow or crossbow.

```kotlin
enchantment("ammo_use") {
    effects {
        ammoUse {
            add(5)
        }
    }
}
```

### Attributes

Modifies or adds attribute modifiers to items. For example, increasing an item’s scale.

```kotlin
enchantment("attributes") {
    effects {
        attributes {
            attribute("my_modifier", name, Attributes.SCALE, AttributeModifierOperation.ADD_VALUE, 5)
        }
    }
}
```

### Armor Effectiveness

Modifies the effectiveness of armor when the enchantment is active.

```kotlin
enchantment("armor_effectiveness") {
    effects {
        armorEffectiveness {
            add(5) {
                requirements {
                    weatherCheck(raining = true)
                }
            }
            allOf {
                add(5)
                allOf {
                    add(5)
                }
            }
            multiply(2)
            removeBinomial(2)
            set(2)
        }
    }
}
```

### Block Experience

Calculates and awards experience based on block-related factors using a combination of level-based providers. This effect demonstrates the DSL’s advanced capabilities by combining several sub-effects:

```kotlin
enchantment("block_experience") {
    effects {
        blockExperience {
            allOf {
                add(clampedLevelBased(5, 0.0, 10.0))
                add(constantLevelBased(5))
                add(fractionLevelBased(1, 5))
                add(levelsSquaredLevelBased(2))
                add(linearLevelBased(2, 2))
                add(lookupLevelBased(2, 2, fallback = 2))
            }
        }
    }
}
```

### Crossbow Charging Sounds

Changes the sounds produced when a crossbow is charged.

```kotlin
enchantment("crossbow_charging_sounds") {
    effects {
        crossbowChargingSounds {
            crossbowChargingSound {
                start(SoundEvents.Item.Crossbow.QUICK_CHARGE_3)
            }
        }
    }
}
```

### Crossbow Charge Time

Alters the time required to charge a crossbow.

```kotlin
enchantment("crossbow_charge_time") {
    effects {
        crossbowChargeTime {
            add(5)
        }
    }
}
```

### Damage

Applies an additional damage effect when the enchanted item is used.

```kotlin
enchantment("damage") {
    effects {
        damage {
            add(5)
        }
    }
}
```

### Damage Immunity

Gives temporary immunity from damage; can also trigger a sound effect when activated.

```kotlin
enchantment("damage_immunity") {
    effects {
        damageImmunity {
            sound {
                requirements {
                    weatherCheck(raining = true)
                }
            }
        }
    }
}
```

### Equipment Drops

Modifies the probability and quantity of equipment drops when an entity is defeated.

```kotlin
enchantment("equipment_drops") {
    effects {
        equipmentDrops {
            add(EquipmentDropsSpecifier.ATTACKER, 5) {
                requirements {
                    weatherCheck(raining = true)
                }
            }
            allOf(EquipmentDropsSpecifier.VICTIM) {
                add(5)
                allOf {
                    add(5)
                }
            }
        }
    }
}
```

### Fishing Luck Bonus

Increases luck when fishing.

```kotlin
enchantment("fishing_luck_bonus") {
    effects {
        fishingLuckBonus {
            add(5)
        }
    }
}
```

### Fishing Time Reduction

Reduces the time required to catch fish.

```kotlin
enchantment("fishing_time_reduction") {
    effects {
        fishingTimeReduction {
            add(5)
        }
    }
}
```

### Hit Block

A multifaceted effect triggered when the enchanted item is used to hit a block. It supports many sub-effects including applying mob effects, damaging entities, modifying item durability, explosions, igniting blocks, playing sounds, changing block states, and summoning particles or entities.

```kotlin
enchantment("hit_block") {
    effects {
        hitBlock {
            allOf {
                applyMobEffect(Effects.SPEED) {
                    maxDuration(2)
                    minAmplifier(1)
                }
                requirements {
                    weatherCheck(raining = true)
                }
            }
            damageEntity(DamageTypes.IN_FIRE, 1, 2) {
                requirements {
                    weatherCheck(raining = false)
                }
            }
            changeItemDamage(1)
            explode(
                attributeToUser = true,
                createFire = true,
                blockInteraction = BlockInteraction.TNT,
                smallParticle = Particles.ANGRY_VILLAGER,
                largeParticle = Particles.ANGRY_VILLAGER,
                sound = Sounds.Random.FUSE
            ) {
                radius(2)
            }
            ignite(2)
            playSound(SoundEvents.Entity.FireworkRocket.LAUNCH, 5f)
            replaceBlock(simpleStateProvider(Blocks.DIAMOND_BLOCK)) {
                offset(5, 5, 5)
                triggerGameEvent = GameEvents.BLOCK_PLACE
            }
            replaceDisk(simpleStateProvider(Blocks.DIAMOND_BLOCK)) {
                radius(5)
                height(2)
            }
            runFunction(FunctionArgument("function", "namespace"))
            setBlockProperties {
                properties {
                    this["test"] = "test"
                }
            }
            spawnParticles(
                Particles.ANGRY_VILLAGER,
                horizontalPositionType = ParticlePositionType.IN_BOUNDING_BOX,
                verticalPositionType = ParticlePositionType.IN_BOUNDING_BOX,
            ) {
                horizontalVelocity(base = 2.5f, movementScale = 1.2f)
            }
            summonEntity(EntityTypes.AREA_EFFECT_CLOUD)
        }
    }
}
```

### Item Damage

Inflicts additional damage (i.e. worsening the item's condition).

```kotlin
enchantment("item_damage") {
    effects {
        itemDamage {
            add(5)
        }
    }
}
```

### Knockback

Applies extra knockback to the target when attacking.

```kotlin
enchantment("knockback") {
    effects {
        knockback {
            add(5)
        }
    }
}
```

### Mob Experience

Increases the experience dropped by mobs upon death.

```kotlin
enchantment("mob_experience") {
    effects {
        mobExperience {
            add(5)
        }
    }
}
```

### Post Attack

Executes effects immediately after an attack is completed. This effect can both apply a mob effect and cause damage.

```kotlin
enchantment("post_attack") {
    effects {
        postAttack {
            applyMobEffect(PostAttackSpecifier.ATTACKER, PostAttackSpecifier.DAMAGING_ENTITY, Effects.SPEED) {
                requirements {
                    weatherCheck(raining = true)
                }
            }
            damageEntity(PostAttackSpecifier.VICTIM, PostAttackSpecifier.DAMAGING_ENTITY, DamageTypes.IN_FIRE, 1, 2)
        }
    }
}
```

### Prevent Armor Change

Prevents armor slots from being modified.

```kotlin
enchantment("prevent_armor_change") {
    effects {
        preventArmorChange()
    }
}
```

### Prevent Equipment Drop

Stops equipment from being dropped on death.

```kotlin
enchantment("prevent_equipment_drop") {
    effects {
        preventEquipmentDrop()
    }
}
```

### Projectile Count

Modifies the number of projectiles produced (for example, by a crossbow).

```kotlin
enchantment("projectile_count") {
    effects {
        projectileCount {
            add(5)
        }
    }
}
```

### Projectile Piercing

Sets the number of targets a projectile can pierce.

```kotlin
enchantment("projectile_piercing") {
    effects {
        projectilePiercing {
            add(5)
        }
    }
}
```

### Projectile Spawned

Executes an effect when a projectile entity has been spawned from a bow, crossbow, snowball, trident, splash potion, lingering potion, ender pearl, firework rocket, wind charge, or egg.

```kotlin
enchantment("projectile_spawned") {
    effects {
        projectileSpawned {
            applyMobEffect(Effects.SPEED) {
                maxDuration(2)
                minAmplifier(1)
            }
        }
    }
}
```

### Projectile Spread

Alters the spread angle of projectiles fired.

```kotlin
enchantment("projectile_spread") {
    effects {
        projectileSpread {
            add(5)
        }
    }
}
```

### Repair with XP

Repairs an item using player experience when the item is used.

```kotlin
enchantment("repair_with_xp") {
    effects {
        repairWithXp {
            add(5)
        }
    }
}
```

### Smash Damage Per Fallen Block

Increases damage based on the number of blocks fallen before impact.

```kotlin
enchantment("smash_damage_per_fallen_block") {
    effects {
        smashDamagePerFallenBlock {
            add(5)
        }
    }
}
```

### Tick

Executes an effect periodically (every game tick). For example, dealing damage over time.

```kotlin
enchantment("tick") {
    effects {
        tick {
            damageEntity(DamageTypes.IN_FIRE, 1, 2)
        }
    }
}
```

### Trident Return Acceleration

Accelerates the return speed of a thrown trident.

```kotlin
enchantment("trident_return_acceleration") {
    effects {
        tridentReturnAcceleration {
            add(5)
        }
    }
}
```

### Trident Spin Attack Strength

Boosts the strength of a trident’s spin attack.

```kotlin
enchantment("trident_spin_attack_strength") {
    effects {
        tridentSpinAttackStrength {
            add(5)
        }
    }
}
```

### Trident Sound

Modifies the sound played when a trident is thrown or returns.

```kotlin
enchantment("trident_sound") {
    effects {
        tridentSound {
            sound(SoundEvents.Entity.FireworkRocket.LAUNCH, 5f)
        }
    }
}
```

Using Kore, you can create a nearly limitless variety of enchantments by mixing and matching these effects. The examples above illustrate each individual effect available in the library. Combine them as needed to design your custom gameplay mechanics with full type-safety and a clean, Kotlin-based DSL.

Happy enchanting!

## See Also

- [Predicates](./predicates) - Predicate conditions for enchantment effect requirements
- [Components](../concepts/components) - Item components and matchers
- [Loot Tables](./loot-tables) - Apply enchantments via loot functions
- [Item Modifiers](./item-modifiers) - Add enchantments at runtime

### External Resources

- [Minecraft Wiki: Enchantment definition](https://minecraft.wiki/w/Enchantment_definition) - Official JSON format reference
- [Minecraft Wiki: Enchanting](https://minecraft.wiki/w/Enchanting) - Enchanting mechanics overview
