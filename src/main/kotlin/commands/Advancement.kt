package commands

import arguments.Argument
import arguments.literal
import functions.Function
import kotlinx.serialization.Serializable
import serializers.LowercaseSerializer

@Serializable(AdvancementAction.Companion.AdvancementActionSerializer::class)
enum class AdvancementAction {
	GRANT,
	REVOKE;
	
	companion object {
		val values = values()
		
		object AdvancementActionSerializer : LowercaseSerializer<AdvancementAction>(values)
	}
}

@Serializable(AdvancementRoute.Companion.AdvancementRouteSerializer::class)
enum class AdvancementRoute {
	ONLY,
	THROUGH,
	UNTIL;
	
	companion object {
		val values = values()
		
		object AdvancementRouteSerializer : LowercaseSerializer<AdvancementRoute>(values)
	}
}

fun Function.advancement(
	action: AdvancementAction,
	targets: Argument.Selector,
	advancement: Argument.Advancement,
	criterion: String = "",
) = addLine(command("advancement", literal(action.asArg()), targets, literal("only"), advancement, literal(criterion)))

fun Function.advancement(
	action: AdvancementAction,
	targets: Argument.Selector,
	route: AdvancementRoute,
	advancement: Argument.Advancement,
	criterion: String = "",
) = addLine(command("advancement", literal(action.asArg()), targets, literal(route.asArg()), advancement, literal(criterion)))

fun Function.advancement(action: AdvancementAction, targets: Argument.Selector) = addLine(command("advancement", literal(action.asArg()), targets, literal("everything")))
