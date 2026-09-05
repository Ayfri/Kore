package io.github.ayfri.kore.arguments.chatcomponents

import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.Serializable

/**
 * A `minecraft:keybind` component that renders the key currently bound to a given action (e.g. `"key.jump"`).
 *
 * Docs: [Text component format - Keybind](https://minecraft.wiki/w/Text_component_format#Keybind)
 */
@Serializable
data class KeybindComponent(
	/** The keybind identifier as defined in the controls menu (e.g. `"key.jump"`, `"key.attack"`). */
	var keybind: String,
) : ChatComponent() {
	override val type = ChatComponentType.KEYBIND

	override fun toNbtTag() = nbt {
		super.toNbtTag().entries.forEach { (key, value) -> if (key != "text") this[key] = value }
		this["keybind"] = keybind
	}
}

/** Creates a [KeybindComponent] displaying the key bound to [keybind]. */
fun keybindComponent(
	keybind: String,
	block: KeybindComponent.() -> Unit = {},
) =
	ChatComponents(KeybindComponent(keybind).apply(block))
