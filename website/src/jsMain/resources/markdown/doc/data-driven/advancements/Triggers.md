---
root: .components.layouts.MarkdownLayout
title: Advancements Triggers
nav-title: Advancements Triggers
description: A guide for using advancements triggers in Minecraft with Kore.
keywords: minecraft, datapack, kore, guide, advancements, triggers
date-created: 2024-08-01
date-modified: 2026-02-15
routeOverride: /docs/data-driven/advancements/triggers
---

## Available Triggers

Below is the comprehensive list of available trigger types. Each trigger includes a description, its properties with explanations, and an example usage in Kotlin.

---

### `allayDropItemOnBlock`

**Description:**  
Triggers when an allay drops an item on a block.

**Properties:**

- `location`: The location where the item is dropped.

**Example:**

```kotlin
allayDropItemOnBlock("allay_drop") {
	location {
		block {
			blocks(Blocks.GRASS_BLOCK, Blocks.DIRT)
		}
	}
}
```

---

### `anyBlockUse`

**Description:**  
Triggers when a player uses any block.

**Properties:**  
_None._

**Example:**

```kotlin
anyBlockUse("use_block") {
	conditions {
		playerProperties {
			lookingAt(Blocks.CRAFTING_TABLE)
		}
	}
}
```

---

### `avoidVibrations`

**Description:**  
Triggers when a player avoids vibrations.

**Properties:**

- `location`: The location where vibrations are avoided.

**Example:**

```kotlin
avoidVibrations("avoid_sculk") {
	conditions {
		location {
			block = Blocks.SCULK_SENSOR
		}
	}
}
```

---

### `beeNestDestroyed`

**Description:**  
Triggers when a bee nest is destroyed.

**Properties:**

- `block`: The type of block that was destroyed.
- `item`: The item involved in the destruction.
- `numBeesInside`: The number of bees that were inside the nest.

**Example:**

```kotlin
beeNestDestroyed("destroy_nest") {
	block = Blocks.BEE_NEST
	numBeesInside = rangeOrInt(1)
	item {
		item(Items.HONEYCOMB)
	}
}
```

---

### `bredAnimals`

**Description:**  
Triggers when animals are bred.

**Properties:**

- `child`: The child animal resulting from the breeding.
- `parent`: One of the parent animals.
- `partner`: The other parent animal.

**Example:**

```kotlin
bredAnimals("breed_animals") {
	child {
		conditions {
			entityProperties {
				type(EntityTypes.COW)
			}
		}
	}
	parent {
		conditions {
			entityProperties {
				type(EntityTypes.COW)
			}
		}
	}
	partner {
		conditions {
			entityProperties {
				type(EntityTypes.COW)
			}
		}
	}
}
```

---

### `brewedPotion`

**Description:**  
Triggers when a potion is brewed (player takes item from brewing stand).

**Properties:**

- `potion`: The potion that was brewed.

**Example:**

```kotlin
brewedPotion("brewed_potion")
```

---

### `changedDimension`

**Description:**  
Triggers when a player changes dimension.

**Properties:**

- `from`: The original dimension.
- `to`: The new dimension.

**Example:**

```kotlin
changedDimension("enter_nether") {
	from = Dimensions.OVERWORLD
	to = Dimensions.NETHER
}
```

---

### `channeledLightning`

**Description:**  
Triggers when lightning is channeled.

**Properties:**

- `victims`: A list of entities affected by the lightning.

**Example:**

```kotlin
channeledLightning("lightning_rod") {
	victim {
		conditions {
			entityProperties {
				type(EntityTypes.CREEPER)
			}
		}
	}
}
```

---

### `constructBeacon`

**Description:**  
Triggers when a beacon is constructed.

**Properties:**

- `level`: The level of the beacon.

**Example:**

```kotlin
constructBeacon("make_beacon") {
	level = rangeOrInt(4)
}
```

---

### `consumeItem`

**Description:**  
Triggers when an item is consumed.

**Properties:**

- `item`: The item that was consumed.

**Example:**

```kotlin
consumeItem("eat_apple") {
	item {
		items = listOf(Items.GOLDEN_APPLE)
	}
}
```

---

### `crafterRecipeCrafted`

**Description:**  
Triggers when a recipe is crafted.

**Properties:**

- `recipeId`: The ID of the crafted recipe.
- `ingredients`: The ingredients used in the recipe.

**Example:**

```kotlin
crafterRecipeCrafted("craft_diamond") {
	recipeId = Recipes.DIAMOND
	ingredient(Items.DIAMOND) {
		components {
			damage(0)
		}
	}
}
```

---

### `curedZombieVillager`

**Description:**  
Triggers when a zombie villager is cured.

**Properties:**

- `villager`: The villager involved in the curing.
- `zombie`: The zombie involved in the curing.

**Example:**

```kotlin
curedZombieVillager("cure_zombie") {
	villager {
		conditions {
			entityProperties {
				type(EntityTypes.VILLAGER)
			}
		}
	}
	zombie {
		conditions {
			entityProperties {
				type(EntityTypes.ZOMBIE)
			}
		}
	}
}
```

---

### `defaultBlockUse`

**Description:**  
Triggers when a block is used with default interaction.

**Properties:**  
_None._

**Example:**

```kotlin
defaultBlockUse("use_default") {
	conditions {
		playerProperties {
			lookingAt(Blocks.CHEST)
		}
	}
}
```

---

### `effectsChanged`

**Description:**  
Triggers when a player's effects change.

**Properties:**

- `effects`: The effects that have changed.
- `source`: The source of the effect changes.

**Example:**

```kotlin
effectsChanged("get_effect") {
	effect(Effects.SPEED) {
		amplifier = rangeOrInt(1..3)
		duration = rangeOrInt(100..200)
	}
	source {
		conditions {
			entityProperties {
				type(EntityTypes.WITCH)
			}
		}
	}
}
```

---

### `enchantedItem`

**Description:**  
Triggers when an item is enchanted.

**Properties:**

- `item`: The item that was enchanted.
- `levels`: The levels of enchantment applied.

**Example:**

```kotlin
enchantedItem("enchant_item") {
	item {
		item(Items.DIAMOND_SWORD)
	}
	levels = rangeOrInt(1..3)
}
```

---

### `enterBlock`

**Description:**  
Triggers when a player enters a specific block.

**Properties:**

- `block`: The block being entered.
- `states`: The state properties of the block.

**Example:**

```kotlin
enterBlock("enter_block") {
	block = Blocks.REDSTONE_LAMP
	states {
		this["lit"] = "true"
	}
}
```

---

### `entityHurtPlayer`

**Description:**  
Triggers when an entity hurts a player.

**Properties:**

- `damage`: Details about the damage inflicted.

**Example:**

```kotlin
entityHurtPlayer("hurt_player") {
	damage {
		sourceEntity {
			type(EntityTypes.ZOMBIE)
		}
		taken = rangeOrDouble(5.0..10.0)
		type {
			tag(Tags.DamageType.IS_FALL)
		}
	}
}
```

---

### `entityKilledPlayer`

**Description:**  
Triggers when an entity kills a player.

**Properties:**

- `entity`: The entity that killed the player.
- `killingBlow`: Details about the killing blow.

**Example:**
_Not provided in the original examples._

---

### `fallAfterExplosion`

**Description:**  
Triggers after falling from an explosion.

**Properties:**

- `startPosition`: The starting position of the fall.
- `distance`: The distance fallen.
- `cause`: The cause of the fall.

**Example:**

```kotlin
fallAfterExplosion("tnt_launch") {
	startPosition {
		position {
			y = rangeOrInt(100..200)
		}
	}
	distance {
		horizontal(10f)
	}
}
```

---

### `fallFromHeight`

**Description:**  
Triggers when falling from a height.

**Properties:**

- `startPosition`: The starting position of the fall.
- `distance`: The distance fallen.

**Example:**

```kotlin
fallFromHeight("high_fall") {
	distance {
		vertical(20f)
	}
}
```

---

### `filledBucket`

**Description:**  
Triggers when a bucket is filled.

**Properties:**

- `item`: The bucket item that was filled.

**Example:**

```kotlin
filledBucket("fill_bucket") {
	item {
		item(Items.WATER_BUCKET)
	}
}
```

---

### `fishingRodHooked`

**Description:**  
Triggers when a fishing rod hooks something.

**Properties:**

- `entity`: The entity hooked by the fishing rod.
- `item`: The item used as the fishing rod.
- `rod`: Details about the fishing rod.

**Example:**

```kotlin
fishingRodHooked("catch_fish") {
	item {
		item(Items.FISHING_ROD)
	}
	rod {
		components {
			enchantments {
				enchantment(Enchantments.LUCK_OF_THE_SEA, 3)
			}
		}
	}
}
```

---

### `heroOfTheVillage`

**Description:**  
Triggers when becoming a hero of the village.

**Properties:**  
_None._

**Example:**

```kotlin
heroOfTheVillage("save_village") {
	conditions {
		location {
			dimension = Dimensions.OVERWORLD
		}
	}
}
```

---

### `impossible`

**Description:**  
Prevents the advancement from being achieved. Useful for creating advancements that should only trigger functions.

**Properties:**  
_None._

**Example:**

```kotlin
impossible("impossible")
```

---

### `inventoryChanged`

**Description:**  
Triggers when inventory contents change.

**Properties:**

- `items`: The items involved in the inventory change.
- `slots`: The inventory slots affected.

**Example:**

```kotlin
inventoryChanged("get_diamond") {
	item {
		item(Items.DIAMOND)
	}
	slots {
		empty = rangeOrInt(1..3)
	}
}
```

---

### `itemDurabilityChanged`

**Description:**  
Triggers when item durability changes.

**Properties:**

- `delta`: The change in durability.
- `durability`: The current durability of the item.
- `item`: The item whose durability changed.

**Example:**

```kotlin
itemDurabilityChanged("tool_break") {
	delta = rangeOrInt(-10..-1)
	item {
		item(Items.DIAMOND_PICKAXE)
	}
}
```

---

### `itemUsedOnBlock`

**Description:**  
Triggers when an item is used on a block.

**Properties:**

- `location`: The location where the item was used.

**Example:**

```kotlin
itemUsedOnBlock("bone_meal_use") {
	location {
		predicate {
			locationCheck {
				block {
					blocks(Blocks.GRASS_BLOCK, Blocks.DIRT)
				}
			}
		}
	}
}
```

---

### `killedByArrow`

**Description:**  
Triggers when killed by a crossbow.

**Properties:**

- `firedFromWeapon`: The weapon used to fire the arrow.
- `uniqueEntityTypes`: The number of unique entity types involved.
- `victims`: The entities that were killed.

**Example:**

```kotlin
killedByArrow("killed_by_arrow") {
	firedFromWeapon {
		items = listOf(Items.BOW)
		components {
			enchantments {
				enchantment(Enchantments.POWER, 5)
			}
		}
	}

	uniqueEntityTypes = rangeOrInt(1..5)

	victim {
		conditions {
			entityProperties {
				type(EntityTypes.PLAYER)
			}
		}
	}
}
```

---

### `killMobNearSculkCatalyst`

**Description:**  
Triggers when a mob is killed near a sculk catalyst.

**Properties:**

- `entity`: The entity that was killed.
- `killingBlow`: Details about the killing blow.

**Example:**

```kotlin
killMobNearSculkCatalyst("kill_mob") {
	entity {
		type(EntityTypes.ZOMBIE)
	}
	killingBlow {
		sourceEntity {
			type(EntityTypes.PLAYER)
		}
	}
}
```

---

### `levitation`

**Description:**  
Triggers during levitation.

**Properties:**

- `distance`: The distance of levitation.
- `duration`: The duration of levitation.

**Example:**

```kotlin
levitation("float_up") {
	distance {
		y(10f)
	}
	duration = rangeOrInt(10..20)
}
```

---

### `lightningStrike`

**Description:**  
Triggers on a lightning strike.

**Properties:**

- `bystander`: The bystanders affected by the lightning.
- `lightning`: Details about the lightning strike.

**Example:**

```kotlin
lightningStrike("struck") {
	bystander {
		type {
			conditions {
				entityProperties {
					type(EntityTypes.CREEPER)
				}
			}
		}
	}
}
```

---

### `location`

**Description:**  
Triggers every second based on location conditions.

**Properties:**

- `location`: The specific location conditions for the trigger.

**Example:**

```kotlin
location("reach_end") {
	conditions {
		location {
			dimension = Dimensions.THE_END
		}
	}
}
```

---

### `netherTravel`

**Description:**  
Triggers when a player enters or exits the Nether.

**Properties:**

- `distance`: The distance traveled during the dimension change.
- `startPosition`: The starting position before the change.

**Example:**

```kotlin
netherTravel("enter_nether") {
	distance {
		horizontal(100f)
	}
	startPosition {
		position {
			x = rangeOrInt(0..100)
			z = rangeOrInt(0..100)
		}
	}
}
```

---

### `placedBlock`

**Description:**  
Triggers when a block is placed.

**Properties:**

- `location`: The location where the block was placed.

**Example:**

```kotlin
placedBlock("place_block") {
	conditions {
		location {
			biomes(Biomes.PLAINS)
		}
	}
}
```

---

### `playerGeneratesContainerLoot`

**Description:**  
Triggers when container loot is generated.

**Properties:**

- `lootTable`: The loot table used to generate the container loot.

**Example:**

```kotlin
playerGeneratesContainerLoot("find_treasure", LootTables.Chests.BURIED_TREASURE)
```

---

### `playerHurtEntity`

**Description:**  
Triggers when a player hurts an entity.

**Properties:**

- `damage`: Details about the damage inflicted.
- `entity`: The entity that was hurt.

**Example:**

```kotlin
playerHurtEntity("hurt_mob") {
	damage {
		taken = rangeOrDouble(5.0..10.0)
	}
}
```

---

### `playerKilledEntity`

**Description:**  
Triggers when a player kills an entity.

**Properties:**

- `entity`: The entity that was killed.
- `killingBlow`: Details about the killing blow.

**Example:**

```kotlin
playerKilledEntity("kill_mob") {
	entity {
		conditions {
			entityProperties {
				type(EntityTypes.ZOMBIE)
			}
		}
	}
}
```

---

### `playerShearedEquipment`

**Description:**  
Triggers after a player shears equipment off of a mob, such as wolf armor.

**Properties:**

- `entity`: The entity whose equipment was sheared.
- `item`: The item of equipment that was sheared off.

**Example:**

```kotlin
playerShearedEquipment("shear_wolf_armor") {
	entity {
		type(EntityTypes.WOLF)
	}
	item {
		item(Items.LEATHER)
	}
}
```

### `recipeCrafted`

**Description:**  
Triggers when a recipe is crafted.

**Properties:**

- `recipeId`: The ID of the crafted recipe.
- `ingredients`: The ingredients used in the recipe.

**Example:**

```kotlin
recipeCrafted("craft_diamond") {
	recipeId = Recipes.DIAMOND
	ingredient(Items.DIAMOND) {
		components {
			damage(0)
		}
	}
}
```

---

### `recipeUnlocked`

**Description:**  
Triggers when a recipe is unlocked.

**Properties:**

- `recipe`: The recipe that was unlocked.

**Example:**

```kotlin
recipeUnlocked("unlock_recipe", Recipes.DIAMOND)
```

---

### `rideEntityInLava`

**Description:**  
Triggers when riding an entity in lava.

**Properties:**

- `distance`: The distance traveled while riding in lava.
- `startPosition`: The starting position before riding.

**Example:**

```kotlin
rideEntityInLava("lava_ride") {
	distance {
		horizontal(10f)
	}
	startPosition {
		position {
			y = rangeOrInt(100..200)
		}
	}
}
```

---

### `shotCrossbow`

**Description:**  
Triggers when shooting a crossbow.

**Properties:**

- `item`: The crossbow item that was shot.

**Example:**

```kotlin
shotCrossbow("shoot_crossbow") {
	item {
		item(Items.CROSSBOW)
		enchantments {
			enchantment(Enchantments.MULTISHOT, 1)
		}
	}
}
```

---

### `sleptInBed`

**Description:**  
Triggers when a player sleeps in a bed.

**Properties:**  
_None._

**Example:**

```kotlin
sleptInBed("sleep_in_bed")
```

---

### `slideDownBlock`

**Description:**  
Triggers when sliding down a block.

**Properties:**

- `block`: The block being slid down.

**Example:**

```kotlin
slideDownBlock("slide_down") {
	block {
		blocks(Blocks.SNOW_BLOCK)
	}
}
```

---

### `spearMobs`

**Description:**  
Triggers when a player spears mobs.

**Properties:**

- `count`: The number of mobs speared.

**Example:**

```kotlin
spearMobs("spear_mobs") {
	count = 3
}
```

---

### `startedRiding`

**Description:**  
Triggers when a player starts riding an entity.

**Properties:**  
_None._

**Example:**

```kotlin
startedRiding("ride_horse") {
	conditions {
		vehicle {
			type(EntityTypes.HORSE)
		}
	}
}
```

---

### `summonedEntity`

**Description:**  
Triggers when an entity is summoned.

**Properties:**

- `entity`: The entity that was summoned.

**Example:**

```kotlin
summonedEntity("summon_iron_golem") {
	entity {
		type(EntityTypes.IRON_GOLEM)
	}
}
```

---

### `tameAnimal`

**Description:**  
Triggers when an animal is tamed.

**Properties:**

- `entity`: The animal that was tamed.

**Example:**

```kotlin
tameAnimal("tame_wolf") {
	entity {
		type(EntityTypes.WOLF)
	}
}
```

---

### `targetHit`

**Description:**  
Triggers when a target block is hit.

**Properties:**

- `signalStrength`: The strength of the signal when the target is hit.
- `projectile`: The projectile used to hit the target.

**Example:**

```kotlin
targetHit("hit_target") {
	signalStrength = rangeOrInt(1..15)
	projectile {
		conditions {
			entityProperties {
				type(EntityTypes.ARROW)
			}
		}
	}
}
```

---

### `thrownItemPickedUpByEntity`

**Description:**  
Triggers when a thrown item is picked up by an entity.

**Properties:**

- `entity`: The entity that picked up the item.
- `item`: The item that was picked up.

**Example:**

```kotlin
thrownItemPickedUpByEntity("feed_animal") {
	entity {
		type(EntityTypes.COW)
	}
	item {
		item(Items.WHEAT)
	}
}
```

---

### `thrownItemPickedUpByPlayer`

**Description:**  
Triggers when a thrown item is picked up by a player.

**Properties:**

- `entity`: The entity that picked up the item.
- `item`: The item that was picked up.

**Example:**

```kotlin
thrownItemPickedUpByPlayer("catch_trident") {
	item {
		item(Items.TRIDENT)
	}
}
```

---

### `tick`

**Description:**  
Triggers every tick (20 times per second).

**Properties:**

- `conditions`: Conditions that must be met for the trigger to activate.

**Example:**

```kotlin
tick("game_tick") {
	conditions {
		timeCheck(6000..18000) // Daytime only
	}
}
```

---

### `usedEnderEye`

**Description:**  
Triggers when an ender eye is used.

**Properties:**

- `distance`: The distance traveled using the ender eye.

**Example:**

```kotlin
usedEnderEye("find_stronghold") {
	distance {
		horizontal(100f)
	}
}
```

---

### `usedTotem`

**Description:**  
Triggers when a totem is used.

**Properties:**

- `item`: The totem item that was used.

**Example:**

```kotlin
usedTotem("save_life") {
	item {
		item(Items.TOTEM_OF_UNDYING)
	}
}
```

---

### `usingItem`

**Description:**  
Triggers while using an item.

**Properties:**

- `item`: The item being used.

**Example:**

```kotlin
usingItem("shield_block") {
	item {
		items = listOf(Items.SHIELD)
	}
}
```

---

### `villagerTrade`

**Description:**  
Triggers when a villager trades.

**Properties:**

- `item`: The item involved in the trade.
- `villager`: The villager involved in the trade.

**Example:**

```kotlin
villagerTrade("trade") {
	item {
		item(Items.EMERALD)
	}
	villager {
		conditions {
			entityProperties {
				team = "villager"
			}
		}
	}
}
```

---

### `voluntaryExile`

**Description:**  
Triggers when a player causes a raid in a village.

**Properties:**

- `location`: The location where the raid occurred.

**Example:**

```kotlin
voluntaryExile("raid_village") {
	conditions {
		location {
			dimension = Dimensions.OVERWORLD
		}
	}
}
```

---

Each trigger example demonstrates the basic usage with common properties and conditions. You can customize these triggers by adding more conditions and requirements to suit your specific advancement needs.
