package io.github.ayfri.kore.arguments.chatcomponents.events

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.hover.Contents
import io.github.ayfri.kore.arguments.chatcomponents.hover.ContentsEntityIntArray
import io.github.ayfri.kore.arguments.chatcomponents.hover.ContentsEntityUUID
import io.github.ayfri.kore.arguments.chatcomponents.hover.ContentsItem
import io.github.ayfri.kore.arguments.chatcomponents.text
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound

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
	contents = ContentsEntityUUID(type.asString(), name, id?.asString())
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: ChatComponent? = null, id: IntArray? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(type.asString(), name, id)
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, id: UUIDArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityUUID(type.asString(), name?.let { text(it) }, id.asString())
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, id: IntArray) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(type.asString(), name?.let { text(it) }, id)
}

fun HoverEvent.showItem(item: ItemArgument, count: Int) = apply {
	action = HoverAction.SHOW_ITEM
	contents = ContentsItem(item.asId(), count, item.components?.toString())
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
