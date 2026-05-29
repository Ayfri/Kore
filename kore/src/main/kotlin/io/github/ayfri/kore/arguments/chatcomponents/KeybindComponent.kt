package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

@Serializable
data class KeybindComponent(
	var keybind: String,
) : ChatComponent() {
	override val type = ChatComponentType.KEYBIND

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["keybind"] = keybind
	}
}

fun keybindComponent(
	keybind: String,
	block: KeybindComponent.() -> Unit = {},
) =
	ChatComponents(KeybindComponent(keybind).apply(block))
