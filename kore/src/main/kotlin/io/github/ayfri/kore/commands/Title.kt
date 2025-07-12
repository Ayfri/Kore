package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.serializers.LowercaseSerializer
import io.github.ayfri.kore.utils.asArg
import kotlinx.serialization.Serializable

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
	addLine(command("title", targets, literal(location.asArg()), message.asSnbtArg()))

fun Function.title(targets: EntityArgument, fadeIn: Double, stay: Double, fadeOut: Double) =
	addLine(command("title", targets, literal("times"), float(fadeIn), float(stay), float(fadeOut)))

fun Function.title(targets: EntityArgument, fadeIn: TimeNumber, stay: TimeNumber, fadeOut: TimeNumber) =
	addLine(command("title", targets, literal("times"), fadeIn.asArg(), stay.asArg(), fadeOut.asArg()))
