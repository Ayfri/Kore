package io.github.ayfri.kore.website.components.doc

import androidx.compose.runtime.*
import io.github.ayfri.kore.website.components.common.HighlightedCode
import org.jetbrains.compose.web.dom.*
import kotlin.math.round

private const val COMMAND_ID = "time-converter-command"
private const val TICKS_PER_SECOND = 20
private const val TICKS_PER_DAY = 24_000

private enum class TimeType(val label: String, val suffix: String, val ticks: Int) {
	TICKS("ticks", "", 1),
	SECONDS("seconds", "s", TICKS_PER_SECOND),
	DAYS("days", "d", TICKS_PER_DAY),
}

/** Four decimals at most. On JS a round Double prints as `5`, the text Kore's `toStringTruncatedIfRound` writes. */
private fun Double.rounded() = (round(this * 10_000) / 10_000).toString()

/** Converts a duration between ticks, seconds and days like Kore's `TimeNumber`, and shows how a command prints it. */
@Composable
fun TimeConverter() {
	var value by remember { mutableStateOf(5.0) }
	var type by remember { mutableStateOf(TimeType.SECONDS) }
	val ticks = value * type.ticks
	val command = "schedule function my_pack:explode $value${type.suffix} replace"

	Div({ classes(DocControlsStyle.widget) }) {
		Div({ classes(DocControlsStyle.panel, DocControlsStyle.stack) }) {
			SliderField("time-converter-value", "value", value, 0.0, 100.0, 0.05) { value = it }
			PillGroup("Unit", TimeType.entries, type, TimeType::label) { type = it }
		}

		Div({ classes(DocControlsStyle.panel, DocControlsStyle.grid) }) {
			Readout("Kotlin", "$value.${type.label}")
			Readout("In ticks", ticks.rounded())
			Readout("In seconds", (ticks / TICKS_PER_SECOND).rounded())
			Readout("In days", (ticks / TICKS_PER_DAY).rounded())
			Readout("wholeTicks()", ticks.toInt().toString())
		}

		HighlightedCode(COMMAND_ID, command, "mcfunction")
	}
}

@Composable
private fun Readout(label: String, text: String) {
	ControlField(label) { Span({ classes(DocControlsStyle.readout) }) { Text(text) } }
}
