package io.github.ayfri.kore.arguments.scores

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.arguments.types.resources.CustomStatArgument
import io.github.ayfri.kore.arguments.types.resources.EntityTypeArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.generated.StatisticTypes

interface ScoreboardCriterion : Argument

enum class ScoreboardCriteria : ScoreboardCriterion {
	AIR,
	ARMOR,
	DEATH_COUNT,
	DUMMY,
	FOOD,
	HEALTH,
	LEVEL,
	PLAYER_KILL_COUNT,
	TOTAL_KILL_COUNT,
	TRIGGER,
	XP;

	override fun asString() = name.lowercase()
}

data class StatScoreboardCriteria(val statType: StatisticTypes, val value: ResourceLocationArgument) : ScoreboardCriterion {
	override fun asString() = "${statType.asId().replace(":", ".")}:${value.asId().replace(":", ".")}"
}

data class CompoundScoreboardCriteria(val name: String, val teamColor: FormattingColor) : ScoreboardCriterion {
	override fun asString() = "$name.${teamColor.asString()}"
}

fun criteriaTeamKill(teamColor: FormattingColor) = CompoundScoreboardCriteria("teamkill", teamColor)
fun criteriaKilledByTeam(teamColor: FormattingColor) = CompoundScoreboardCriteria("killed_by_team", teamColor)

fun criteriaStat(statType: StatisticTypes, value: ResourceLocationArgument) = StatScoreboardCriteria(statType, value)

fun criteriaBroken(value: ItemArgument) = criteriaStat(StatisticTypes.BROKEN, value)
fun criteriaCrafted(value: ItemArgument) = criteriaStat(StatisticTypes.CRAFTED, value)
fun criteriaCustom(value: CustomStatArgument) = criteriaStat(StatisticTypes.CUSTOM, value)
fun criteriaDropped(value: ItemArgument) = criteriaStat(StatisticTypes.DROPPED, value)
fun criteriaKilled(value: EntityTypeArgument) = criteriaStat(StatisticTypes.KILLED, value)
fun criteriaKilledBy(value: EntityTypeArgument) = criteriaStat(StatisticTypes.KILLED_BY, value)
fun criteriaMined(value: BlockArgument) = criteriaStat(StatisticTypes.MINED, value)
fun criteriaPickedUp(value: ItemArgument) = criteriaStat(StatisticTypes.PICKED_UP, value)
fun criteriaUsed(value: ItemArgument) = criteriaStat(StatisticTypes.USED, value)
