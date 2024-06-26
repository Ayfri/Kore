@file:Suppress("UNSUPPORTED_FEATURE")

package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.coordinate
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.Execute
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.teams.Team
import io.github.ayfri.kore.teams.addMembers

open class Entity(val selector: SelectorArguments = SelectorArguments()) {
	open val type = selector.type
	open val isPlayer get() = type?.name == "player"

	fun asSelector(modification: SelectorArguments.() -> Unit = {}) = allEntities(true) {
		copyFrom(selector)
		modification()
		type = this@Entity.type
	}

	var team: String?
		get() = selector.team
		set(value) {
			selector.team = value
		}
}

inline fun <reified T : Entity> Entity.toEntityOrNull() = when (this) {
	is T -> this
	else -> when (T::class) {
		Player::class -> selector.name?.let { Player(it) }
		else -> null
	} as T?
}

inline fun <reified T : Entity> Entity.toEntity() =
	toEntityOrNull<T>() ?: throw IllegalArgumentException("Cannot cast entity '$this' to type '${T::class.simpleName}'")

context(Function)
fun Entity.getScoreEntity(name: String) = ScoreboardEntity(name, this@Entity)

context(Function)
fun Entity.setScore(name: String, value: Int) = scoreboard.players.set(asSelector(), name, value)

context(Function)
fun Entity.joinTeam(team: String) = teams {
	join(team, asSelector { this.team = null })
}.also { this.team = team }

context(Function)
fun Entity.joinTeam(team: Team) = team.addMembers(this@Entity.asSelector()).also { this.team = team.name }

context(Function)
fun Entity.leaveAnyTeam() = teams {
	leave(asSelector())
}.also { team = null }

context(Function)
fun Entity.giveItem(item: ItemStack) = give(asSelector(), item.toItemArgument(), item.count?.toInt())

context(Function)
fun Entity.replaceItem(slot: ItemSlotType, item: ItemStack) = items {
	replace(asSelector(), slot, item.toItemArgument(), item.count?.toInt())
}

context(Function)
fun <T : Entity> T.executeAs(block: Execute.(T) -> FunctionArgument) = execute {
	asTarget(asSelector())
	block(this@executeAs)
}

context(Function)
fun <T : Entity> T.executeAt(block: Execute.(T) -> FunctionArgument) = execute {
	at(asSelector())
	block(this@executeAt)
}

context(Function)
fun <T : Entity> T.executeAsAt(block: Execute.(T) -> FunctionArgument) = execute {
	asTarget(asSelector())
	at(asSelector())
	block(this@executeAsAt)
}

context(Function)
fun Entity.teleportTo(entity: Entity) = teleport(asSelector(), if (entity == this) self() else entity.asSelector())

context(Function)
fun Entity.teleportTo(x: Number, y: Number, z: Number, yaw: Number? = null, pitch: Number? = null): Command {
	val rotation = if (yaw != null && pitch != null) rotation(yaw, pitch) else null
	return teleport(asSelector(), coordinate(x, y, z), rotation)
}

context(Function)
fun Entity.teleportTo(coordinate: Vec3, rotation: RotationArgument? = null) = teleport(asSelector(), coordinate, rotation)
