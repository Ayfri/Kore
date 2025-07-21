package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
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

fun ComponentsScope.recipes(recipes: List<RecipeArgument>) = apply {
	this[ItemComponentTypes.RECIPES] = RecipesComponent(recipes)
}

fun ComponentsScope.recipes(vararg recipes: RecipeArgument) = apply {
	this[ItemComponentTypes.RECIPES] = RecipesComponent(recipes.toList())
}

fun ComponentsScope.recipes(block: RecipesComponent.() -> Unit) = apply {
	this[ItemComponentTypes.RECIPES] = RecipesComponent(mutableListOf()).apply(block)
}

fun RecipesComponent.recipe(recipe: RecipeArgument) = apply {
	recipes += recipe
}
