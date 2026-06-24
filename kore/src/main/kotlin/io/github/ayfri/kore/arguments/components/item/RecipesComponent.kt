package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:recipes` item component, which unlocks specified recipes when this knowledge book is used.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#recipes
 */
@Serializable(with = RecipesComponent.Companion.RecipesComponentSerializer::class)
data class RecipesComponent(var recipes: List<RecipeArgument>) : Component() {
	companion object {
		data object RecipesComponentSerializer : InlineAutoSerializer<RecipesComponent>(RecipesComponent::class)
	}
}

/** Unlocks specified recipes when this knowledge book is used. */
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
