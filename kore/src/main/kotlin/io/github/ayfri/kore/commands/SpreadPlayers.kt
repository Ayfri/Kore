package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.functions.Function

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
