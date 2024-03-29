package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
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
	components["recipes"] = RecipesComponent(recipes)
}

fun Components.recipes(vararg recipes: RecipeArgument) = apply {
	components["recipes"] = RecipesComponent(recipes.toList())
}

fun Components.recipes(block: RecipesComponent.() -> Unit) = apply {
	components["recipes"] = RecipesComponent(mutableListOf()).apply(block)
}

fun RecipesComponent.recipe(recipe: RecipeArgument) = apply {
	recipes += recipe
}
