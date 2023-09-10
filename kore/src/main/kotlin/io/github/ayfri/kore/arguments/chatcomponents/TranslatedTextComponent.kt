package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.Serializable

@Serializable
data class TranslatedTextComponent(
	var translate: String,
	var with: List<ChatComponent>? = null,
	var fallback: String? = null,
) : ChatComponent(), SimpleComponent {
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
	block: TranslatedTextComponent.() -> Unit = {},
) =
	ChatComponents(TranslatedTextComponent(translate, with, fallback).apply(block))
