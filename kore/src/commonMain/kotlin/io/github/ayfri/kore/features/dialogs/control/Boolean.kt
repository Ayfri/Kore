package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import kotlinx.serialization.Serializable

@Serializable
data class Boolean(
	override var key: String,
	var label: ChatComponents,
	var initial: Boolean? = null,
	var onTrue: String? = null,
	var onFalse: String? = null,
) : DialogControl()

fun ControlContainer.boolean(
	key: String,
	label: ChatComponents,
	initial: Boolean? = null,
	onTrue: String? = null,
	onFalse: String? = null,
	block: Boolean.() -> Unit = {},
) {
	val control = Boolean(key, label, initial = initial, onTrue = onTrue, onFalse = onFalse).apply(block)
	controls += control
}

fun ControlContainer.boolean(
	key: String,
	label: String,
	initial: Boolean? = null,
	onTrue: String? = null,
	onFalse: String? = null,
	block: Boolean.() -> Unit = {},
) = boolean(key, textComponent(label), initial, onTrue, onFalse, block)
