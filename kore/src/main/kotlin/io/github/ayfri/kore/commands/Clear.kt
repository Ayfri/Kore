package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.components.ItemPredicate
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.functions.Function

fun Function.clear(targets: EntityArgument? = null, item: ItemPredicate? = null, maxCount: Int? = null) =
	addLine(command("clear", targets, literal(item.toString()), int(maxCount)))

fun Function.clear(targets: EntityArgument, item: ItemArgument, maxCount: Int? = null) =
	addLine(command("clear", targets, item, int(maxCount)))
