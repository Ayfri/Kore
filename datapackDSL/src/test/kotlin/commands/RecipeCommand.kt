package commands

import arguments.Argument
import arguments.self
import functions.Function
import utils.assertsIs

fun Function.recipeTests() {
	val recipe = Argument.Recipe("stone")
	recipeGive(self(), recipe) assertsIs "recipe give @s minecraft:stone"
	recipeGiveAll(self()) assertsIs "recipe give @s *"
	recipeTake(self(), recipe) assertsIs "recipe take @s minecraft:stone"
	recipeTakeAll(self()) assertsIs "recipe take @s *"
}
