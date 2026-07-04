package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Target for `set_name`: item display name or custom name. */
@Serializable(with = SetNameTarget.Companion.SetNameTargetSerializer::class)
enum class SetNameTarget {
	CUSTOM_NAME,
	ITEM_NAME;

	companion object {
		data object SetNameTargetSerializer : LowercaseSerializer<SetNameTarget>(entries)
	}
}

/**
 * Sets the item name. Mirrors `minecraft:set_name`. You can target the item display name
 * or the `custom_name` component and optionally resolve from an entity context.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable
data class SetName(
	override var conditions: PredicateAsList? = null,
	var entity: Source? = null,
	var name: ChatComponents,
	var target: SetNameTarget? = null,
) : ItemFunction()

/** Add a `set_name` step with a plain string converted to chat components. */
fun ItemModifier.setName(
	name: String,
	color: Color? = null,
	entity: Source? = null,
	componentBlock: ChatComponent.() -> Unit = {},
	block: SetName.() -> Unit = {},
) =
	SetName(entity = entity, name = textComponent(name, color, componentBlock)).apply(block).also { modifiers += it }

/** Add a `set_name` step from an existing component. */
fun ItemModifier.setName(name: ChatComponent, entity: Source? = null, block: SetName.() -> Unit = {}) {
	modifiers += SetName(entity = entity, name = ChatComponents(name)).apply(block)
}
