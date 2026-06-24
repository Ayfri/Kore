package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.utils.nbt
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

/** Alias for the `minecraft:custom_data` component, backed by [CustomComponent]. */
typealias CustomDataComponent = CustomComponent

/**
 * Attaches arbitrary custom NBT data (`minecraft:custom_data`) for use by datapacks or mods.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#custom_data
 */
fun ComponentsScope.customData(nbt: NbtTag) = apply { this[ItemComponentTypes.CUSTOM_DATA] = CustomDataComponent(nbt) }
fun ComponentsScope.customData(block: NbtCompoundBuilder.() -> Unit) = customData(nbt(block))
