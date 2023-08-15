package commands

import arguments.types.literals.self
import arguments.types.resources.RecipeArgument
import functions.Function
import utils.assertsIs

fun Function.recipeTests() {
	val recipe = RecipeArgument("stone")
	recipeGive(self(), recipe) assertsIs "recipe give @s minecraft:stone"
	recipeGiveAll(self()) assertsIs "recipe give @s *"
	recipeTake(self(), recipe) assertsIs "recipe take @s minecraft:stone"
	recipeTakeAll(self()) assertsIs "recipe take @s *"

	recipe(self()) {
		give(recipe) assertsIs "recipe give @s minecraft:stone"
		giveAll() assertsIs "recipe give @s *"
		take(recipe) assertsIs "recipe take @s minecraft:stone"
		takeAll() assertsIs "recipe take @s *"
	}
}
