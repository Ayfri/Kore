package arguments

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import serializers.LowercaseSerializer

@Serializable(with = TextComponents.Companion.TextComponentsSerializer::class)
data class TextComponents(val list: MutableList<TextComponent> = mutableListOf()) {
	constructor(vararg components: TextComponent) : this(components.toMutableList())
	
	operator fun plus(textComponent: TextComponent): TextComponents {
		list += textComponent
		return this
	}
	
	operator fun plus(textComponents: TextComponents): TextComponents {
		list += textComponents.list
		return this
	}
	
	companion object {
		object TextComponentsSerializer : KSerializer<TextComponents> {
			override val descriptor = ListSerializer(JsonElement.serializer()).descriptor
			
			override fun deserialize(decoder: Decoder) = TextComponents()
			
			/* Encode each component, if there's only one, encode it as a single component, if the component only contains a text, encode it as a string. */
			override fun serialize(encoder: Encoder, value: TextComponents) {
				if (value.list.size == 1) {
					val component = value.list[0]
					if (component.containsOnlyText()) {
						encoder.encodeString(component.text)
					} else {
						encoder.encodeSerializableValue(TextComponent.serializer(), component)
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
	fun containsOnlyText() = color == null
		&& bold == null
		&& italic == null
		&& underlined == null
		&& strikethrough == null
		&& obfuscated == null
		&& insertion == null
		&& clickEvent == null
		&& hoverEvent == null
		&& font == null
}

@Serializable
data class ClickEvent(
	var action: ClickAction,
	var value: String,
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
	var value: String,
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
fun TextComponent.hoverEvent(block: HoverEvent.() -> Unit) = HoverEvent(HoverAction.SHOW_TEXT, "").apply(block)
fun TextComponent.clickEvent(block: ClickEvent.() -> Unit) = ClickEvent(ClickAction.RUN_COMMAND, "").apply(block)
fun textComponent(textComponents: TextComponents) = literal(Json.encodeToString(textComponents))

@JvmName("textComponentNullable")
internal fun textComponent(textComponent: TextComponents?) = literal(Json.encodeToString(textComponent))
