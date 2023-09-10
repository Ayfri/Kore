package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.types.resources.CatVariantArgument
import io.github.ayfri.kore.arguments.types.resources.FrogVariantArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.StatisticArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable

@Serializable
sealed interface EntityTypeSpecific

@Serializable
data object AnyTypeSpecific : EntityTypeSpecific

@Serializable
data class CatTypeSpecific(var variant: CatVariantArgument? = null) : EntityTypeSpecific

@Serializable
data class FishingHookTypeSpecific(var inOpenWater: Boolean? = null) : EntityTypeSpecific

@Serializable
data class FrogTypeSpecific(var variant: FrogVariantArgument? = null) : EntityTypeSpecific

@Serializable
data class LightningTypeSpecific(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntityTypeSpecific

@Serializable
data class PlayerTypeSpecific(
	var lookingAt: Entity? = null,
	var advancements: Advancements? = null,
	var gamemode: Gamemode? = null,
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: Map<StatisticArgument, Statistic>? = null,
) : EntityTypeSpecific

@Serializable
data class SlimeTypeSpecific(var size: IntRangeOrIntJson? = null) : EntityTypeSpecific
