package io.github.ayfri.kore.features.recipes

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument

data class Recipes(val dp: DataPack) {
	/** Adds [file] to the pack under [namespace] (the pack's when `null`) and returns its id. */
	fun register(file: RecipeFile, namespace: String?): RecipeArgument {
		file.namespace = namespace
		dp.recipes += file
		return RecipeArgument(file.fileName, namespace ?: dp.name)
	}
}
