package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.float
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

/**
 * Spreads [targets] across a square region around [center]. When [respectTeams] is `true`, team
 * members stay together.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/spreadplayers)
 */
fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(command("spreadplayers", center, float(spreadDistance), float(maxRange), bool(respectTeams), targets))

/**
 * Spreads [targets] across a square region around [center] while keeping the search below
 * [maxHeight]. When [respectTeams] is `true`, team members stay together.
 *
 * @see [Minecraft wiki](https://minecraft.wiki/w/Commands/spreadplayers)
 */
fun Function.spreadPlayers(
	center: Vec2,
	spreadDistance: Double,
	maxRange: Double,
	maxHeight: Int,
	respectTeams: Boolean,
	targets: EntityArgument,
) = addLine(
	command(
		"spreadplayers",
		center,
		float(spreadDistance),
		float(maxRange),
		literal("under"),
		int(maxHeight),
		bool(respectTeams),
		targets
	)
)
