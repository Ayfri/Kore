package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.selector.Selector
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.selector.SelectorType
import io.github.ayfri.kore.arguments.types.DataArgument
import io.github.ayfri.kore.arguments.types.EntityArgument
import io.github.ayfri.kore.arguments.types.PossessorArgument
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class SelectorArgument(val selector: Selector) : EntityArgument, DataArgument, PossessorArgument, ScoreHolderArgument {
	override fun asString() = selector.toString()
}

fun selector(base: SelectorType, limitToOne: Boolean = false, data: SelectorArguments.() -> Unit = {}) =
	SelectorArgument(Selector(base).apply {
		nbtData.data()
		if (limitToOne) nbtData.limit = 1
	})

fun allPlayers(limitToOne: Boolean = false, data: SelectorArguments.() -> Unit = {}) = selector(SelectorType.ALL_PLAYERS, limitToOne, data)
fun allPlayers(limit: Int, data: SelectorArguments.() -> Unit = {}) = allPlayers { this.limit = limit; data() }
fun allEntities(limitToOne: Boolean = false, data: SelectorArguments.() -> Unit = {}) =
	selector(SelectorType.ALL_ENTITIES, limitToOne, data)

fun allEntities(limit: Int, data: SelectorArguments.() -> Unit = {}) = allEntities { this.limit = limit; data() }
fun allEntitiesLimitToOne(data: SelectorArguments.() -> Unit = {}) = allEntities(true, data)
fun nearestEntity(data: SelectorArguments.() -> Unit = {}) = selector(SelectorType.NEAREST_ENTITY, data = data)
fun nearestPlayer(data: SelectorArguments.() -> Unit = {}) = selector(SelectorType.NEAREST_PLAYER, data = data)
fun player(name: String, limitToOne: Boolean = true, data: SelectorArguments.() -> Unit = {}) =
	allPlayers(limitToOne) { this.name = name; data() }

fun randomPlayer(data: SelectorArguments.() -> Unit = {}) = selector(SelectorType.RANDOM_PLAYER, data = data)
fun self(limitToOne: Boolean = false, data: SelectorArguments.() -> Unit = {}) = selector(SelectorType.SELF, limitToOne, data)
