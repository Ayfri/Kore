package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(with = RecipesComponent.Companion.RecipesComponentSerializer::class)
data class RecipesComponent(var recipes: List<RecipeArgument>) : Component() {
	companion object {
		object RecipesComponentSerializer : InlineSerializer<RecipesComponent, List<RecipeArgument>>(
			ListSerializer(RecipeArgument.serializer()),
			RecipesComponent::recipes
		)
	}
}

fun Components.recipes(recipes: List<RecipeArgument>) = apply {
	this[ComponentTypes.RECIPES] = RecipesComponent(recipes)
}

fun Components.recipes(vararg recipes: RecipeArgument) = apply {
	this[ComponentTypes.RECIPES] = RecipesComponent(recipes.toList())
}

fun Components.recipes(block: RecipesComponent.() -> Unit) = apply {
	this[ComponentTypes.RECIPES] = RecipesComponent(mutableListOf()).apply(block)
}

fun RecipesComponent.recipe(recipe: RecipeArgument) = apply {
	recipes += recipe
}
