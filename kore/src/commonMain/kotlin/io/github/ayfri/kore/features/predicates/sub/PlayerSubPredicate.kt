package io.github.ayfri.kore.features.predicates.sub

import io.github.ayfri.kore.arguments.enums.Gamemode
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.FloatRangeOrFloatJson
import io.github.ayfri.kore.arguments.numbers.ranges.serializers.IntRangeOrIntJson
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.generated.arguments.types.RecipeArgument
import io.github.ayfri.kore.generated.arguments.types.StatTypeArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Matches the hunger bar of a player, as the `food` key of a [PlayerSubPredicate].
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class PlayerFoodPredicate(
	var level: IntRangeOrIntJson? = null,
	var saturation: FloatRangeOrFloatJson? = null,
)

/**
 * Matches the movement keys a player is pressing, as the `input` key of a [PlayerSubPredicate].
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
data class InputPredicate(
	var forward: Boolean? = null,
	var backward: Boolean? = null,
	var left: Boolean? = null,
	var right: Boolean? = null,
	var jump: Boolean? = null,
	var sneak: Boolean? = null,
	var sprint: Boolean? = null,
)

/**
 * Matches player-only state - advancements, game mode, experience level, recipes, statistics and inputs - keyed under
 * `minecraft:type_specific/player`.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable
@SerialName("type_specific/player")
data class PlayerSubPredicate(
	var lookingAt: EntityPredicate? = null,
	var advancements: AdvancementsPredicate? = null,
	var food: PlayerFoodPredicate? = null,
	var gamemode: List<Gamemode>? = null,
	/** Experience level. */
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: List<StatisticPredicate>? = null,
	var input: InputPredicate? = null,
) : EntitySubPredicate()

/** Adds a [PlayerSubPredicate]. */
fun EntityTypeSpecificScope.player(block: PlayerSubPredicate.() -> Unit = {}) {
	entity.subPredicates += PlayerSubPredicate().apply(block)
}

/** Matches the advancement progress of the player. */
fun PlayerSubPredicate.advancements(block: AdvancementsPredicate.() -> Unit) {
	advancements = AdvancementsPredicate().apply(block)
}

/** Matches the hunger bar of the player. */
fun PlayerSubPredicate.food(block: PlayerFoodPredicate.() -> Unit = {}) {
	food = PlayerFoodPredicate().apply(block)
}

/** Matches any of [gamemodes]. */
fun PlayerSubPredicate.gamemodes(vararg gamemodes: Gamemode) {
	gamemode = gamemodes.toList()
}

/** Matches the movement keys the player is pressing. */
fun PlayerSubPredicate.input(block: InputPredicate.() -> Unit = {}) {
	input = InputPredicate().apply(block)
}

/** Matches the entity the player is looking at. */
fun PlayerSubPredicate.lookingAt(block: EntityPredicate.() -> Unit = {}) {
	lookingAt = EntityPredicate().apply(block)
}

/** Requires [recipes] to be unlocked, on top of any recipe already declared in [block]. */
fun PlayerSubPredicate.recipes(vararg recipes: RecipeArgument, block: MutableMap<RecipeArgument, Boolean>.() -> Unit = {}) {
	this.recipes = (this.recipes ?: emptyMap()) + buildMap(block) + recipes.associateWith { true }
}

/** Matches the statistics declared in [block]. */
fun PlayerSubPredicate.stats(block: MutableList<StatisticPredicate>.() -> Unit = {}) {
	stats = (stats ?: emptyList()) + buildList(block)
}

/** Adds a [StatisticPredicate] matching an exact statistic [value]. */
fun MutableList<StatisticPredicate>.statistic(type: StatTypeArgument, stat: ResourceLocationArgument, value: Int) {
	add(statisticPredicate(type, stat, value))
}

/** Adds a [StatisticPredicate] matching a statistic value within [value]. */
fun MutableList<StatisticPredicate>.statistic(type: StatTypeArgument, stat: ResourceLocationArgument, value: IntRangeOrIntJson) {
	add(statisticPredicate(type, stat, value))
}

/** Adds a [StatisticPredicate] matching a statistic value within [value]. */
fun MutableList<StatisticPredicate>.statistic(type: StatTypeArgument, stat: ResourceLocationArgument, value: IntRange) {
	add(statisticPredicate(type, stat, value))
}
