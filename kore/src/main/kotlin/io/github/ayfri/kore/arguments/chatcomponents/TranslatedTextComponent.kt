package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.add
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.buildNbtList

@Serializable
data class TranslatedTextComponent(
	var translate: String,
	var with: List<ChatComponent>? = null,
	var fallback: String? = null,
) : ChatComponent(), SimpleComponent {
	override val type = ChatComponentType.TRANSLATABLE

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["translate"] = translate
		with?.let {
			this["with"] = buildNbtList {
				it.forEach {
					add(it.toNbtTag())
				}
			}
		}
		fallback?.let { this["fallback"] = it }
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
