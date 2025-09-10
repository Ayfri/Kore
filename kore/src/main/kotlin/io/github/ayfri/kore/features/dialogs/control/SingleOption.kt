package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.Serializable

data class SingleOption(
	override var key: String,
	var label: ChatComponents,
	var labelVisible: Boolean? = null,
	var width: Int? = null,
	var options: List<DialogSelectOption>,
) : DialogControl()

@Serializable(with = DialogSelectOption.Companion.DialogSelectOptionSerializer::class)
data class DialogSelectOption(
	var id: String,
	var display: ChatComponents? = null,
	var initial: Boolean? = null,
) {
	companion object {
		data object DialogSelectOptionSerializer : SinglePropertySimplifierSerializer<DialogSelectOption, String>(
			DialogSelectOption::class,
			DialogSelectOption::id,
		)
	}
}

fun ControlContainer.singleOption(
	key: String,
	label: ChatComponents,
	options: List<DialogSelectOption> = emptyList(),
	width: Int? = null,
	block: SingleOption.() -> Unit = {},
) {
	val control = SingleOption(key, label, options = options, width = width).apply(block)
	controls += control
}

fun SingleOption.label(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	label = textComponent(text, color, block)
}

fun SingleOption.option(id: String, display: ChatComponents? = null, initial: Boolean? = null) = apply {
	options += DialogSelectOption(id, display, initial)
}

fun SingleOption.option(id: String, display: String, initial: Boolean? = null) = option(id, textComponent(display), initial)
