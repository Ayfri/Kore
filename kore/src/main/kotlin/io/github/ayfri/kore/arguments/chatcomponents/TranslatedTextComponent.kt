package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/**
 * A `minecraft:translatable` component that resolves a translation key using the client's language files.
 * [with] provides positional substitutions for `%s` / `%1$s` placeholders; [fallback] is rendered when the key is missing.
 *
 * Docs: [Text component format - Translated text](https://minecraft.wiki/w/Text_component_format#Translated_text)
 */
@Serializable
data class TranslatedTextComponent(
	/** The translation key to look up in the client's active language file (e.g. `"item.minecraft.diamond"`). */
	var translate: String,
	// Each argument is a single component (what every component factory returns); a multi-component arg is flattened to root + extra.
	/** Ordered substitutions for `%s` / `%1$s` placeholders in the translated string. */
	var with: List<ChatComponents>? = null,
	/** Literal string displayed when [translate] has no entry in the active language. */
	var fallback: String? = null,
) : ChatComponent(), SimpleComponent {
	override val type = ChatComponentType.TRANSLATABLE

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		fallback?.let { this["fallback"] = it }
		this["translate"] = translate
		with?.let { args ->
			this["with"] = nbtListOf(args.map { it.toComponent().toNbtTag() })
		}
	}
}

/** Creates a [TranslatedTextComponent] for the given translation [translate] key, with optional [with] substitutions and [fallback]. */
fun translatedTextComponent(
	translate: String,
	with: List<ChatComponents>? = null,
	fallback: String? = null,
	block: TranslatedTextComponent.() -> Unit = {},
) =
	ChatComponents(TranslatedTextComponent(translate, with, fallback).apply(block))

/** Creates a [TranslatedTextComponent] where each [with] string is wrapped in a [PlainTextComponent]. */
@JvmName("translatedTextComponentString")
fun translatedTextComponent(
	translate: String,
	with: List<String>,
	fallback: String? = null,
	block: TranslatedTextComponent.() -> Unit = {},
) =
	translatedTextComponent(translate, with.map { textComponent(it) }, fallback, block)
