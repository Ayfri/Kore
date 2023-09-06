package commands

import arguments.maths.Vec2
import arguments.types.EntityArgument
import arguments.types.literals.bool
import arguments.types.literals.float
import arguments.types.literals.int
import functions.Function

fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), bool(respectTeams), targets))

fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	maxHeight: Int,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), int(maxHeight), bool(respectTeams), targets))
