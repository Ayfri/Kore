package io.github.ayfri.kore.features.dialogs.control

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import kotlinx.serialization.Serializable

@Serializable
data class NumberRange(
	override var key: String,
	var label: ChatComponents,
	var labelFormat: String? = null,
	var width: Int? = null,
	var start: Float,
	var end: Float,
	var step: Float? = null,
	var initial: Float? = null,
) : DialogControl()

fun ControlContainer.numberRange(
	key: String,
	label: ChatComponents,
	start: Float,
	end: Float,
	initial: Float? = null,
	block: NumberRange.() -> Unit = {}
) {
	val control = NumberRange(key, label, start=start, end=end, initial=initial).apply(block)
	controls += control
}

fun ControlContainer.numberRange(
	key: String,
	label: ChatComponents,
	range : ClosedFloatingPointRange<Float>,
	initial: Number? = null,
	block: NumberRange.() -> Unit = {}
) = numberRange(key, label, range.start, range.endInclusive, initial?.toFloat(), block)

fun ControlContainer.numberRange(
	key: String,
	label: ChatComponents,
	range : IntRange,
	initial: Number? = null,
	block: NumberRange.() -> Unit = {}
) = numberRange(key, label, range.first.toFloat(), range.last.toFloat(), initial?.toFloat(), block)


fun ControlContainer.numberRange(
	key: String,
	label: String,
	range: ClosedFloatingPointRange<Float>,
	initial: Number? = null,
	block: NumberRange.() -> Unit = {}
) = numberRange(key, textComponent(label), range.start, range.endInclusive, initial?.toFloat(), block)

fun ControlContainer.numberRange(
	key: String,
	label: String,
	range: IntRange,
	initial: Number? = null,
	block: NumberRange.() -> Unit = {}
) = numberRange(key, textComponent(label), range.first.toFloat(), range.last.toFloat(), initial?.toFloat(), block)


fun NumberRange.label(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit = {}) = apply {
	label = textComponent(text, color, block)
}
