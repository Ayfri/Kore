package generators

import generateEnum
import getFromCacheOrDownloadTxt
import removeJSONSuffix
import url

suspend fun downloadRecipes() {
	val url = url("custom-generated/lists/recipes.txt")
	val recipes = getFromCacheOrDownloadTxt("recipes.txt", url).lines()

	generateRecipesEnum(recipes, url)
}

fun generateRecipesEnum(recipes: List<String>, sourceUrl: String) {
	generateEnum(recipes.removeJSONSuffix(), "Recipes", sourceUrl, "Recipe")
}
