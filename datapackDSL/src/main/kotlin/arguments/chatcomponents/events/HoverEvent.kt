package arguments.chatcomponents.events

import arguments.*
import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.hover.Contents
import arguments.chatcomponents.hover.ContentsEntity
import arguments.chatcomponents.hover.ContentsItem
import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import serializers.LowercaseSerializer
import serializers.NbtAsJsonTextComponentSerializer
import utils.asArg

@Serializable
data class HoverEvent(
	var action: HoverAction,
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var value: NbtTag,
	var contents: Contents? = null,
) {
	fun toNbtTag() = buildNbtCompound {
		this["action"] = action.asArg()
		this["value"] = value
		contents?.let { this["contents"] = it.toNbtTag() }
	}
}

fun HoverEvent.showEntity(entity: Argument.Entity) = apply {
	action = HoverAction.SHOW_ENTITY
	value = entity.asString().nbt
}

fun HoverEvent.showEntity(type: Argument.EntitySummon, name: ChatComponent? = null, id: Argument.UUID? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntity(type.asString(), name, id?.asString())
}

fun HoverEvent.showEntity(type: Argument.EntitySummon, name: String? = null, id: Argument.UUID? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntity(type.asString(), name?.let { text(it) }, id?.asString())
}

fun HoverEvent.showItem(item: Argument.Item, count: Int) = apply {
	action = HoverAction.SHOW_ITEM
	contents = ContentsItem(item.asId(), count, item.nbtData?.let { stringifiedNbt(it) })
}

fun HoverEvent.showText(block: ChatComponents.() -> Unit) = apply {
	action = HoverAction.SHOW_TEXT
	value = ChatComponents().apply(block).toNbtTag()
}

fun HoverEvent.showText(text: String, block: ChatComponent.() -> Unit = {}) = apply {
	action = HoverAction.SHOW_TEXT
	value = textComponent(text, block).toNbtTag()
}

@Serializable(HoverAction.Companion.HoverActionSerializer::class)
enum class HoverAction {
	SHOW_TEXT,
	SHOW_ITEM,
	SHOW_ENTITY;

	companion object {
		val values = values()

		object HoverActionSerializer : LowercaseSerializer<HoverAction>(values)
	}
}