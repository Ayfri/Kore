package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.literals.all
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument

/** DSL scope for the `recipe` command bound to [target]. */
class Recipe(private val fn: Function, val target: EntityArgument) {
	/** Gives [recipe] to the bound target. */
	fun give(recipe: RecipeArgument) = fn.addLine(command("recipe", literal("give"), target, recipe))

	/** Gives every recipe to the bound target. */
	fun giveAll() = fn.addLine(command("recipe", literal("give"), target, all()))

	/** Takes [recipe] away from the bound target. */
	fun take(recipe: RecipeArgument) = fn.addLine(command("recipe", literal("take"), target, recipe))

	/** Takes every recipe away from the bound target. */
	fun takeAll() = fn.addLine(command("recipe", literal("take"), target, all()))
}

/** Gives [recipe] to [target]. */
fun Function.recipeGive(target: EntityArgument, recipe: RecipeArgument) = addLine(command("recipe", literal("give"), target, recipe))

/** Gives every recipe to [target]. */
fun Function.recipeGiveAll(target: EntityArgument) = addLine(command("recipe", literal("give"), target, all()))

/** Takes [recipe] away from [target]. */
fun Function.recipeTake(target: EntityArgument, recipe: RecipeArgument) = addLine(command("recipe", literal("take"), target, recipe))

/** Takes every recipe away from [target]. */
fun Function.recipeTakeAll(target: EntityArgument) = addLine(command("recipe", literal("take"), target, all()))

/** Opens the [Recipe] DSL for [target]. */
fun Function.recipe(target: EntityArgument, block: Recipe.() -> Command) = Recipe(this, target).block()

/** Returns the [Recipe] DSL for [target]. */
fun Function.recipe(target: EntityArgument) = Recipe(this, target)
