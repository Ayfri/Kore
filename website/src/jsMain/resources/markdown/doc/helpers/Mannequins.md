---
root: .components.layouts.MarkdownLayout
title: How to Summon a Minecraft Mannequin with a Player Skin
nav-title: Mannequins
description: Summon a Minecraft mannequin wearing any player's skin, then set its pose, hidden skin layers, main hand and description, as a /summon command or in Kotlin.
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

## Profiles

`profile` is the same value as the [`minecraft:profile` component](https://minecraft.wiki/w/Data_component_format#profile) of player
heads. It takes four forms:

| Form       | SNBT                                                                     | Kore                                                                                 | Skin shown                                                  |
|------------|--------------------------------------------------------------------------|--------------------------------------------------------------------------------------|-------------------------------------------------------------|
| Name       | `profile:"Ayfri"` or `profile:{name:"Ayfri"}`                            | `playerProfile("Ayfri")`                                                             | The current skin of that player, looked up by name          |
| UUID       | `profile:{id:[I;-2040022415,-1202044924,-1353431686,-1758138665]}`       | `playerProfile(id = uuid("8667ba71-b85a-4004-af54-457a9734eed7"))`                   | The current skin of that player, kept after a name change   |
| Properties | `profile:{properties:[{name:"textures",value:"<base64>"}]}`              | `playerProfile { property("textures", "<base64>") }`                                 | The skin encoded in `value`, with no lookup                 |
| Texture    | `profile:{texture:"entity/player/slim/alex",model:"slim"}`               | `textureProfile("entity/player/slim/alex") { model = MannequinModel.SLIM }`          | A texture shipped by the game or a resource pack            |

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

A texture profile points at a texture ID instead of a player, a texture from the game (`entity/player/wide/steve`,
`entity/player/slim/alex`...) or from a resource pack. `model` picks the arm width, `WIDE` (4 pixels, Steve) or `SLIM` (3 pixels, Alex),
and `cape` and `elytra` take a texture ID the same way.

```kotlin
mannequin {
	textureProfile("entity/player/slim/alex") {
		model = MannequinModel.SLIM
	}
}
```

```mcfunction
summon minecraft:mannequin ~ ~ ~ {profile:{model:"slim",texture:"minecraft:entity/player/slim/alex"}}
```

The game also accepts a name or UUID with `texture`, `cape`, `elytra` or `model` overrides in the same profile, like
`{name:"Ayfri",model:"slim"}`. That combination isn't possible with Kore's builders because `PlayerProfile` and `TextureProfile` are
separate types.

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

| Field              | Kore                                     | Default    | Effect                                                              |
|--------------------|------------------------------------------|------------|---------------------------------------------------------------------|
| `description`      | `description`                            | `NPC`      | Text shown under the name, where a player's `below_name` score goes |
| `hidden_layers`    | `hiddenLayers(...)`                      | none       | Skin layers left out, see [Hidden Layers](#hidden-layers)           |
| `hide_description` | `hideDescription`                        | `false`    | Removes the description line                                        |
| `immovable`        | `immovable`                              | `false`    | Keeps the mannequin from being moved                                |
| `main_hand`        | `mainHand`                               | `right`    | Hand holding the main hand item                                     |
| `pose`             | `pose`                                   | `standing` | Body pose, see [Pose](#pose)                                        |
| `profile`          | `playerProfile(...)`, `textureProfile()` | slim Alex  | Skin, see [Profiles](#profiles)                                     |

### Pose

The `pose` field sets the mannequin's pose, a subset of the poses players take:

- `STANDING`
- `CROUCHING`
- `SWIMMING`
- `FALL_FLYING`
- `SLEEPING`

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

## Main Hand

`mainHand` sets which hand is the main hand of the mannequin, the one holding its main hand item.

```kotlin
mannequin {
	mainHand = MannequinHand.LEFT
}
```

## A Shopkeeper NPC

Put together, the fields give a crouching shopkeeper that wears a player's skin, shows a gold label under its name and can't be pushed
away from its stall:

```kotlin
val shopkeeper = mannequin {
	description = textComponent("Shopkeeper", Color.GOLD)
	hiddenLayers(MannequinLayer.CAPE)
	immovable = true
	mainHand = MannequinHand.LEFT
	playerProfile("Ayfri")
	pose = MannequinPose.CROUCHING
}

summon(shopkeeper.entityType, vec3(0, 64, 0), shopkeeper.toNbt())
```

```mcfunction
summon minecraft:mannequin 0 64 0 {description:{text:"Shopkeeper",color:"gold"},hidden_layers:["cape"],immovable:1b,main_hand:"left",pose:"crouching",profile:{name:"Ayfri"}}
```

`Mannequin` covers the fields specific to mannequins. Equipment (`equipment`) and the fields every living entity has, like `CustomName`,
aren't part of it yet.

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

The generated selector is `@e[type=minecraft:mannequin,nbt={UUID:[I;...]}]`, uniquely targeting the spawned instance.

`MannequinEntity` lives in the `oop` module and extends `Entity`, so every entity-scoped extension applies to it -
see [Entities & Players](/docs/oop/entities-and-players) for the full list.
