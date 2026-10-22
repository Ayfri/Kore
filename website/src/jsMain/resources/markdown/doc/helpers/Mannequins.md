---
root: .components.layouts.MarkdownLayout
title: How to Summon a Minecraft Mannequin with a Player Skin
nav-title: Mannequins
description: Summon a Minecraft mannequin NPC wearing any player's skin and armor, then set its pose, name, layers and description, as a /summon command or in Kotlin.
keywords: minecraft mannequin, summon mannequin, mannequin player skin, mannequin profile, mannequin pose, minecraft npc, player statue, datapack, kore
date-created: 2026-01-25
date-modified: 2026-10-22
routeOverride: /docs/helpers/mannequins
---

# Mannequins

A mannequin (`minecraft:mannequin`, added in Java Edition 1.21.9) is an entity drawn with the player model. It wears any player's skin,
holds a pose like crouching or sleeping and never walks around on its own, which makes it the usual base for NPCs, shopkeepers and
player statues in a datapack. Mannequins only spawn through commands such as `summon`.

> [!FOOTPRINT]
> Only the entities you summon: each `summon` creates one `minecraft:mannequin`. No objective, storage, forceloaded chunk, tick or load function.

## Summon a Mannequin with a Player Skin

Put the player's name in `profile`: the game looks up that player's current skin and puts it on the mannequin.

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:"Ayfri"}
```

With Kore, `playerProfile(name)` sets the same field, written in its object form:

```kotlin
val statue = mannequin {
	playerProfile("Ayfri")
}

summon(statue.entityType, vec3(), statue.toNbt())
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:{name:"Ayfri"}}
```

Without a `profile`, a mannequin wears the default slim Alex skin.

Type a player name below, pick a pose and the layers to hide, and copy the `summon` command or the Kotlin that writes it.

{{{ .components.doc.MannequinBuilder }}}

## Profiles

`profile` is the same value as the [`minecraft:profile` component](https://minecraft.wiki/w/Data_component_format#profile) of player
heads. It takes four forms:

| Form       | SNBT                                                                     | Kore                                                                                 | Skin shown                                                  |
|------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------------------|-------------------------------------------------------------|
| Name       | `profile:"Ayfri"` or `profile:{name:"Ayfri"}`                            | `playerProfile("Ayfri")`                                                             | The current skin of that player, looked up by name          |
| UUID       | `profile:{id:[I;-2040022415,-1202044924,-1353431686,-1758138665]}`       | `playerProfile(id = uuid("8667ba71-b85a-4004-af54-457a9734eed7"))`                   | The current skin of that player, kept after a name change   |
| Properties | `profile:{properties:[{name:"textures",value:"<base64>"}]}`              | `playerProfile { property("textures", "<base64>") }`                                 | The skin encoded in `value`, with no lookup                 |
| Texture    | `profile:{model:"slim",texture:"minecraft:entity/player/slim/alex"}`     | `textureProfile(Textures.Entity.Player.Slim.ALEX) { model = MannequinModel.SLIM }`   | A texture shipped by the game or a resource pack            |

### Player Profile

A player profile uses a player's name or UUID to fetch their skin and properties. A UUID keeps pointing at the same player after they
rename their account.

```kotlin
mannequin {
	playerProfile(name = "Steve", id = uuid("8667ba71-b85a-4004-af54-457a9734eed7"))
}
```

`property(name, value, signature)` adds a signed `textures` property, the form skin sites export, so the skin no longer depends on the
player:

```kotlin
mannequin {
	playerProfile {
		property("textures", "<base64 value>", "<signature>")
	}
}
```

### Texture Profile

A texture profile points at a texture ID instead of a player. `model` picks the arm width, `WIDE` (4 pixels, Steve) or `SLIM` (3 pixels,
Alex).

```kotlin
mannequin {
	textureProfile(Textures.Entity.Player.Slim.ALEX) {
		model = MannequinModel.SLIM
	}
}
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:{model:"slim",texture:"minecraft:entity/player/slim/alex"}}
```

The game's skins are `ALEX`, `ARI`, `EFE`, `KAI`, `MAKENA`, `NOOR`, `STEVE`, `SUNNY` and `ZURI`, under `Textures.Entity.Player.Slim` and
`Textures.Entity.Player.Wide`. A texture from a resource pack is given as a `namespace:path` string, a bare path stays in `minecraft`:

```kotlin
mannequin {
	textureProfile("mypack:entity/npc/bob")
}
```

`elytra` takes an `ElytraTextureArgument`, like `Textures.Entity.Equipment.Wings.ELYTRA`, and `cape` a `CapeTextureArgument`:

```kotlin
mannequin {
	textureProfile(Textures.Entity.Player.Wide.STEVE) {
		elytra = Textures.Entity.Equipment.Wings.ELYTRA
		cape = CapeTextureArgument("capes/guild", "mypack")
	}
}
```

A player profile also takes `texture`, `cape`, `elytra` and `model` overrides, applied on top of the looked-up skin:

```kotlin
mannequin {
	playerProfile("Ayfri") {
		model = MannequinModel.SLIM
	}
}
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:{model:"slim",name:"Ayfri"}}
```

## Customization

Mannequins support additional fields for further customization:

```kotlin
mannequin {
	description = textComponent("Shopkeeper", Color.GOLD)
	hideDescription = false
	immovable = true
	pose = MannequinPose.CROUCHING
}
```

The rows for `CustomName`, `CustomNameVisible`, `Glowing`, `Invulnerable`, `NoGravity`, `Rotation`, `Silent` and `Tags` are entity fields
a mannequin shares with every entity, they come from the [`EntitySummonData`](/docs/oop/items#shared-entity-fields) base, which lists the
others too (`motion`, `fire`, `air`...).

| Field               | Kore                                     | Default    | Effect                                                              |
|---------------------|------------------------------------------|------------|---------------------------------------------------------------------|
| `CustomName`        | `customName`                             | none       | Name shown above the mannequin when it is looked at                 |
| `CustomNameVisible` | `customNameVisible`                      | `false`    | Shows the name even when the player isn't looking at the mannequin  |
| `description`       | `description`                            | `NPC`      | Text shown under the name, where a player's `below_name` score goes |
| `equipment`         | `equipment { }`                          | none       | Items worn and held, see [Equipment](#equipment)                    |
| `Glowing`           | `glowing`                                | `false`    | Draws the glowing outline around the mannequin                      |
| `hidden_layers`     | `hiddenLayers(...)`                      | none       | Skin layers left out, see [Hidden Layers](#hidden-layers)           |
| `hide_description`  | `hideDescription`                        | `false`    | Removes the description line                                        |
| `immovable`         | `immovable`                              | `false`    | Keeps the mannequin from being moved                                |
| `Invulnerable`      | `invulnerable`                           | `false`    | Makes the mannequin immune to damage                                |
| `main_hand`         | `mainHand`                               | `right`    | Hand holding the main hand item                                     |
| `NoGravity`         | `noGravity`                              | `false`    | Keeps the mannequin from falling                                    |
| `pose`              | `pose`                                   | `standing` | Body pose, see [Pose](#pose)                                        |
| `profile`           | `playerProfile(...)`, `textureProfile()` | slim Alex  | Skin, see [Profiles](#profiles)                                     |
| `Rotation`          | `rotation(yaw, pitch)`                   | `0, 0`     | Facing, in degrees                                                  |
| `Silent`            | `silent`                                 | `false`    | Mutes the sounds of the mannequin                                   |
| `Tags`              | `tags`                                   | none       | Scoreboard tags, matched by `tag=` in selectors                     |

### Pose

The `pose` field sets the mannequin's pose, a subset of the poses players take:

- `STANDING`
- `CROUCHING`
- `SWIMMING`
- `FALL_FLYING`
- `SLEEPING`

{{{ .components.doc.MannequinPoses }}}

## Hidden Layers

The second layer of a skin (hat, jacket, sleeves, pants legs) and the cape can be hidden one by one with `hiddenLayers`, the same parts
the Skin Customization screen of the game toggles.

Available layers:

- `CAPE`
- `HAT`
- `JACKET`
- `LEFT_PANTS_LEG`
- `LEFT_SLEEVE`
- `RIGHT_PANTS_LEG`
- `RIGHT_SLEEVE`

```kotlin
mannequin {
	hiddenLayers(MannequinLayer.JACKET, MannequinLayer.HAT)
}
```

## Equipment

`equipment { }` puts items in the mannequin's equipment slots, set by `EquipmentSlot` (`HEAD`, `CHEST`, `LEGS`, `FEET`, `MAINHAND`, `OFFHAND`,
`BODY` and `SADDLE`). Each slot takes an item stack, and slots left out stay empty.

```kotlin
val guard = mannequin {
	equipment {
		this[EquipmentSlot.CHEST] = itemStack(Items.DIAMOND_CHESTPLATE)
		this[EquipmentSlot.HEAD] = itemStack(Items.GOLDEN_HELMET)
		this[EquipmentSlot.MAINHAND] = itemStack(Items.IRON_SWORD)
	}
}

summon(guard.entityType, vec3(), guard.toNbt())
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {equipment:{chest:{id:"minecraft:diamond_chestplate"},head:{id:"minecraft:golden_helmet"},mainhand:{id:"minecraft:iron_sword"}}}
```

## Main Hand

`mainHand` sets which hand is the main hand of the mannequin, the one holding its main hand item.

```kotlin
mannequin {
	mainHand = MannequinHand.LEFT
}
```

## A Shopkeeper NPC

Put together, the fields give a crouching shopkeeper named Bob that wears a player's skin, holds an emerald, shows a gold label under its
name and can't be pushed away from its stall or hurt:

```kotlin
val shopkeeper = mannequin {
	customName = textComponent("Bob")
	customNameVisible = true
	description = textComponent("Shopkeeper", Color.GOLD)
	equipment { this[EquipmentSlot.MAINHAND] = itemStack(Items.EMERALD) }
	hiddenLayers(MannequinLayer.CAPE)
	immovable = true
	invulnerable = true
	mainHand = MannequinHand.LEFT
	playerProfile("Ayfri")
	pose = MannequinPose.CROUCHING
	tags = listOf("shopkeeper")
}

summon(shopkeeper.entityType, vec3(0, 64, 0), shopkeeper.toNbt())
```

```mcfunction
summon minecraft:mannequin 0 64 0 {CustomName:"Bob",CustomNameVisible:1b,Invulnerable:1b,Tags:["shopkeeper"],description:{text:"Shopkeeper",color:"gold"},equipment:{mainhand:{id:"minecraft:emerald"}},hidden_layers:["cape"],immovable:1b,main_hand:"left",pose:"crouching",profile:{name:"Ayfri"}}
```

`Mannequin` doesn't cover every field a living entity has: attributes (such as `minecraft:scale`) and `Health` aren't part of it. They can
be merged into `toNbt()` before the `summon`, the way `Mannequin.summon` adds `UUID`:

```kotlin
val giant = mannequin { playerProfile("Ayfri") }
summon(giant.entityType, vec3(), nbt {
	giant.toNbt().forEach { (key, value) -> this[key] = value }
	this["Health"] = 40f
})
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:{name:"Ayfri"},Health:40.0f}
```

## Clickable NPC

A mannequin only stands there. `npc(name) { }` pairs it with an [interaction](/docs/helpers/interactions) sized to its pose, so
right and left clicks run Kotlin callbacks:

```kotlin
val greeter = npc("greeter") {
	mannequin {
		customName("Bob", Color.GOLD)
		playerProfile("Ayfri")
	}
	onRightClick { say("Welcome!") }
}

function("place_greeter") { greeter.summon(vec3(0, 64, 0)) }
```

The mannequin and the interaction are summoned together, and `clicked` in a callback selects the mannequin. The hitbox sizes,
the per-NPC scoreboard state and a shopkeeper opening a menu are in [NPCs](/docs/helpers/interactions#npcs).

## Summoning and OOP Handle

Use `summon()` inside a function to spawn the mannequin and receive a `MannequinEntity` handle.
The handle uniquely identifies this instance by its UUID, giving access to all OOP entity commands.

```kotlin
val myMannequin = mannequin {
	pose = MannequinPose.STANDING
	playerProfile("Ayfri")
}

// summon() spawns the entity and hands back a handle pointing at that exact instance
val handle: MannequinEntity = myMannequin.summon(vec3(0, 64, 0))

// every Entity OOP extension works on the handle
handle.swing(SwingHand.MAINHAND)
handle.kill()
handle.teleportTo(0, 64, 5)
```

The generated selector is `@e[limit=1,nbt={UUID:[I;...]},type=minecraft:mannequin]`, uniquely targeting the spawned instance.

`MannequinEntity` lives in the `oop` module and extends `Entity`, so every entity-scoped extension applies to it -
see [Entities & Players](/docs/oop/entities-and-players) for the full list.
