package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.resources.StatisticArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerFoodPredicate(
	var level: IntRangeOrIntJson? = null,
	var saturation: FloatRangeOrFloatJson? = null,
)

@Serializable
data class Input(
	var forward: Boolean? = null,
	var backward: Boolean? = null,
	var left: Boolean? = null,
	var right: Boolean? = null,
	var jump: Boolean? = null,
	var sneak: Boolean? = null,
	var sprint: Boolean? = null,
)

@Serializable
@SerialName("type_specific/player")
data class PlayerSubPredicate(
	var lookingAt: Entity? = null,
	var advancements: Advancements? = null,
	var food: PlayerFoodPredicate? = null,
	var gamemode: List<Gamemode>? = null,
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: Map<StatisticArgument, Statistic>? = null,
	var input: Input? = null,
) : EntitySubPredicate()

fun EntityTypeSpecificScope.player(block: PlayerSubPredicate.() -> Unit = {}) {
	entity.subPredicates += PlayerSubPredicate().apply(block)
}

fun PlayerSubPredicate.advancements(block: Advancements.() -> Unit) {
	advancements = Advancements().apply(block)
}

fun PlayerSubPredicate.gamemodes(vararg gamemodes: Gamemode) {
	gamemode = gamemodes.toList()
}

fun PlayerSubPredicate.lookingAt(block: Entity.() -> Unit = {}) {
	lookingAt = Entity().apply(block)
}

fun PlayerSubPredicate.stats(block: MutableMap<StatisticArgument, Statistic>.() -> Unit = {}) {
	stats = buildMap(block)
}

fun MutableMap<StatisticArgument, Statistic>.statistic(statistic: StatisticArgument, block: Statistic.() -> Unit = {}) {
	put(statistic, Statistic().apply(block))
}

fun PlayerSubPredicate.recipes(vararg recipes: RecipeArgument, block: MutableMap<RecipeArgument, Boolean>.() -> Unit = {}) {
	this.recipes = (this.recipes ?: mutableMapOf()) + buildMap(block) + recipes.associateWith { true }
}

fun PlayerSubPredicate.food(block: PlayerFoodPredicate.() -> Unit = {}) {
	food = PlayerFoodPredicate().apply(block)
}

fun PlayerSubPredicate.input(block: Input.() -> Unit = {}) {
	input = Input().apply(block)
}
