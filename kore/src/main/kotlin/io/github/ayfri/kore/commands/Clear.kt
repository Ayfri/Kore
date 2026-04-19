package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.components.ItemPredicate
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.functions.Function

/** Clears matching items from [targets], optionally filtering by [item] and [maxCount]. */
fun Function.clear(targets: EntityArgument? = null, item: ItemArgument? = null, maxCount: Int? = null) =
	addLine(command("clear", targets, item, int(maxCount)))

/** Clears items matching [predicate] from [targets], optionally limited by [maxCount]. */
fun Function.clear(targets: EntityArgument, predicate: ItemPredicate, maxCount: Int? = null) =
	addLine(command("clear", targets, literal(predicate.toString()), int(maxCount)))
