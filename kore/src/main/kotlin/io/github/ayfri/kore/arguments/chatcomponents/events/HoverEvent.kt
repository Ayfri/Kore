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
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.asArg
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class HoverEvent(
	var action: HoverAction,
	@Serializable(with = NbtAsJsonSerializer::class)
	var value: NbtTag,
	var contents: Contents? = null,
) {
	fun toNbtTag() = buildNbtCompound {
		this["action"] = action.asArg()

		when (action) {
			HoverAction.SHOW_TEXT -> {
				this["value"] = value
			}

			HoverAction.SHOW_ITEM -> {
				// Ignore this.value
				contents?.let {
					val contentsTag = it.toNbtTag()
					if (contentsTag is NbtCompound) {
						contentsTag.forEach { (key, value) ->
							this[key] = value
						}
					}
				}
			}

			HoverAction.SHOW_ENTITY -> {
				// Ignore this.value
				contents?.let {
					val contentsTag = it.toNbtTag()
					if (contentsTag is NbtCompound) {
						contentsTag.forEach { (key, value) ->
							this[key] = value
						}
					}
				}
			}
		}
	}
}

fun HoverEvent.showEntity(entity: EntityArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	value = entity.asString().nbt
}

fun HoverEvent.showEntity(id: EntityTypeArgument, name: ChatComponent? = null, uuid: UUIDArgument? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityUUID(id.asString(), name, uuid?.asString())
}

fun HoverEvent.showEntity(id: EntityTypeArgument, name: ChatComponent? = null, uuid: IntArray? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(id.asString(), name, uuid)
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, uuid: UUIDArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityUUID(type.asString(), name?.let(::text), uuid.asString())
}

fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, uuid: IntArray) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(type.asString(), name?.let(::text), uuid)
}

fun HoverEvent.showItem(item: ItemArgument, count: Int) = apply {
	action = HoverAction.SHOW_ITEM
	contents = ContentsItem(item.asId(), count, item.components)
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
