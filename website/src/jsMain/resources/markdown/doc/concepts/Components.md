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

| Helper                               | Description                                                                                       |
|--------------------------------------|---------------------------------------------------------------------------------------------------|
| `attributeModifiers(..)`             | Modifies entity attributes (e.g., attack damage, speed, armor) when the item is equipped or held. |
| `bannerPatterns(..)`                 | Defines the layered patterns displayed on a banner or shield.                                     |
| `baseColor(..)`                      | Sets the base color of a banner before patterns are applied.                                      |
| `bees { .. }`                        | Stores bee entities inside a beehive or bee nest item.                                            |
| `blockEntityData(..)`                | Attaches custom NBT data to a block entity when the item is placed.                               |
| `blocksAttacks(..)`                  | Configures how the item blocks incoming attacks when used (like a shield).                        |
| `blockState(..)`                     | Sets block state properties (e.g., facing, powered) when the item is placed.                      |
| `breakSound(..)`                     | Specifies the sound played when the item breaks from durability loss.                             |
| `bucketEntityData(..)`               | Stores entity data for mobs captured in buckets (e.g., fish, axolotl).                            |
| `bundleContents(..)`                 | Defines the items stored inside a bundle.                                                         |
| `canBreak(..)`                       | Restricts which blocks this item can break in Adventure mode.                                     |
| `canPlaceOn(..)`                     | Restricts which blocks this item can be placed on in Adventure mode.                              |
| `chargedProjectiles(..)`             | Stores projectiles loaded into a crossbow.                                                        |
| `consumable(..) { .. }`              | Makes the item consumable with configurable eating time, animation, sound, and effects.           |
| `container(..)`                      | Stores items inside a container item (e.g., shulker box).                                         |
| `containerLoot(..)`                  | References a loot table to generate container contents when opened.                               |
| `customData(..)`                     | Attaches arbitrary custom NBT data for use by datapacks or mods.                                  |
| `customModelData(..)`                | Provides numeric values for custom item model selection in resource packs.                        |
| `customName(..)`                     | Sets a custom display name for the item (supports text components).                               |
| `damage(..)`                         | Sets the current damage/durability consumed on a damageable item.                                 |
| `damageResistant(..)`                | Makes the item entity resistant to specific damage types (e.g., fire, explosions).                |
| `damageType(..)`                     | Specifies the damage type dealt when attacking with this item.                                    |
| `deathProtection(..)`                | Prevents death and applies effects when the holder would die (like a totem).                      |
| `debugStickState(..)`                | Stores the selected block state property for the debug stick per block type.                      |
| `dyedColor(..)`                      | Sets the dye color for leather armor or other dyeable items.                                      |
| `enchantable(..)`                    | Defines the enchantability value affecting enchantment quality at enchanting tables.              |
| `enchantmentGlintOverride(..)`       | Forces the enchantment glint on or off regardless of enchantments.                                |
| `enchantments(..)`                   | Applies enchantments with their levels to the item.                                               |
| `entityData(..)`                     | Stores entity NBT data for spawn eggs or items that spawn entities.                               |
| `equippable(..)`                     | Configures equipment slot, sounds, and model when the item is worn.                               |
| `fireworkExplosion(..)`              | Defines a single firework star explosion shape, colors, and effects.                              |
| `fireworks(..)`                      | Configures firework rocket flight duration and explosion effects.                                 |
| `food(..)`                           | Makes the item edible with nutrition, saturation, and optional effects.                           |
| `glider()`                           | Enables elytra-like gliding when equipped in the chest slot.                                      |
| `instrument(..)`                     | Specifies the goat horn sound variant when the item is used.                                      |
| `intangibleProjectile()`             | Makes projectiles from this item pass through entities without collision.                         |
| `itemModel(..)`                      | Overrides the item's model with a custom model resource location.                                 |
| `itemName(..)`                       | Sets the item's base name (different from custom name; not italicized).                           |
| `jukeboxPlayable(..)`                | Allows the item to be played in a jukebox with a specified music disc track.                      |
| `lock(..)`                           | Requires a matching item predicate (key) to open this container.                                  |
| `lodestoneTarget(..)`                | Makes a compass point to specific coordinates in a dimension.                                     |
| `lore(..)`                           | Adds tooltip lines below the item name for descriptions or flavor text.                           |
| `mapColor(..)`                       | Sets the color tint for filled map item textures.                                                 |
| `mapDecorations(..)`                 | Adds custom icons/markers displayed on a filled map.                                              |
| `mapId(..)`                          | Links the item to a specific map data ID for filled maps.                                         |
| `maxDamage(..)`                      | Sets the maximum durability before the item breaks.                                               |
| `maxStackSize(..)`                   | Overrides how many items can stack in a single inventory slot (1-99).                             |
| `noteBlockSound(..)`                 | Specifies the sound a note block plays when this player head is above it.                         |
| `ominousBottleAmplifier(..)`         | Sets the Bad Omen effect amplifier (0-4) when consuming an ominous bottle.                        |
| `playerProfile(..)`                  | Sets the player skin displayed on a player head item.                                             |
| `potDecorations(..)`                 | Defines the pottery sherds or bricks on each face of a decorated pot.                             |
| `potionContents(..)`                 | Configures potion color, effects, and custom potion mixtures.                                     |
| `potionDurationScale(..)`            | Multiplies the duration of potion effects from this item.                                         |
| `providesBannerPatterns(..)`         | Registers this item as a banner pattern source for the loom.                                      |
| `providesTrimMaterial(..)`           | Registers this item as an armor trim material for the smithing table.                             |
| `rarity(..)`                         | Sets the item name color tier (common, uncommon, rare, epic).                                     |
| `recipes(..)`                        | Unlocks specified recipes when this knowledge book is used.                                       |
| `repairable(..)`                     | Defines which items can repair this item on an anvil.                                             |
| `repairCost(..)`                     | Sets the anvil repair cost penalty for combining or renaming.                                     |
| `storedEnchantments(..)`             | Stores enchantments in an enchanted book for anvil application.                                   |
| `suspiciousStewEffectsComponent(..)` | Defines the status effects applied when consuming suspicious stew.                                |
| `tool { .. }`                        | Configures mining speeds, suitable blocks, and durability cost for tools.                         |
| `tooltipDisplay(..)`                 | Controls which tooltip sections are shown or hidden.                                              |
| `tooltipStyle(..)`                   | Applies a custom tooltip background/border style from a resource pack.                            |
| `trim(..)`                           | Applies an armor trim pattern and material to armor items.                                        |
| `unbreakable()`                      | Prevents the item from taking durability damage.                                                  |
| `useCooldown(..)`                    | Applies a cooldown period after using this item.                                                  |
| `useRemainder(..)`                   | Specifies an item left behind after this item is fully consumed.                                  |
| `weapon(..)`                         | Configures melee weapon properties like damage and attack speed.                                  |
| `writableBookContent(..)`            | Stores editable pages in a book and quill.                                                        |
| `writtenBookContent(..)`             | Stores signed book content including title, author, and pages.                                    |

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

## Conclusion

Components are a powerful tool for customizing Minecraft objects, and the Kore library makes it easier than ever to work with these components programmatically. Whether you're adding custom attributes, enchantments, or creating complex items with multiple components, Kore provides a robust and intuitive API for enhancing your Minecraft experience.

By following the examples and practices outlined in this article, you can leverage the full potential of components in your Minecraft projects, creating richer and more engaging content for players.

Happy crafting!

## See Also

- [Predicates](../data-driven/predicates) - Use components in predicate conditions
- [Item Modifiers](../data-driven/item-modifiers) - Patch components at runtime
- [Recipes](../data-driven/recipes) - Use components in recipe results
- [Inventory Manager](../helpers/inventory-manager) - Enforce component-rich items in slots

### External Resources

- [Minecraft Wiki: Data component format](https://minecraft.wiki/w/Data_component_format) - Official component reference
