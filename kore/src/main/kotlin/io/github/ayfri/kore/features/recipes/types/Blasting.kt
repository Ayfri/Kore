package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.arguments.types.resources.item
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.data.item.builders.itemStack
import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import io.github.ayfri.kore.features.recipes.data.Ingredient
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class Blasting(
	override var ingredient: InlinableList<Ingredient> = emptyList(),
	override var result: ItemStack,
	override var group: String? = null,
	override var experience: Double? = null,
	override var cookingTime: Int? = null,
) : Recipe(), CookingRecipe {
	override val type = RecipeTypes.BLASTING
}

fun Recipes.blasting(name: String, block: Blasting.() -> Unit): RecipeArgument {
	val recipe = RecipeFile(name, Blasting(result = itemStack(item(""))).apply(block))
	dp.recipes += recipe
	return RecipeArgument(name, recipe.namespace ?: dp.name)
}
