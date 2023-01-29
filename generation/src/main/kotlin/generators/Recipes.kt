package generators

import generateEnum
import getFromCacheOrDownloadTxt
import url

suspend fun downloadRecipes() {
	val url = url("data/misc/recipes.txt")
	val recipes = getFromCacheOrDownloadTxt("recipes.txt", url).lines()

	generateRecipesEnum(recipes, url)
}

fun generateRecipesEnum(recipes: List<String>, sourceUrl: String) {
	generateEnum(recipes, "Recipes", sourceUrl, "Recipe")
	/* val name = "Recipes"
	generateEnum(
		name = name,
		sourceUrl = sourceUrl,
		additionalHeaders = listOf("Minecraft version: $minecraftVersion"),
		properties = recipes.map { it.substringAfter("minecraft:").uppercase() },
		serializer = Serializer.Lowercase,
		customEncoder = """encoder.encodeString("minecraft:${"\${value.name.lowercase()}"}")""",
		additionalImports = listOf("arguments.Argument")
	) */
}
