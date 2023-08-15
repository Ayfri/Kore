package arguments.chatcomponents.events

import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.hover.Contents
import arguments.chatcomponents.hover.ContentsEntity
import arguments.chatcomponents.hover.ContentsItem
import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import arguments.colors.Color
import arguments.types.EntityArgument
import arguments.types.literals.UUIDArgument
import arguments.types.resources.EntityTypeArgument
import arguments.types.resources.ItemArgument
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import serializers.LowercaseSerializer
import serializers.NbtAsJsonSerializer
import utils.asArg
import utils.nbt
import utils.set
import utils.stringifiedNbt
import kotlinx.serialization.Serializable

@Serializable
data class HoverEvent(
	var action: HoverAction,
	@Serializable(with = NbtAsJsonSerializer::class) var value: NbtTag,
	var contents: Contents? = null,
) {
	fun toNbtTag() = buildNbtCompound {
		this["action"] = action.asArg()
		this["value"] = value
		contents?.let { this["contents"] = it.toNbtTag() }
	}
}

fun HoverEvent.showEntity(entity: EntityArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	value = entity.asString().nbt
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: ChatComponent? = null, id: UUIDArgument? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntity(type.asString(), name, id?.asString())
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, id: UUIDArgument? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntity(type.asString(), name?.let { text(it) }, id?.asString())
}

fun HoverEvent.showItem(item: ItemArgument, count: Int) = apply {
	action = HoverAction.SHOW_ITEM
	contents = ContentsItem(item.asId(), count, item.nbtData?.let { stringifiedNbt(it) })
}

fun HoverEvent.showText(block: ChatComponents.() -> Unit) = apply {
	action = HoverAction.SHOW_TEXT
	value = ChatComponents().apply(block).toNbtTag()
}

fun HoverEvent.showText(text: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) = apply {
	action = HoverAction.SHOW_TEXT
	value = textComponent(text, color, block).toNbtTag()
}

@Serializable(HoverAction.Companion.HoverActionSerializer::class)
enum class HoverAction {
	SHOW_TEXT,
	SHOW_ITEM,
	SHOW_ENTITY;

	companion object {
		data object HoverActionSerializer : LowercaseSerializer<HoverAction>(entries)
	}
}
