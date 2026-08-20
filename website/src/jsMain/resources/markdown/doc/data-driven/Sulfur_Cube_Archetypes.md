---
root: .components.layouts.MarkdownLayout
title: Sulfur Cube Archetypes - Custom Sulfur Cube Behavior in Kore
nav-title: Sulfur Cube Archetypes
description: Define custom sulfur cube archetypes for Minecraft 26.2+ with Kore's Kotlin DSL. Configure attribute modifiers, buoyancy, contact damage, explosion, knockback and item contents.
keywords: minecraft sulfur cube, datapack sulfur_cube_archetype, kore sulfur cube archetype, custom mob archetype datapack, sulfur cube attribute modifiers
date-created: 2026-07-29
date-modified: 2026-08-20
routeOverride: /docs/data-driven/sulfur-cube-archetypes
---

# Sulfur Cube Archetypes

Sulfur cube archetypes are data-driven JSON files that control the attribute modifiers, buoyancy, contact damage,
explosion, knockback, and valid item contents of a sulfur cube. Each archetype is referenced by its file name and can
be assigned to sulfur cube entities to change their behavior.

## File Structure

```
data/<namespace>/sulfur_cube_archetype/<name>.json
```

## Creating an Archetype

Use `sulfurCubeArchetype` on your `DataPack`. Pass an `ItemTagArgument` describing which items the sulfur cube can
hold and a `SulfurCubeArchetypeKnockbackModifiers` for the knockback it deals, then add attribute modifiers with
`modifier`, contact damage with `contactDamage`, and an explosion with `explosion`:

```kotlin
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.sulfurcubearchetype.SulfurCubeArchetypeKnockbackModifiers
import io.github.ayfri.kore.features.sulfurcubearchetype.contactDamage
import io.github.ayfri.kore.features.sulfurcubearchetype.explosion
import io.github.ayfri.kore.features.sulfurcubearchetype.modifier
import io.github.ayfri.kore.features.sulfurcubearchetype.sulfurCubeArchetype
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.DamageTypes
import io.github.ayfri.kore.generated.Tags

datapack.sulfurCubeArchetype(
	"regular",
	items = Tags.Item.SWORDS,
	knockbackModifiers = SulfurCubeArchetypeKnockbackModifiers(horizontalPower = 0.4f, verticalPower = 0.2f),
) {
	buoyant = true

	contactDamage(amount = 3f, damageType = DamageTypes.GENERIC, attributeToSource = true)
	explosion(fuse = 80, power = 3, causesFire = true)

	modifier(
		amount = 4.0,
		attribute = Attributes.MAX_HEALTH,
		id = "sulfur_cube_archetype:regular",
		operation = AttributeModifierOperation.ADD_VALUE,
	)
}
```

## SulfurCubeArchetype Fields

| Field                | Type                                                | Description                                                               |
|----------------------|-----------------------------------------------------|---------------------------------------------------------------------------|
| `attributeModifiers` | `MutableList<SulfurCubeArchetypeAttributeModifier>` | Attribute modifiers applied while the archetype is active.                |
| `buoyant`            | `Boolean`                                           | Whether the sulfur cube floats on liquids.                                |
| `contactDamage`      | `SulfurCubeArchetypeContactDamage?`                 | Damage dealt to entities that touch the sulfur cube, omitted when `null`. |
| `explosion`          | `SulfurCubeArchetypeExplosion?`                     | The explosion triggered by the sulfur cube, omitted when `null`.          |
| `items`              | `ItemTagArgument`                                   | Item tag defining which items the sulfur cube can hold. **Required.**     |
| `knockbackModifiers` | `SulfurCubeArchetypeKnockbackModifiers`             | Knockback dealt to entities that touch the sulfur cube. **Required.**     |

### SulfurCubeArchetypeAttributeModifier Fields

| Field       | Type                         | Description                                               |
|-------------|------------------------------|-----------------------------------------------------------|
| `amount`    | `Double`                     | The amount applied by the modifier.                       |
| `attribute` | `AttributeArgument`          | The attribute being modified.                             |
| `id`        | `String`                     | The identifier of the modifier.                           |
| `operation` | `AttributeModifierOperation` | How `amount` is combined with the attribute's base value. |

### SulfurCubeArchetypeContactDamage Fields

| Field               | Type                 | Description                                                        |
|---------------------|----------------------|--------------------------------------------------------------------|
| `amount`            | `Float`              | The amount of damage dealt.                                        |
| `attributeToSource` | `Boolean`            | Whether the damage is attributed to the sulfur cube as its source. |
| `damageType`        | `DamageTypeArgument` | The type of damage dealt.                                          |

### SulfurCubeArchetypeExplosion Fields

| Field        | Type      | Description                                         |
|--------------|-----------|-----------------------------------------------------|
| `causesFire` | `Boolean` | Whether the explosion sets blocks on fire.          |
| `fuse`       | `Int`     | The delay, in ticks, before the explosion goes off. |
| `power`      | `Int`     | The explosion's power.                              |

### SulfurCubeArchetypeKnockbackModifiers Fields

| Field             | Type    | Description                     |
|-------------------|---------|---------------------------------|
| `horizontalPower` | `Float` | The horizontal knockback power. |
| `verticalPower`   | `Float` | The vertical knockback power.   |

## See Also

- [Item Predicates](/docs/concepts/components#item-predicates) - Match items against tags
- [Tags](/docs/data-driven/tags) - Group items into reusable tags for `items`

### External Resources

- [Minecraft Wiki: Sulfur cube archetype definition](https://minecraft.wiki/w/Sulfur_cube_archetype_definition)
