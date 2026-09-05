package io.github.ayfri.kore.arguments.chatcomponents.hover

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponent
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
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

/**
 * A `hover_event` attached to a text component that shows a tooltip when the player hovers over it.
 * The [action] determines how [value] or [contents] is interpreted and rendered.
 *
 * Docs: [Text component format - Hover event](https://minecraft.wiki/w/Text_component_format#Hover_event)
 */
@Serializable
data class HoverEvent(
	/** Which tooltip type to render: text, item, or entity. */
	var action: HoverAction,
	/** Raw NBT payload; used directly for [HoverAction.SHOW_TEXT] as a serialized component. */
	@Serializable(with = NbtAsJsonSerializer::class)
	var value: NbtTag,
	/** Structured payload used for [HoverAction.SHOW_ITEM] and [HoverAction.SHOW_ENTITY] instead of [value]. */
	var contents: Contents? = null,
) {
	fun toNbtTag() = nbt {
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

/** Sets action to [HoverAction.SHOW_ENTITY] using the selector string of [entity]. */
fun HoverEvent.showEntity(entity: EntityArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	value = entity.asString().nbt
}

/** Sets action to [HoverAction.SHOW_ENTITY] with a structured [ContentsEntityUUID] payload. */
fun HoverEvent.showEntity(id: EntityTypeArgument, name: ChatComponent? = null, uuid: UUIDArgument? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityUUID(id.asString(), name, uuid?.asString())
}

/** Sets action to [HoverAction.SHOW_ENTITY] with a structured [ContentsEntityIntArray] payload. */
fun HoverEvent.showEntity(id: EntityTypeArgument, name: ChatComponent? = null, uuid: IntArray? = null) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(id.asString(), name, uuid)
}

/** Sets action to [HoverAction.SHOW_ENTITY] with a [ContentsEntityUUID] payload using a plain [name] string. */
fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, uuid: UUIDArgument) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityUUID(type.asString(), name?.let(::text), uuid.asString())
}

/** Sets action to [HoverAction.SHOW_ENTITY] with a [ContentsEntityIntArray] payload using a plain [name] string. */
fun HoverEvent.showEntity(type: EntityTypeArgument, name: String? = null, uuid: IntArray) = apply {
	action = HoverAction.SHOW_ENTITY
	contents = ContentsEntityIntArray(type.asString(), name?.let(::text), uuid)
}

/** Sets action to [HoverAction.SHOW_ITEM] displaying [item] with a specific stack [count]. */
fun HoverEvent.showItem(item: ItemArgument, count: Int) = apply {
	action = HoverAction.SHOW_ITEM
	contents = ContentsItem(item.asId(), count, item.components)
}

/** Sets action to [HoverAction.SHOW_TEXT] using a [ChatComponents] builder. */
fun HoverEvent.showText(block: ChatComponents.() -> Unit) = apply {
	action = HoverAction.SHOW_TEXT
	value = ChatComponents().apply(block).toNbtTag()
}

/** Sets action to [HoverAction.SHOW_TEXT] displaying a plain text tooltip. */
fun HoverEvent.showText(text: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) = apply {
	action = HoverAction.SHOW_TEXT
	value = textComponent(text, color, block).toNbtTag()
}

/** The three tooltip types a hover event can render. */
@Serializable(HoverAction.Companion.HoverActionSerializer::class)
enum class HoverAction {
	/** Renders a text component tooltip. */
	SHOW_TEXT,

	/** Renders an item tooltip including name, lore, and enchantments. */
	SHOW_ITEM,

	/** Renders an entity card with type, name, and UUID. */
	SHOW_ENTITY;

	companion object {
		data object HoverActionSerializer : LowercaseSerializer<HoverAction>(entries)
	}
}
