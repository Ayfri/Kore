package features.itemmodifiers.functions

import arguments.ChatComponents
import arguments.Color
import arguments.chatcomponents.ChatComponent
import arguments.chatcomponents.PlainTextComponent
import arguments.chatcomponents.text
import arguments.chatcomponents.textComponent
import features.itemmodifiers.ItemModifierEntry
import kotlinx.serialization.Serializable

@Serializable
data class SetLore(
	var entity: Source? = null,
	var lore: ChatComponents = textComponent(),
	var replace: Boolean? = null,
) : ItemFunctionSurrogate

fun ItemModifierEntry.setLore(entity: Source? = null, replace: Boolean? = null, vararg lore: String) {
	function = SetLore(entity, ChatComponents(lore.map { text(it) }.toMutableList()), replace)
}

fun ItemModifierEntry.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	text: String,
	color: Color? = null,
	block: PlainTextComponent.() -> Unit = {}
) {
	function = SetLore(entity, textComponent(text) {
		this.color = color
		block()
	}, replace)
}

fun ItemModifierEntry.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	components: ChatComponents,
) {
	function = SetLore(entity, components, replace)
}

fun ItemModifierEntry.setLore(
	entity: Source? = null,
	replace: Boolean? = null,
	component: ChatComponent,
) {
	function = SetLore(entity, ChatComponents(component), replace)
}
