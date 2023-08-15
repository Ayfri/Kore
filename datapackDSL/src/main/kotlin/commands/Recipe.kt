package commands

import arguments.types.EntityArgument
import arguments.types.literals.all
import arguments.types.literals.literal
import arguments.types.resources.RecipeArgument
import functions.Function

class Recipe(private val fn: Function, val target: EntityArgument) {
	fun give(recipe: RecipeArgument) = fn.addLine(command("recipe", literal("give"), target, recipe))
	fun giveAll() = fn.addLine(command("recipe", literal("give"), target, all()))
	fun take(recipe: RecipeArgument) = fn.addLine(command("recipe", literal("take"), target, recipe))
	fun takeAll() = fn.addLine(command("recipe", literal("take"), target, all()))
}

fun Function.recipeGive(target: EntityArgument, recipe: RecipeArgument) = addLine(command("recipe", literal("give"), target, recipe))
fun Function.recipeGiveAll(target: EntityArgument) = addLine(command("recipe", literal("give"), target, all()))
fun Function.recipeTake(target: EntityArgument, recipe: RecipeArgument) = addLine(command("recipe", literal("take"), target, recipe))
fun Function.recipeTakeAll(target: EntityArgument) = addLine(command("recipe", literal("take"), target, all()))

fun Function.recipe(target: EntityArgument, block: Recipe.() -> Command) = Recipe(this, target).block()
fun Function.recipe(target: EntityArgument) = Recipe(this, target)
