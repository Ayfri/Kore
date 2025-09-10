package io.github.ayfri.kore.features.dialogs.body

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

@Serializable
data class Item(
	var item: ItemStack,
	var description: ItemDescription? = null,
	var showDecorations: Boolean? = null,
	var showTooltip: Boolean? = null,
	var height: Int? = null,
	var width: Int? = null,
) : DialogBody()

@Serializable(with = ItemDescription.Companion.ItemDescriptionSerializer::class)
data class ItemDescription(
	var contents: ChatComponents,
	var width: Int? = null,
) {
	companion object {
		data object ItemDescriptionSerializer : SinglePropertySimplifierSerializer<ItemDescription, ChatComponents>(
			ItemDescription::class,
			ItemDescription::contents,
		)
	}
}

fun BodyContainer.item(item: ItemArgument, description: ChatComponents? = null, block: Item.() -> Unit = {}) = apply {
	val body = Item(itemStack(item), description?.let { ItemDescription(it) }).apply(block)
	bodies += body
}

fun BodyContainer.item(item: ItemArgument, description: String, block: Item.() -> Unit = {}) =
	item(item, description = textComponent(description), block = block)

fun Item.item(item: ItemArgument, count: Short? = null, block: Components.() -> Unit = {}) {
	this.item = itemStack(item, count, block)
}

fun Item.description(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
	description = ItemDescription(textComponent(text, color, block))
}

fun Item.description(text: String, width: Int, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) {
	description = ItemDescription(textComponent(text, color, block), width)
}
