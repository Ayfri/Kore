package features.recipes.types

import features.recipes.RecipeTypes
import kotlinx.serialization.Serializable

@Serializable
data class CraftingSpecial(
	var name: String,
) : Recipe() {
	override var group: String? = null
		@Deprecated("CraftingSpecial does not have a group", level = DeprecationLevel.HIDDEN)
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
