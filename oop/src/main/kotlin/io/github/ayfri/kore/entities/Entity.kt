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

context(fn: Function)
fun Entity.getScoreEntity(name: String) = ScoreboardEntity(name, this)

context(fn: Function)
fun Entity.setScore(name: String, value: Int) = fn.scoreboard.players.set(asSelector(), name, value)

context(fn: Function)
fun Entity.joinTeam(team: String) = fn.teams {
	join(team, asSelector { this.team = null })
}.also { this.team = team }

context(fn: Function)
fun Entity.joinTeam(team: Team) = team.addMembers(asSelector()).also { this.team = team.name }

context(fn: Function)
fun Entity.leaveAnyTeam() = fn.teams {
	leave(asSelector())
}.also { team = null }

context(fn: Function)
fun Entity.giveItem(item: ItemStack) = fn.give(asSelector(), item.toItemArgument(), item.count?.toInt())

context(fn: Function)
fun Entity.replaceItem(slot: ItemSlotType, item: ItemStack) = fn.items {
	replace(asSelector(), slot, item.toItemArgument(), item.count?.toInt())
}

context(fn: Function)
fun <T : Entity> T.executeAs(block: Execute.(T) -> FunctionArgument) = fn.execute {
	asTarget(asSelector())
	block(this@executeAs)
}

context(fn: Function)
fun <T : Entity> T.executeAt(block: Execute.(T) -> FunctionArgument) = fn.execute {
	at(asSelector())
	block(this@executeAt)
}

context(fn: Function)
fun <T : Entity> T.executeAsAt(block: Execute.(T) -> FunctionArgument) = fn.execute {
	asTarget(asSelector())
	at(asSelector())
	block(this@executeAsAt)
}

context(fn: Function)
fun Entity.teleportTo(entity: Entity) = fn.teleport(asSelector(), if (entity == this) self() else entity.asSelector())

context(fn: Function)
fun Entity.teleportTo(x: Number, y: Number, z: Number, yaw: Number? = null, pitch: Number? = null): Command {
	val rotation = if (yaw != null && pitch != null) rotation(yaw, pitch) else null
	return fn.teleport(asSelector(), coordinate(x, y, z), rotation)
}

context(fn: Function)
fun Entity.teleportTo(coordinate: Vec3, rotation: RotationArgument? = null) = fn.teleport(asSelector(), coordinate, rotation)
