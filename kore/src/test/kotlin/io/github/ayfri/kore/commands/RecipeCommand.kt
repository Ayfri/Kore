package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Recipes
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument

fun Function.recipeTests() {
	val recipe = Recipes.STONE
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
