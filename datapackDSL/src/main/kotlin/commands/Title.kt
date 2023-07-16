package commands

import arguments.chatcomponents.ChatComponents
import arguments.numbers.TimeNumber
import arguments.types.EntityArgument
import arguments.types.literals.float
import arguments.types.literals.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer
import utils.asArg

@Serializable(TitleAction.Companion.TitleActionSerializer::class)
enum class TitleAction {
	CLEAR,
	RESET;

	companion object {
		data object TitleActionSerializer : LowercaseSerializer<TitleAction>(entries)
	}
}

@Serializable(TitleLocation.Companion.TitleLocationSerializer::class)
enum class TitleLocation {
	TITLE,
	SUBTITLE,
	ACTIONBAR;

	companion object {
		data object TitleLocationSerializer : LowercaseSerializer<TitleLocation>(entries)
	}
}

fun Function.title(targets: EntityArgument, action: TitleAction) = addLine(command("title", targets, literal(action.asArg())))
fun Function.title(targets: EntityArgument, location: TitleLocation, message: ChatComponents) =
	addLine(command("title", targets, literal(location.asArg()), message.asJsonArg()))

fun Function.title(targets: EntityArgument, fadeIn: Double, stay: Double, fadeOut: Double) =
	addLine(command("title", targets, literal("times"), float(fadeIn), float(stay), float(fadeOut)))

fun Function.title(targets: EntityArgument, fadeIn: TimeNumber, stay: TimeNumber, fadeOut: TimeNumber) =
	addLine(command("title", targets, literal("times"), fadeIn.asArg(), stay.asArg(), fadeOut.asArg()))
