package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.arguments.actions.ClickEvent
import io.github.ayfri.kore.arguments.actions.ClickEventContainer
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverAction
import io.github.ayfri.kore.arguments.chatcomponents.hover.HoverEvent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.pascalCase
import io.github.ayfri.kore.utils.set
import io.github.ayfri.kore.utils.snbtSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.encodeToNbtTag

/**
 * Base class for all Minecraft text (chat) components. Holds the shared styling fields (bold, italic, color, events, etc.)
 * that every concrete component type inherits.
 *
 * Docs: [Text component format](https://minecraft.wiki/w/Text_component_format)
 */
@GeneratedSealedSerializer
@Serializable
sealed class ChatComponent {
	/** Discriminator written as the `type` key in the serialized form. */
	abstract val type: ChatComponentType

	/** Raw text content. Only meaningful on [PlainTextComponent]; omitted from serialization when empty. */
	@EncodeDefault(EncodeDefault.Mode.NEVER)
	open var text = ""

	/** Renders the text in bold when `true`, removes bold when `false`, inherits when `null`. */
	var bold: Boolean? = null

	/** Action executed when the player clicks on this component. */
	@SerialName("click_event")
	var clickEvent: ClickEvent? = null

	/** Color of the text. Accepts named colors or hex values via [Color]. */
	var color: Color? = null

	/** Additional sibling components rendered after this one. They inherit but can override the parent's styling. */
	var extra: ChatComponents? = null

	/** Resource location of the font to use (e.g. `"minecraft:default"`). */
	var font: String? = null

	/** Event shown in a tooltip when the player hovers over this component. */
	@SerialName("hover_event")
	var hoverEvent: HoverEvent? = null

	/** Text inserted into the chat input box when the player shift-clicks this component. */
	var insertion: String? = null

	/** Renders the text in italics when `true`, removes italics when `false`, inherits when `null`. */
	var italic: Boolean? = null

	/** Replaces each character with a random character that changes every tick when `true`. */
	var obfuscated: Boolean? = null

	/** ARGB color of the drop shadow cast by this text. */
	@SerialName("shadow_color")
	var shadowColor: Color? = null

	/** Renders a horizontal line through the text when `true`. */
	var strikethrough: Boolean? = null

	/** Renders the text with an underline when `true`. */
	var underlined: Boolean? = null

	/** Returns `true` if this component is equivalent to a bare [PlainTextComponent] with only its [text] set. */
	open fun containsOnlyText() = text(text) == this

	/** Serializes this component to an NBT compound, including all non-null styling fields. */
	open fun toNbtTag() = nbt {
		this["type"] = type.name.lowercase()
		bold?.let { this["bold"] = it }
		clickEvent?.let { this["click_event"] = snbtSerializer.encodeToNbtTag(it) }
		color?.let { this["color"] = it.asString() }
		extra?.let { this["extra"] = it.toNbtTag() }
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

	/** Returns a compact NBT representation: a bare [NbtString][net.benwoodworth.knbt.NbtString] when [containsOnlyText], otherwise [toNbtTag]. */
	fun toNbt() = if (containsOnlyText()) text.nbt else toNbtTag()

	override fun toString() =
		"${type.name.pascalCase()}(text='$text', bold=$bold, clickEvent=$clickEvent, color=$color, extra=$extra, font=$font, hoverEvent=$hoverEvent, insertion=$insertion, italic=$italic, obfuscated=$obfuscated, shadowColor=$shadowColor, strikethrough=$strikethrough, underlined=$underlined)"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (this::class != other?.let { it::class }) return false

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

/** Creates a [ChatComponents] wrapping a single [PlainTextComponent] with the given [text] and optional [color]. */
fun textComponent(text: String = "", color: Color? = null, block: PlainTextComponent.() -> Unit = {}) =
	ChatComponents(text(text, color, block))

/** Sets a [HoverEvent] on this component using a structured [block]. */
fun ChatComponent.hoverEvent(action: HoverAction = HoverAction.SHOW_TEXT, block: HoverEvent.() -> Unit) =
	apply { hoverEvent = HoverEvent(action, "".nbt).apply(block) }

/** Sets a [ClickEvent] on this component using a structured [block]. */
fun ChatComponent.clickEvent(block: ClickEventContainer.() -> Unit) =
	apply { clickEvent = ClickEventContainer().apply(block).action }

/** Sets a `show_text` [HoverEvent] on this component displaying a plain text tooltip. */
fun ChatComponent.hoverEvent(text: String, color: Color? = null, block: ChatComponent.() -> Unit = {}) =
	apply { hoverEvent = HoverEvent(HoverAction.SHOW_TEXT, textComponent(text, color, block).toNbtTag()) }
