package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.actions.ClickEvent
import io.github.ayfri.kore.arguments.actions.ClickEventContainer
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverAction
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverEvent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.pascalCase
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.encodeToNbtTag

@OptIn(ExperimentalSerializationApi::class)
@Serializable
abstract class ChatComponent {
	abstract val type: ChatComponentType

	@EncodeDefault(EncodeDefault.Mode.NEVER)
	open var text = ""
	var bold: Boolean? = null

	@SerialName("click_event")
	var clickEvent: ClickEvent? = null
	var color: Color? = null
	var extra: ChatComponents? = null
	var font: String? = null

	@SerialName("hover_event")
	var hoverEvent: HoverEvent? = null
	var insertion: String? = null
	var italic: Boolean? = null
	var obfuscated: Boolean? = null

	@SerialName("shadow_color")
	var shadowColor: Color? = null
	var strikethrough: Boolean? = null
	var underlined: Boolean? = null

	open fun containsOnlyText() = text(text) == this

	open fun toNbtTag() = buildNbtCompound {
		this["type"] = type.name.lowercase()
		bold?.let { this["bold"] = it }
		clickEvent?.let { this["click_event"] = snbtSerializer.encodeToNbtTag(it) }
		color?.let { this["color"] = it.asString() }
		extra?.let { this["extra"] = it }
		font?.let { this["font"] = it }
		hoverEvent?.let { this["hover_event"] = it.toNbtTag() }
		insertion?.let { this["insertion"] = it }
		italic?.let { this["italic"] = it }
		obfuscated?.let { this["obfuscated"] = it }
		shadowColor?.let { this["shadow_color"] = it.asString() }
		strikethrough?.let { this["strikethrough"] = it }
		if (extra == null) this["text"] = text
		underlined?.let { this["underlined"] = it }
	}

	fun toNbt() = if (containsOnlyText()) text.nbt else toNbtTag()

	override fun toString() =
		"${type.name.pascalCase()}(text='$text', bold=$bold, clickEvent=$clickEvent, color=$color, extra=$extra, font=$font, hoverEvent=$hoverEvent, insertion=$insertion, italic=$italic, obfuscated=$obfuscated, shadowColor=$shadowColor, strikethrough=$strikethrough, underlined=$underlined)"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ChatComponent

		if (text != other.text) return false
		if (bold != other.bold) return false
		if (clickEvent != other.clickEvent) return false
		if (color != other.color) return false
		if (extra != other.extra) return false
		if (font != other.font) return false
		if (hoverEvent != other.hoverEvent) return false
		if (insertion != other.insertion) return false
		if (italic != other.italic) return false
		if (obfuscated != other.obfuscated) return false
		if (shadowColor != other.shadowColor) return false
		if (strikethrough != other.strikethrough) return false
		return underlined == other.underlined
	}

	override fun hashCode(): Int {
		var result = text.hashCode()
		result = 31 * result + (bold?.hashCode() ?: 0)
		result = 31 * result + (clickEvent?.hashCode() ?: 0)
		result = 31 * result + (color?.hashCode() ?: 0)
		result = 31 * result + (extra?.hashCode() ?: 0)
		result = 31 * result + (font?.hashCode() ?: 0)
		result = 31 * result + (hoverEvent?.hashCode() ?: 0)
		result = 31 * result + (insertion?.hashCode() ?: 0)
		result = 31 * result + (italic?.hashCode() ?: 0)
		result = 31 * result + (obfuscated?.hashCode() ?: 0)
		result = 31 * result + (shadowColor?.hashCode() ?: 0)
		result = 31 * result + (strikethrough?.hashCode() ?: 0)
		result = 31 * result + (underlined?.hashCode() ?: 0)
		return result
	}
}

fun textComponent(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	ChatComponents(text(text, color, block))

fun ChatComponent.hoverEvent(action: HoverAction = HoverAction.SHOW_TEXT, block: HoverEvent.() -> Unit) =
	apply { hoverEvent = HoverEvent(action, "".nbt).apply(block) }

fun ChatComponent.clickEvent(block: ClickEventContainer.() -> Unit) =
	apply { clickEvent = ClickEventContainer().apply(block).action }

fun ChatComponent.hoverEvent(text: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) =
	apply { hoverEvent = HoverEvent(HoverAction.SHOW_TEXT, textComponent(text, color, block).toNbtTag()) }
