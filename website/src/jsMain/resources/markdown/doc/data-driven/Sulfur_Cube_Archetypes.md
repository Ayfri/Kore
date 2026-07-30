---
root: .components.layouts.MarkdownLayout
title: Sulfur Cube Archetypes - Custom Sulfur Cube Behavior in Kore
nav-title: Sulfur Cube Archetypes
description: Define custom sulfur cube archetypes for Minecraft 26.2+ with Kore's type-safe Kotlin DSL. Configure attribute modifiers, buoyancy, and valid item contents.
keywords: minecraft sulfur cube, datapack sulfur_cube_archetype, kore sulfur cube archetype, custom mob archetype datapack, sulfur cube attribute modifiers
date-created: 2026-07-29
date-modified: 2026-07-29
routeOverride: /docs/data-driven/sulfur-cube-archetypes
---

# Sulfur Cube Archetypes

Sulfur cube archetypes are data-driven JSON files that control the attribute modifiers, buoyancy, and valid item
contents of a sulfur cube. Each archetype is referenced by its file name and can be assigned to sulfur cube
entities to change their behavior.

## File Structure

```
data/<namespace>/sulfur_cube_archetype/<name>.json
```

## Creating an Archetype

Use `sulfurCubeArchetype` on your `DataPack`. Pass an `ItemTagArgument` describing which items the sulfur cube
can hold, then add attribute modifiers with `modifier`:

```kotlin
import io.github.ayfri.kore.commands.AttributeModifierOperation
import io.github.ayfri.kore.features.sulfurcubearchetype.modifier
import io.github.ayfri.kore.features.sulfurcubearchetype.sulfurCubeArchetype
import io.github.ayfri.kore.generated.Attributes
import io.github.ayfri.kore.generated.Tags

datapack.sulfurCubeArchetype("regular", items = Tags.Item.SWORDS) {
	buoyant = true

	modifier(
		amount = 4.0,
		attribute = Attributes.MAX_HEALTH,
		id = "sulfur_cube_archetype:regular",
		operation = AttributeModifierOperation.ADD_VALUE,
	)
}
```

## SulfurCubeArchetype Fields

| Field                | Type                                                | Description                                                           |
|----------------------|-----------------------------------------------------|-----------------------------------------------------------------------|
| `attributeModifiers` | `MutableList<SulfurCubeArchetypeAttributeModifier>` | Attribute modifiers applied while the archetype is active.            |
| `buoyant`            | `Boolean`                                           | Whether the sulfur cube floats on liquids.                            |
| `items`              | `ItemTagArgument`                                   | Item tag defining which items the sulfur cube can hold. **Required.** |

### SulfurCubeArchetypeAttributeModifier Fields

| Field       | Type                         | Description                                               |
|-------------|------------------------------|-----------------------------------------------------------|
| `amount`    | `Double`                     | The amount applied by the modifier.                       |
| `attribute` | `AttributeArgument`          | The attribute being modified.                             |
| `id`        | `String`                     | The identifier of the modifier.                           |
| `operation` | `AttributeModifierOperation` | How `amount` is combined with the attribute's base value. |

## See Also

- [Item Predicates](/docs/concepts/components#item-predicates) - Match items against tags
- [Tags](/docs/data-driven/tags) - Group items into reusable tags for `items`

### External Resources

- [Minecraft Wiki: Sulfur cube archetype definition](https://minecraft.wiki/w/Sulfur_cube_archetype_definition)
