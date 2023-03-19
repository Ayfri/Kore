package arguments.chatcomponents

import arguments.ChatComponents
import arguments.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class TranslatedTextComponent(
	var translate: String,
	var with: List<ChatComponent>? = null,
	var fallback: String? = null,
) : TextComponent() {
	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["translate"] = translate
		with?.let { this["with"] = it }
		fallback?.let { this["fallback"] = it }
	}
}

fun translatedText(
	translate: String,
	with: List<ChatComponent>? = null,
	fallback: String? = null,
	block: TranslatedTextComponent.() -> Unit = {}
) =
	ChatComponents(TranslatedTextComponent(translate, with, fallback).apply(block))
