@file:Suppress("UNSUPPORTED_FEATURE")

package entities

import arguments.SelectorNbtData
import arguments.allEntities
import commands.teams
import functions.Function
import scoreboard.ScoreboardEntity
import teams.Team
import teams.addMembers

open class Entity(val selector: SelectorNbtData = SelectorNbtData()) {
	open val type: String = selector.type ?: "null"
	open val isPlayer get() = type == "player"
	
	fun asSelector() = allEntities(true) {
		copyFrom(selector)
		type = this@Entity.type
	}
}

inline fun <reified T : Entity> Entity.toEntityOrNull() = when (this) {
	is T -> this
	else -> when (T::class) {
		Player::class -> selector.name?.let { Player(it) }
		else -> null
	} as T?
}

inline fun <reified T : Entity> Entity.toEntity() = toEntityOrNull<T>() ?: throw IllegalArgumentException("Cannot cast entity '$this' to type '${T::class.simpleName}'")

context(Function)
fun Entity.getScore(name: String) = ScoreboardEntity(name, this@Entity)

context(Function)
fun Entity.joinTeam(team: String) = teams {
	join(team, asSelector())
}

context(Function)
fun Entity.joinTeam(team: Team) = team.addMembers(this@Entity.asSelector())

context(Function)
fun Entity.leaveAnyTeam() = teams {
	leave(asSelector())
}
