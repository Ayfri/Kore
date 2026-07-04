package io.github.ayfri.kore.features.advancements

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.Command
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.generated.arguments.types.LootTableArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.Serializable

/**
 * Rewards granted when an advancement is completed.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements#rewards
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement#JSON_format
 */
@Serializable
data class AdvancementReward(
	/** The amount of experience granted when the advancement is completed. */
	var experience: Int? = null,
	/** The function to be called when the advancement is completed. */
	var function: FunctionArgument? = null,
	/** The loot table to be used when the advancement is completed. */
	var loot: List<LootTableArgument>? = null,
	/** The recipes to be unlocked when the advancement is completed. */
	var recipes: List<RecipeArgument>? = null,
)

/** Set the function rewards as a function called. If [name] is null, a generated function will be created with a random name. */
fun AdvancementReward.function(datapack: DataPack, name: String? = null, block: Function.() -> Command) {
	val functionName = name ?: "generated_${hashCode()}"
	val generatedFunction = if (name != null) {
		datapack.function(name) { block() }
	} else {
		datapack.generatedFunction(functionName) { block() }
	}
	function = generatedFunction
}

/** Set the function rewards as a function called. If [name] is null, a generated function will be created with a random name. */
context(dp: DataPack)
fun AdvancementReward.function(name: String? = null, block: Function.() -> Command) {
	function(dp, name, block)
}

/** Set the loot table rewards. */
fun AdvancementReward.loots(vararg loot: LootTableArgument) {
	this.loot = loot.toList()
}

/** Set the recipe rewards. */
fun AdvancementReward.recipes(vararg recipe: RecipeArgument) {
	recipes = recipe.toList()
}
