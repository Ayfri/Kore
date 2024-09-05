package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound

@Serializable
data class KeybindComponent(
	var keybind: String,
) : ChatComponent() {
	override val type = ChatComponentType.KEYBIND

	override fun toNbtTag() = buildNbtCompound {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["keybind"] = keybind.toString()
	}
}

fun keybindComponent(
	keybind: String,
	block: KeybindComponent.() -> Unit = {},
) =
	ChatComponents(KeybindComponent(keybind).apply(block))
