package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.nbtListOf
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

@Serializable
data class TranslatedTextComponent(
	var translate: String,
	var with: List<ChatComponent>? = null,
	var fallback: String? = null,
) : ChatComponent(), SimpleComponent {
	override val type = ChatComponentType.TRANSLATABLE

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		fallback?.let { this["fallback"] = it }
		this["translate"] = translate
		with?.let {
			this["with"] = nbtListOf(it.map { it.toNbtTag() })
		}
	}
}

fun translatedTextComponent(
	translate: String,
	with: List<ChatComponent>? = null,
	fallback: String? = null,
	block: TranslatedTextComponent.() -> Unit = {},
) =
	ChatComponents(TranslatedTextComponent(translate, with, fallback).apply(block))

@JvmName("translatedTextComponentString")
fun translatedTextComponent(
	translate: String,
	with: List<String>,
	fallback: String? = null,
	block: TranslatedTextComponent.() -> Unit = {},
) =
	translatedTextComponent(translate, with.map(::text), fallback, block)
