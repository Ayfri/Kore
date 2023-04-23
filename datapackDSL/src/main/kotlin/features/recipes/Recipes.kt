package features.recipes

import DataPack
import arguments.Argument
import arguments.item
import features.recipes.data.CraftingResult
import features.recipes.data.Ingredient
import features.recipes.types.*
import features.recipes.types.CraftingSpecial

data class Recipes(val dp: DataPack)

fun Recipes.blasting(name: String, block: Blasting.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, Blasting(mutableListOf(), item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun Recipes.campfireCooking(name: String, block: CookingRecipe.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, CampfireCooking(mutableListOf(), item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun Recipes.craftingShaped(name: String, block: CraftingShaped.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, CraftingShaped(result = CraftingResult(item = item(""))).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun CraftingShaped.key(key: String, ingredients: List<Ingredient>) = this.key.put(key, ingredients.toMutableList())
fun CraftingShaped.key(key: String, block: Ingredient.() -> Unit) = this.key.put(key, mutableListOf(Ingredient().apply(block)))
fun CraftingShaped.key(key: String, item: Argument.Item? = null, tag: Argument.ItemTag? = null) =
	this.key.put(key, mutableListOf(Ingredient(item, tag)))

class Keys(val key: MutableMap<String, MutableList<Ingredient>>) {
	infix fun String.to(item: Argument.Item) = key.put(this, mutableListOf(Ingredient(item)))
	infix fun String.to(tag: Argument.ItemTag) = key.put(this, mutableListOf(Ingredient(tag = tag)))
	infix fun String.to(ingredients: List<Ingredient>) = key.put(this, ingredients.toMutableList())
}

fun CraftingShaped.keys(block: Keys.() -> Unit) = Keys(key).apply(block)

fun CraftingShaped.patternLine(line: String) = pattern.add(line)
fun CraftingShaped.pattern(vararg lines: String) = pattern.addAll(lines.toList())

fun Recipes.craftingShapeless(name: String, block: CraftingShapeless.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, CraftingShapeless(result = CraftingResult(item = item(""))).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun CraftingShapeless.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun CraftingShapeless.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))

fun Recipes.craftingSpecial(name: String, craftingTypeName: String, block: CraftingSpecial.() -> Unit) =
	dp.recipes.add(RecipeFile(name, CraftingSpecial(craftingTypeName).apply(block)))

fun Recipes.craftingSpecial(name: String, craftingSpecial: CraftingSpecial) =
	dp.recipes.add(RecipeFile(name, craftingSpecial))

fun Recipes.smelting(name: String, block: CookingRecipe.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, Smelting(mutableListOf(), item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun Recipes.smithing(name: String, block: Smithing.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, Smithing(base = Ingredient(), addition = Ingredient(), result = item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun Smithing.base(block: Ingredient.() -> Unit) {
	base = Ingredient().apply(block)
}

fun Smithing.base(item: Argument.Item? = null, tag: Argument.ItemTag? = null) {
	base = Ingredient(item, tag)
}

fun Smithing.addition(block: Ingredient.() -> Unit) {
	addition = Ingredient().apply(block)
}

fun Smithing.addition(item: Argument.Item? = null, tag: Argument.ItemTag? = null) {
	addition = Ingredient(item, tag)
}

fun Recipes.smoking(name: String, block: CookingRecipe.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, Smoking(mutableListOf(), item("")).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun Recipes.stoneCutting(name: String, block: StoneCutting.() -> Unit): Argument.Recipe {
	dp.recipes.add(RecipeFile(name, StoneCutting(ingredient = mutableListOf(), result = item(""), count = 1).apply(block)))
	return Argument.Recipe(name, dp.name)
}

fun StoneCutting.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun StoneCutting.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))

fun CookingRecipe.ingredient(block: Ingredient.() -> Unit) = ingredient.add(Ingredient().apply(block))
fun CookingRecipe.ingredient(item: Argument.Item? = null, tag: Argument.ItemTag? = null) = ingredient.add(Ingredient(item, tag))

fun CraftingRecipe.result(block: CraftingResult.() -> Unit) {
	result = CraftingResult(item = item("")).apply(block)
}

fun CraftingRecipe.result(item: Argument.Item, count: Int? = null) {
	result = CraftingResult(item, count)
}
