package io.github.ayfri.kore.features.predicates.sub.entityspecific

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.types.resources.RecipeArgument
import io.github.ayfri.kore.arguments.types.resources.StatisticArgument
import io.github.ayfri.kore.features.advancements.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.features.predicates.sub.Advancements
import io.github.ayfri.kore.features.predicates.sub.Entity
import io.github.ayfri.kore.features.predicates.sub.Statistic
import kotlinx.serialization.Serializable

@Serializable
data class Player(
	var lookingAt: Entity? = null,
	var advancements: Advancements? = null,
	var gamemode: Gamemode? = null,
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: Map<StatisticArgument, Statistic>? = null,
) : EntityTypeSpecific()
