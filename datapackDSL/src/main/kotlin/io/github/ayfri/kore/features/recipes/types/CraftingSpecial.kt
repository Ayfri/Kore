package io.github.ayfri.kore.features.recipes.types

import io.github.ayfri.kore.features.recipes.RecipeFile
import io.github.ayfri.kore.features.recipes.RecipeTypes
import io.github.ayfri.kore.features.recipes.Recipes
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class CraftingSpecial(
	var name: String,
) : Recipe() {
	@Transient
	@Deprecated("CraftingSpecial does not have a group", level = DeprecationLevel.HIDDEN)
	override var group: String? = null
		set(_) = Unit

	override val type = RecipeTypes.CRAFTING_SPECIAL(name)
}

val CraftingSpecialArmorDye = CraftingSpecial("armordye")
val CraftingSpecialBannerDuplicate = CraftingSpecial("bannerduplicate")
val CraftingSpecialBookCloning = CraftingSpecial("bookcloning")
val CraftingSpecialFireworkRocket = CraftingSpecial("firework_rocket")
val CraftingSpecialFireworkStar = CraftingSpecial("firework_star")
val CraftingSpecialFireworkStarFade = CraftingSpecial("firework_star_fade")
val CraftingSpecialMapCloning = CraftingSpecial("mapcloning")
val CraftingSpecialMapExtending = CraftingSpecial("mapextending")
val CraftingSpecialRepairItem = CraftingSpecial("repairitem")
val CraftingSpecialShieldDecoration = CraftingSpecial("shielddecoration")
val CraftingSpecialShulkerBoxColoring = CraftingSpecial("shulkerboxcoloring")
val CraftingSpecialSuspiciousStew = CraftingSpecial("suspiciousstew")
val CraftingSpecialTippedArrow = CraftingSpecial("tippedarrow")

fun Recipes.craftingSpecial(name: String, craftingTypeName: String, block: CraftingSpecial.() -> Unit) =
	dp.recipes.add(RecipeFile(name, CraftingSpecial(craftingTypeName).apply(block)))

fun Recipes.craftingSpecial(name: String, craftingSpecial: CraftingSpecial) =
	dp.recipes.add(RecipeFile(name, craftingSpecial))
