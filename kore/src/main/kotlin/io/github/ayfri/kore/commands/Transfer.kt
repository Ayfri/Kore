package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function

fun Function.transfer(hostname: String, port: Int? = null, players: EntityArgument? = null) =
	addLine(command("transfer", literal(hostname), int(port), players))
