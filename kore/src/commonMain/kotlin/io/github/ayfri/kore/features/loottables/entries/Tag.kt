package io.github.ayfri.kore.features.loottables.entries

import io.github.ayfri.kore.arguments.types.ItemOrTagArgument
import io.github.ayfri.kore.features.itemmodifiers.InlinableItemModifier
import io.github.ayfri.kore.features.predicates.conditions.PredicateCondition
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Loot entry that drops the [items], given by ID or by item tag (`"#minecraft:arrows"`).
 *
 * With [expand] `true` the game picks one of the items at random, otherwise it drops all of them.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/loot-tables
 * Minecraft Wiki: https://minecraft.wiki/w/Loot_table
 */
@Serializable
data class Tag(
	override var condition: PredicateCondition? = null,
	var expand: Boolean? = null,
	var items: InlinableList<ItemOrTagArgument>,
	override var modifier: InlinableItemModifier? = null,
	var quality: Int? = null,
	var weight: Int? = null,
) : LootEntry()

/**
 * Add and configure a Tag entry dropping [items].
 *
 * ```
 * tag(Tags.Item.ARROWS)                   // "items": "#minecraft:arrows"
 * tag(Items.APPLE, Items.CARROT, expand = true)  // "items": ["minecraft:apple", "minecraft:carrot"], one at random
 * ```
 */
fun LootEntries.tag(vararg items: ItemOrTagArgument, expand: Boolean? = null, block: Tag.() -> Unit = {}) {
	add(Tag(expand = expand, items = items.toList()).apply(block))
}
