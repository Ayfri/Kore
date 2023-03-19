package commands

import arguments.*
import arguments.numbers.TimeNumber
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(TitleAction.Companion.TitleActionSerializer::class)
enum class TitleAction {
	CLEAR,
	RESET;

	companion object {
		val values = values()

		object TitleActionSerializer : LowercaseSerializer<TitleAction>(values)
	}
}

@Serializable(TitleLocation.Companion.TitleLocationSerializer::class)
enum class TitleLocation {
	TITLE,
	SUBTITLE,
	ACTIONBAR;

	companion object {
		val values = values()

		object TitleLocationSerializer : LowercaseSerializer<TitleLocation>(values)
	}
}

fun Function.title(targets: Argument.Entity, action: TitleAction) = addLine(command("title", targets, literal(action.asArg())))
fun Function.title(targets: Argument.Entity, location: TitleLocation, message: ChatComponents) =
	addLine(command("title", targets, literal(location.asArg()), message.asJsonArg()))

fun Function.title(targets: Argument.Entity, fadeIn: Double, stay: Double, fadeOut: Double) =
	addLine(command("title", targets, literal("times"), float(fadeIn), float(stay), float(fadeOut)))

fun Function.title(targets: Argument.Entity, fadeIn: TimeNumber, stay: TimeNumber, fadeOut: TimeNumber) =
	addLine(command("title", targets, literal("times"), fadeIn.asArg(), stay.asArg(), fadeOut.asArg()))
