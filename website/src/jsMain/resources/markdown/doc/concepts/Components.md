---
root: .components.layouts.MarkdownLayout
title: Components
nav-title: Components
description: A guide for using components in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, components
date-created: 2024-01-08
date-modified: 2026-02-03
routeOverride: /docs/concepts/components
---

In Minecraft, data components are structured key-value properties used to define and store behavior and attributes. They are attached to different things:

- Item components: properties that live on item stacks (e.g., `enchantments`, `food`,
  `attribute_modifiers`). They affect how items behave in inventories, commands, containers, etc.
- Entity variant components: properties exposed as components for certain entity variants when represented as items or spawn eggs (e.g.,
  `wolf/variant`, `cat/collar`). These follow the same component mechanics but target entity-specific customization.

This page focuses on using item components with Kore. For the vanilla reference and exhaustive definitions, see the Minecraft Wiki’s Data component format (
`https://minecraft.wiki/w/Data_component_format`).

The Kore library provides a comprehensive and user-friendly way to work with these components, enabling you to create custom items with ease. This article will guide you through the process of using components with Kore, showcasing examples and best practices.

## Creating Custom Items with Components

Let's dive into creating custom items with various components using Kore. Below are examples of how to define and manipulate item properties such as attribute modifiers, enchantments, and more.

### Attribute Modifiers

Attribute modifiers allow you to alter the attributes of an item, such as increasing damage or changing the scale. Here's how to define a stone sword with an attribute modifier using Kore:

```kotlin
import io.github.ayfri.kore.arguments.types.literals.randomUUID
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Attributes

val uuid = randomUUID()
val attributeModifiersTest = Items.STONE_SWORD {
	attributeModifiers {
		modifier(
			type = Attributes.SCALE,
			amount = 1.0,
			name = "Big!",
			operation = AttributeModifierOperation.ADD_VALUE,
			uuid = uuid,
		)
	}
}
```

### Enchantments

You can add enchantments to items to give them special abilities. Here’s an example of adding the Sharpness enchantment to a stone sword:

```kotlin
import io.github.ayfri.kore.generated.Enchantments

val enchantmentsTest = Items.STONE_SWORD {
	enchantments(mapOf(Enchantments.SHARPNESS to 5))
}
```

Check out the [Enchantments](./enchantments) article for more information on how to use enchantments with Kore.

### Custom Names and Lore

Custom names and lore can be added to items to give them unique identifiers and background stories:

```kotlin
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color

val customNameTest = Items.STONE_SWORD {
	customName(textComponent("Legendary Sword", Color.AQUA))
}
```

### Fireworks

You can define the properties of fireworks, including the shape and colors of the explosions:

```kotlin
import io.github.ayfri.kore.generated.FireworkExplosionShape
import io.github.ayfri.kore.arguments.colors.Color

val fireworksTest = Items.FIREWORK_ROCKET {
	fireworks(flightDuration = 1) {
		explosion(FireworkExplosionShape.BURST) {
			colors(Color.AQUA)
			fadeColors(Color.BLACK, Color.WHITE)
			hasTrail = true
			hasFlicker = true
		}
	}
}
```

See how to set custom colors in the [Colors](./colors) article.

### Custom Block Data

You can define custom properties for blocks using block entity data. Here's an example of adding custom data to a bee nest block:

```kotlin
import io.github.ayfri.kore.generated.Blocks
import io.github.ayfri.kore.generated.Items

val blockEntityDataTest = Items.BEE_NEST {
	blockEntityData(Blocks.BEE_NEST) {
		this["test"] = "test"
	}
}
```

### Profile

The profile component can be either a player profile or a texture-based profile.

#### Player Profile

A player profile uses a player's name or UUID:

```kotlin
Items.PLAYER_HEAD {
	playerProfile("Notch")
}
```

#### Texture Profile

A texture profile allows you to specify textures and models:

```kotlin
Items.PLAYER_HEAD {
	textureProfile(texture = "tex") {
		model = MannequinModel.SLIM
	}
}
```

### Recipes result with components

You can define recipes with components as well. Here's an example of crafting a custom enchanted golden apple using a shaped recipe:

```kotlin
recipes {
	craftingShaped("enchanted_golden_apple") {
		pattern(
			"GGG",
			"GAG",
			"GGG"
		)

		key("G", Items.GOLD_BLOCK)
		key("A", Items.APPLE)

		result(Items.ENCHANTED_GOLDEN_APPLE {
			food(
				nutrition = 10,
				saturation = 5.0f,
			) {
				effect(
					probability = 1f,
					id = Effects.REGENERATION,
					duration = 40,
					amplifier = 1,
					ambient = true,
					showParticles = true,
					showIcon = true
				)
			}
		})
	}
}
```

## Example Usage

To give yourself an item with custom components using the
`/give` command, you can define the item and its components as shown in the following example:

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.utils.set

// Define the item with a custom name
val customStone = Items.STONE {
	fireResistant()
	customName(textComponent("Special Stone", Color.AQUA))
	rarity(Rarities.EPIC)
	lore(
		textComponent("A stone with special properties.", Color.GRAY) +
			text("Use it wisely!", Color.GRAY)
	)
}

// Use the /give command to give the item to yourself
give(self(), customStone)
```

This example creates a custom stone item with a special name "Special Stone" in aqua color and gives it to the player using the
`/give`
command.

### Full list of Item components

Below is an alphabetical list of all item component helpers available in Kore. The names match the DSL functions you call inside an
`Items.* { }` builder.

| Helper                               | Description                                                                                              |
|--------------------------------------|----------------------------------------------------------------------------------------------------------|
| `attributeModifiers(..)`             | Modifies entity attributes (e.g., attack damage, speed, armor) when the item is equipped or held.        |
| `bannerPatterns(..)`                 | Defines the layered patterns displayed on a banner or shield.                                            |
| `baseColor(..)`                      | Sets the base color of a banner before patterns are applied.                                             |
| `bees { .. }`                        | Stores bee entities inside a beehive or bee nest item.                                                   |
| `blockEntityData(..)`                | Attaches custom NBT data to a block entity when the item is placed.                                      |
| `blocksAttacks(..)`                  | Configures how the item blocks incoming attacks when used (like a shield).                               |
| `blockState(..)`                     | Sets block state properties (e.g., facing, powered) when the item is placed.                             |
| `breakSound(..)`                     | Specifies the sound played when the item breaks from durability loss.                                    |
| `bucketEntityData(..)`               | Stores entity data for mobs captured in buckets (e.g., fish, axolotl).                                   |
| `bundleContents(..)`                 | Defines the items stored inside a bundle.                                                                |
| `canBreak(..)`                       | Restricts which blocks this item can break in Adventure mode.                                            |
| `canPlaceOn(..)`                     | Restricts which blocks this item can be placed on in Adventure mode.                                     |
| `chargedProjectiles(..)`             | Stores projectiles loaded into a crossbow.                                                               |
| `consumable(..) { .. }`              | Makes the item consumable with configurable eating time, animation, sound, and effects.                  |
| `container(..)`                      | Stores items inside a container item (e.g., shulker box).                                                |
| `containerLoot(..)`                  | References a loot table to generate container contents when opened.                                      |
| `customData(..)`                     | Attaches arbitrary custom NBT data for use by datapacks or mods.                                         |
| `customModelData(..)`                | Provides numeric values for custom item model selection in resource packs.                               |
| `customName(..)`                     | Sets a custom display name for the item (supports text components).                                      |
| `damage(..)`                         | Sets the current damage/durability consumed on a damageable item.                                        |
| `damageResistant(..)`                | Makes the item entity resistant to specific damage types (e.g., fire, explosions).                       |
| `damageType(..)`                     | Specifies the damage type dealt when attacking with this item.                                           |
| `deathProtection(..)`                | Prevents death and applies effects when the holder would die (like a totem).                             |
| `debugStickState(..)`                | Stores the selected block state property for the debug stick per block type.                             |
| `dyedColor(..)`                      | Sets the dye color for leather armor or other dyeable items.                                             |
| `enchantable(..)`                    | Defines the enchantability value affecting enchantment quality at enchanting tables.                     |
| `enchantmentGlintOverride(..)`       | Forces the enchantment glint on or off regardless of enchantments.                                       |
| `enchantments(..)`                   | Applies enchantments with their levels to the item.                                                      |
| `entityData(..)`                     | Stores entity NBT data for spawn eggs or items that spawn entities.                                      |
| `equippable(..)`                     | Configures equipment slot, sounds, and model when the item is worn.                                      |
| `fireworkExplosion(..)`              | Defines a single firework star explosion shape, colors, and effects.                                     |
| `fireworks(..)`                      | Configures firework rocket flight duration and explosion effects.                                        |
| `food(..)`                           | Makes the item edible with nutrition, saturation, and optional effects.                                  |
| `glider()`                           | Enables elytra-like gliding when equipped in the chest slot.                                             |
| `instrument(..)`                     | Specifies the goat horn sound variant when the item is used.                                             |
| `intangibleProjectile()`             | Makes projectiles from this item pass through entities without collision.                                |
| `itemModel(..)`                      | Overrides the item's model with a custom model resource location.                                        |
| `itemName(..)`                       | Sets the item's base name (different from custom name; not italicized).                                  |
| `jukeboxPlayable(..)`                | Allows the item to be played in a jukebox with a specified music disc track.                             |
| `kineticWeapon(..) { .. }`           | Configures kinetic weapon properties for mounted combat (reach, damage multiplier, conditions).          |
| `lock(..)`                           | Requires a matching item predicate (key) to open this container.                                         |
| `lodestoneTarget(..)`                | Makes a compass point to specific coordinates in a dimension.                                            |
| `lore(..)`                           | Adds tooltip lines below the item name for descriptions or flavor text.                                  |
| `mapColor(..)`                       | Sets the color tint for filled map item textures.                                                        |
| `mapDecorations(..)`                 | Adds custom icons/markers displayed on a filled map.                                                     |
| `mapId(..)`                          | Links the item to a specific map data ID for filled maps.                                                |
| `maxDamage(..)`                      | Sets the maximum durability before the item breaks.                                                      |
| `maxStackSize(..)`                   | Overrides how many items can stack in a single inventory slot (1-99).                                    |
| `minimumAttackCharge(..)`            | Sets the minimum attack charge (0.0-1.0) required for full damage.                                       |
| `noteBlockSound(..)`                 | Specifies the sound a note block plays when this player head is above it.                                |
| `ominousBottleAmplifier(..)`         | Sets the Bad Omen effect amplifier (0-4) when consuming an ominous bottle.                               |
| `piercingWeapon(..) { .. }`          | Configures piercing weapon properties (reach, knockback, dismount behavior).                             |
| `playerProfile(..)`                  | Sets the player skin displayed on a player head item.                                                    |
| `potDecorations(..)`                 | Defines the pottery sherds or bricks on each face of a decorated pot.                                    |
| `potionContents(..)`                 | Configures potion color, effects, and custom potion mixtures.                                            |
| `potionDurationScale(..)`            | Multiplies the duration of potion effects from this item.                                                |
| `providesBannerPatterns(..)`         | Registers this item as a banner pattern source for the loom.                                             |
| `providesTrimMaterial(..)`           | Registers this item as an armor trim material for the smithing table.                                    |
| `rarity(..)`                         | Sets the item name color tier (common, uncommon, rare, epic).                                            |
| `recipes(..)`                        | Unlocks specified recipes when this knowledge book is used.                                              |
| `repairable(..)`                     | Defines which items can repair this item on an anvil.                                                    |
| `repairCost(..)`                     | Sets the anvil repair cost penalty for combining or renaming.                                            |
| `storedEnchantments(..)`             | Stores enchantments in an enchanted book for anvil application.                                          |
| `suspiciousStewEffectsComponent(..)` | Defines the status effects applied when consuming suspicious stew.                                       |
| `swingAnimation(..)`                 | Configures the swing animation type (none, stab, whack) and duration.                                    |
| `tool { .. }`                        | Configures mining speeds, suitable blocks, and durability cost for tools.                                |
| `tooltipDisplay(..)`                 | Controls which tooltip sections are shown or hidden.                                                     |
| `tooltipStyle(..)`                   | Applies a custom tooltip background/border style from a resource pack.                                   |
| `trim(..)`                           | Applies an armor trim pattern and material to armor items.                                               |
| `unbreakable()`                      | Prevents the item from taking durability damage.                                                         |
| `useCooldown(..)`                    | Applies a cooldown period after using this item.                                                         |
| `useEffects(..)`                     | Configures use effects like allowing sprinting, interacting vibrations and speed multiplier while using. |
| `useRemainder(..)`                   | Specifies an item left behind after this item is fully consumed.                                         |
| `weapon(..)`                         | Configures melee weapon properties like damage and attack speed.                                         |
| `writableBookContent(..)`            | Stores editable pages in a book and quill.                                                               |
| `writtenBookContent(..)`             | Stores signed book content including title, author, and pages.                                           |

## Works great with Inventory Manager

If you build rich items with components and want to enforce them in player GUIs or chest slots, pair them with the [Inventory Manager](./helpers/inventory-manager). It lets you keep specific items in slots, react to takes, and clean up other slots while preserving all component data.

## Patch items

When you need to patch components on existing stacks (set name/lore, toggle tooltips, edit container contents, etc.) at runtime, use [Item Modifiers](./item-modifiers). Kore maps the vanilla functions like
`set_components`, `set_contents`, `set_fireworks`, and more.

## Custom Component

You can create custom components by extending the
`CustomComponent` class. Here's an example of a custom component that adds a custom attribute to an item:

```kotlin
package your.package

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.components.types.CustomComponent
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.SoundArgument
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UseComponent(
	var function: FunctionArgument,
	@SerialName("durability_damages") // properties aren't renamed to snake_case because of a limitation in KNBT library
	var durabilityDamages: Int? = null, // optional property, equals to 0 in Minecraft
	var cooldown: Float? = null, // optional property, equals to 0 in Minecraft
	var consume: Boolean? = null, // optional property, equals to false in Minecraft
	var sound: SoundArgument? = null, // optional property, equals to null in Minecraft
) : CustomComponent(
	nbt {
		this["function"] = function
		this["damage"] = damage
		this["cooldown"] = cooldown
		this["consume"] = consume
		this["sound"] = sound
	}
)

fun ComponentsScope.use(
	function: FunctionArgument,
	damage: Int? = null,
	cooldown: Float? = null,
	consume: Boolean? = null,
	sound: SoundArgument? = null,
) = apply {
	this["use_component"] = UseComponent(function, damage, cooldown, consume, sound)
}
```

And here's how you can use this custom component in an item definition:

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.Sounds
import your.package.use

val customItem = Items.DIAMOND_SWORD {
	val myFunction = function("use_weapon") {
		// Your function code here.
	}

	use(
		function = myFunction,
		durabilityDamages = 4,
		cooldown = 1.5f,
		sound = Sounds.Entity.Player.Attack.CRIT1
	)
}
// Result:
minecraft:diamond_sword[use_component ={ function:"datapack:use_weapon", damage:4, cooldown:1.5f, sound:"entity/player/attack/crit1" }]
```

## Entity Variant Components (25w04a+)

> *Introduced in snapshot [25w04a](https://www.minecraft.net/en-us/article/minecraft-snapshot-25w04a)*
>
> Entity variants such as axolotl colours, cat collars or tropical-fish patterns are now exposed as **data components
** and can be used on entities, spawn-egg items, mob buckets and paintings. This replaces the old `type_specific` NBT fields.

Kore ships dedicated helpers for each of these components. You can attach them to an
`Items.*` builder the exact same way you attach any other component:

```kotlin
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.arguments.enums.*

// Axolotl bucket with the blue variant
val blueAxolotlBucket = Items.AXOLOTL_BUCKET {
    axolotlVariant(AxolotlVariants.BLUE)
}

// Cat spawn-egg with a red collar
val redCollarCat = Items.CAT_SPAWN_EGG {
    catCollar(DyeColors.RED)
}

// Painting item selecting the "kebab" variant (namespace implied)
val kebabPainting = Items.PAINTING {
    paintingVariant(PaintingVariants.KEBAB)
}
```

These components can also be queried inside predicates:

```kotlin
predicate("only_blue_axolotls") {
    entityProperties {
        components {
            axolotlVariant(AxolotlVariants.BLUE)
        }
    }
}
```

The full list of variant helpers currently included in Kore is:

- `axolotlVariant(..)`
- `catCollar(..)` / `catVariant(..)`
- `foxVariant(..)`
- `frogVariant(..)`
- `horseVariant(..)`
- `llamaVariant(..)`
- `mooshroomVariant(..)`
- `paintingVariant(..)`
- `parrotVariant(..)`
- `pigVariant(..)`
- `rabbitVariant(..)`
- `salmonSize(..)`
- `sheepColor(..)`
- `shulkerColor(..)`
- `tropicalFishBaseColor(..)` / `tropicalFishPattern(..)` / `tropicalFishPatternColor(..)`
- `villagerVariant(..)`
- `wolfCollar(..)` / `wolfVariant(..)`

They follow the exact same naming and DSL pattern you already know:

```kotlin
fun ComponentsScope.wolfVariant(variant: WolfVariants) { /*…*/ }
```

Because the logic lives in regular data components, you automatically get:

• Compatibility with `itemStack` / `Items.*` DSL • Predicate support through `components {}`
• Correct serialisation back to the vanilla command-/NBT-syntax

Feel free to mix several variant components on the same `ComponentsScope`:

```kotlin
Items.WOLF_SPAWN_EGG {
    wolfCollar(DyeColors.BLACK)
    wolfVariant(WolfVariants.SNOWY)
}
```

## Component Matchers & Item Predicates

Kore provides powerful tools for matching and filtering items based on their components. This is useful in predicates, execute conditions,
and loot tables.

### Complete Example: Custom Tool Upgrade System

Here's a practical example showing how to use item predicates to create a tool upgrade system that detects enchanted, damaged tools and
replaces them with upgraded versions:

```kotlin
dataPack("tool_upgrades") {
  // Predicate to find diamond swords that need upgrading
  predicate("upgradeable_sword") {
    matchTool {
      items(Items.DIAMOND_SWORD)
      predicates {
        // Must have Sharpness enchantment
        enchantments {
          enchantment(Enchantments.SHARPNESS, level = rangeOrInt(1..4))
        }
        // Must be damaged (durability used)
        damage {
          damage = rangeOrInt(1..1000)
        }
      }
    }
  }

  // Function to check player's held item and upgrade it
  function("check_upgrade") {
    // Check if holding an upgradeable sword
    execute {
      ifCondition {
        items(
          self(),
          ItemSlot.WEAPON_MAINHAND,
          Items.DIAMOND_SWORD.predicate {
            subPredicates {
              enchantments {
                enchantment(Enchantments.SHARPNESS, level = rangeOrInt(3..4))
              }
            }
          }
        )
      }
      run {
        // Replace with netherite sword keeping enchantments
        items.modify(self(), ItemSlot.WEAPON_MAINHAND, itemModifier("upgrade_to_netherite"))
        tellraw(self(), textComponent("Your sword has been upgraded!", Color.GOLD))
      }
    }
  }

  // Clear specific items from inventory using predicates
  function("clear_broken_tools") {
    // Clear any tool with 1 durability left
    clear(allPlayers(), itemPredicate {
      subPredicates {
        damage {
          durability = rangeOrInt(1)
        }
      }
    })
  }

  // Give reward only if player has specific item combination
  function("check_collection") {
    execute {
      // Check for a goat horn (any variant)
      ifCondition {
        items(
          self(),
          ItemSlot.INVENTORY,
          itemPredicate {
            isPresent(ItemComponentTypes.INSTRUMENT)
          }
        )
      }
      // Check for enchanted book with Mending
      ifCondition {
        items(
          self(),
          ItemSlot.INVENTORY,
          Items.ENCHANTED_BOOK.predicate {
            subPredicates {
              storedEnchantments {
                enchantment(Enchantments.MENDING)
              }
            }
          }
        )
      }
      run {
        give(self(), Items.NETHER_STAR)
      }
    }
  }
}
```

### Item Predicates

Item predicates let you filter items by their components using the command syntax `<item>[predicate]`:

```kotlin
import io.github.ayfri.kore.arguments.components.*
import io.github.ayfri.kore.arguments.components.item.*
import io.github.ayfri.kore.generated.Items
import io.github.ayfri.kore.generated.ItemComponentTypes

// Match items with specific component values
val damagedSword = Items.DIAMOND_SWORD.predicate {
  damage(10)
}
// Result: minecraft:diamond_sword[damage=10]

// Match any item with a component present (existence check)
val hasInstrument = itemPredicate {
  isPresent(ItemComponentTypes.INSTRUMENT)
}
// Result: *[instrument]

// Partial matching with ~ syntax
val customDataMatch = Items.STONE.predicate {
  customData {
    this["myKey"] = "myValue"
  }
  partial(ItemComponentTypes.CUSTOM_DATA)
}
// Result: minecraft:stone[custom_data~{myKey:"myValue"}]

// Negated predicates (component must NOT have this value)
val notDamaged = Items.DIAMOND_SWORD.predicate {
  !damage(0)
}
// Result: minecraft:diamond_sword[!damage=0]

// Multiple alternatives with OR
val multipleValues = Items.STONE.predicate {
  damage(1) or damage(2) or damage(3)
}
// Result: minecraft:stone[damage=1|damage=2|damage=3]

// Count predicate
val stackOf10 = Items.DIAMOND.predicate {
  count(10)
}
// Result: minecraft:diamond[count=10]
```

### Component Matchers (Sub-Predicates)

For more complex matching logic, use `subPredicates` with component matchers. These serialize to JSON format for use in predicate files:

```kotlin
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.arguments.components.matchers.*
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt

val subPredicate = ItemStackSubPredicates().apply {
  // Match damage component with range
  damage {
    durability = rangeOrInt(1..100)
    damage = rangeOrInt(0..10)
  }

  // Match enchantments
  enchantments {
    enchantment(Enchantments.SHARPNESS, level = 3)
  }

  // Match potion contents
  potionContents(Effects.SPEED, Effects.STRENGTH)
}
```

### Existence Checks

You can check if a component exists on an item without matching a specific value:

```kotlin
// In item predicates (command form)
val hasInstrument = itemPredicate {
  isPresent(ItemComponentTypes.INSTRUMENT)
}
// Result: *[instrument]

// In sub-predicates (JSON form for predicate files)
val existsCheck = ItemStackSubPredicates().apply {
  exists(ItemComponentTypes.INSTRUMENT)
}
// Result: {"minecraft:instrument": {}}

// Multiple existence checks
val multipleExists = ItemStackSubPredicates().apply {
  exists(ItemComponentTypes.INSTRUMENT)
  exists(ItemComponentTypes.DAMAGE)
}
// Result: {"minecraft:instrument": {}, "minecraft:damage": {}}
```

### Available Component Matchers

| Matcher                   | Description                                    |
|---------------------------|------------------------------------------------|
| `attributeModifiers { }`  | Match attribute modifier properties            |
| `bundlerContents { }`     | Match bundle contents                          |
| `container { }`           | Match container slot contents                  |
| `customData { }`          | Match custom NBT data                          |
| `damage { }`              | Match damage/durability values                 |
| `enchantments { }`        | Match enchantment types and levels             |
| `exists(component)`       | Check if component exists (empty `{}` matcher) |
| `fireworkExplosion { }`   | Match firework star properties                 |
| `fireworks { }`           | Match firework rocket properties               |
| `jukeboxPlayable { }`     | Match jukebox song                             |
| `potionContents(..)`      | Match potion effects                           |
| `storedEnchantments { }`  | Match stored enchantments (enchanted books)    |
| `trim { }`                | Match armor trim pattern/material              |
| `writableBookContent { }` | Match book pages                               |
| `writtenBookContent { }`  | Match signed book content                      |

## Conclusion

Components are a powerful tool for customizing Minecraft objects, and the Kore library makes it easier than ever to work with these components programmatically. Whether you're adding custom attributes, enchantments, or creating complex items with multiple components, Kore provides a robust and intuitive API for enhancing your Minecraft experience.

By following the examples and practices outlined in this article, you can leverage the full potential of components in your Minecraft projects, creating richer and more engaging content for players.

Happy crafting!

## See Also

- [Predicates](/docs/data-driven/predicates) - Use components in predicate conditions
- [Item Modifiers](/docs/data-driven/item-modifiers) - Patch components at runtime
- [Recipes](/docs/data-driven/recipes) - Use components in recipe results
- [Inventory Manager](/docs/helpers/inventory-manager) - Enforce component-rich items in slots

### External Resources

- [Minecraft Wiki: Data component format](https://minecraft.wiki/w/Data_component_format) - Official component reference
