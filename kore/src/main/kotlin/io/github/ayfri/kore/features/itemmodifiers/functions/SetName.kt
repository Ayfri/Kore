package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SetNameTarget.Companion.SetNameTargetSerializer::class)
enum class SetNameTarget {
	CUSTOM_NAME,
	ITEM_NAME;

	companion object {
		data object SetNameTargetSerializer : LowercaseSerializer<SetNameTarget>(entries)
	}
}

@Serializable
data class SetName(
	override var conditions: PredicateAsList? = null,
	var entity: Source? = null,
	var name: ChatComponents,
	var target: SetNameTarget? = null,
) : ItemFunction()

fun ItemModifier.setName(
	name: String,
	color: Color? = null,
	entity: Source? = null,
	componentBlock: ChatComponent.() -> Unit = {},
	block: SetName.() -> Unit = {},
) =
	SetName(entity = entity, name = textComponent(name, color, componentBlock)).apply(block).also { modifiers += it }

fun ItemModifier.setName(name: ChatComponent, entity: Source? = null, block: SetName.() -> Unit = {}) {
	modifiers += SetName(entity = entity, name = ChatComponents(name)).apply(block)
}
