package arguments

import commands.asArg
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.benwoodworth.knbt.*
import serializers.LowercaseSerializer
import serializers.NbtAsJsonTextComponentSerializer

@Serializable(with = TextComponents.Companion.TextComponentsSerializer::class)
data class TextComponents(val list: MutableList<TextComponent> = mutableListOf()) : Argument {
	constructor(vararg components: TextComponent) : this(components.toMutableList())

	operator fun plus(textComponent: TextComponent): TextComponents {
		list += textComponent
		return this
	}

	operator fun plus(textComponents: TextComponents): TextComponents {
		list += textComponents.list
		return this
	}

	fun toJsonString() = Json.encodeToString(this)

	fun toNbtTag() = when (list.size) {
		0 -> NbtString("")
		1 -> list[0].toNbtTag()
		else -> buildNbtList {
			list.forEach {
				this += Json.encodeToJsonElement(it)
			}
		}
	}

	override fun asString() = StringifiedNbt.encodeToString(toNbtTag())

	fun asJsonArg() = literal(toJsonString())

	companion object {
		object TextComponentsSerializer : KSerializer<TextComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = TextComponents()

			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: TextComponents) {
				if (value.list.size == 1) {
					val component = value.list[0]
					when {
						component.containsOnlyText() -> encoder.encodeString(component.text)
						else -> encoder.encodeSerializableValue(TextComponent.serializer(), component)
					}
				} else {
					encoder.encodeSerializableValue(ListSerializer(TextComponent.serializer()), value.list)
				}
			}
		}
	}
}

@Serializable
data class TextComponent(
	var text: String,
	var color: Color? = null,
	var bold: Boolean? = null,
	var italic: Boolean? = null,
	var underlined: Boolean? = null,
	var strikethrough: Boolean? = null,
	var obfuscated: Boolean? = null,
	var insertion: String? = null,
	var clickEvent: ClickEvent? = null,
	var hoverEvent: HoverEvent? = null,
	var font: String? = null,
) {
	fun toNbtTag() = buildNbtCompound {
		this["text"] = text
		color?.let { this["color"] = it.asArg() }
		bold?.let { this["bold"] = it }
		italic?.let { this["italic"] = it }
		underlined?.let { this["underlined"] = it }
		strikethrough?.let { this["strikethrough"] = it }
		obfuscated?.let { this["obfuscated"] = it }
		insertion?.let { this["insertion"] = it }
		clickEvent?.let { this["clickEvent"] = it }
		hoverEvent?.let { this["hoverEvent"] = it }
		font?.let { this["font"] = it }
	}

	fun containsOnlyText() =
		color == null && bold == null && italic == null && underlined == null && strikethrough == null && obfuscated == null && insertion == null && clickEvent == null && hoverEvent == null && font == null
}

@Serializable
data class ClickEvent(
	var action: ClickAction,
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var value: NbtTag,
)

@Serializable(ClickAction.Companion.ClickActionSerializer::class)
enum class ClickAction {
	OPEN_URL,
	OPEN_FILE,
	RUN_COMMAND,
	SUGGEST_COMMAND,
	CHANGE_PAGE,
	COPY_TO_CLIPBOARD;

	companion object {
		val values = values()

		object ClickActionSerializer : LowercaseSerializer<ClickAction>(values)
	}
}

@Serializable
data class HoverEvent(
	var action: HoverAction,
	@Serializable(with = NbtAsJsonTextComponentSerializer::class) var value: NbtTag,
)

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

fun textComponent(text: String = "", block: TextComponent.() -> Unit = {}) = TextComponents(TextComponent(text).apply(block))
fun TextComponent.hoverEvent(action: HoverAction = HoverAction.SHOW_TEXT, block: HoverEvent.() -> Unit) =
	apply { hoverEvent = HoverEvent(action, "".nbt).apply(block) }

fun TextComponent.clickEvent(action: ClickAction = ClickAction.RUN_COMMAND, block: ClickEvent.() -> Unit) =
	apply { clickEvent = ClickEvent(action, "".nbt).apply(block) }
