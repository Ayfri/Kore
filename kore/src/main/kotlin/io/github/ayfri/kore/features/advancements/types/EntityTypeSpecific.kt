package io.github.ayfri.kore.features.advancements.types

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.types.resources.CatVariantArgument
import io.github.ayfri.kore.arguments.types.resources.FrogVariantArgument
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.StatisticArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = EntityTypeSpecific.Companion.EntityTypeSpecificSerializer::class)
sealed class EntityTypeSpecific {
	companion object {
		data object EntityTypeSpecificSerializer : NamespacedPolymorphicSerializer<EntityTypeSpecific>(EntityTypeSpecific::class)
	}
}

@Serializable
@SerialName("any")
data object AnyTypeSpecific : EntityTypeSpecific()

@Serializable
@SerialName("cat")
data class CatTypeSpecific(var variant: CatVariantArgument? = null) : EntityTypeSpecific()

@Serializable
@SerialName("fishing_hook")
data class FishingHookTypeSpecific(var inOpenWater: Boolean? = null) : EntityTypeSpecific()

@Serializable
@SerialName("frog")
data class FrogTypeSpecific(var variant: FrogVariantArgument? = null) : EntityTypeSpecific()

@Serializable
@SerialName("lightning_bolt")
data class LightningTypeSpecific(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntityTypeSpecific()

@Serializable
@SerialName("player")
data class PlayerTypeSpecific(
	var lookingAt: Entity? = null,
	var advancements: Advancements? = null,
	var gamemode: Gamemode? = null,
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: Map<StatisticArgument, Statistic>? = null,
) : EntityTypeSpecific()

@Serializable
@SerialName("slime")
data class SlimeTypeSpecific(var size: IntRangeOrIntJson? = null) : EntityTypeSpecific()
