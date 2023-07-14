package features.itemmodifiers.functions

import arguments.ChatComponents
import arguments.Color
import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.PlainTextComponent
import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import features.itemmodifiers.ItemModifier
import kotlinx.serialization.Serializable

@Serializable
data class SetLore(
	var entity: Source? = null,
	var lore: ChatComponents = textComponent(),
	var replace: Boolean? = null,
) : ItemFunction()

fun ItemModifier.setLore(entity: Source? = null, replace: Boolean? = null, vararg lore: String, block: SetLore.() -> Unit = {}) {
	modifiers += SetLore(entity, ChatComponents(lore.map { text(it) }.toMutableList()), replace).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	text: String,
	color: Color? = null,
	block: PlainTextComponent.() -> Unit = {}
) = SetLore(entity, textComponent(text) {
	this.color = color
	block()
}, replace).also { modifiers += it }

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	components: ChatComponents,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity, components, replace).apply(block)
}

fun ItemModifier.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	component: ChatComponent,
	block: SetLore.() -> Unit = {},
) {
	modifiers += SetLore(entity, ChatComponents(component), replace).apply(block)
}
