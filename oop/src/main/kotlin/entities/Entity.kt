@file:Suppress("UNSUPPORTED_FEATURE")

package entities

import arguments.ItemSlotType
import arguments.maths.Vec3
import arguments.maths.coordinate
import arguments.selector.SelectorNbtData
import arguments.types.literals.RotationArgument
import arguments.types.literals.allEntities
import arguments.types.literals.rotation
import arguments.types.literals.self
import arguments.types.resources.FunctionArgument
import commands.*
import commands.execute.Execute
import commands.execute.execute
import data.item.ItemStack
import functions.Function
import scoreboard.ScoreboardEntity
import teams.Team
import teams.addMembers

open class Entity(val selector: SelectorNbtData = SelectorNbtData()) {
	open val type = selector.type
	open val isPlayer get() = type?.name == "player"

	fun asSelector(modification: SelectorNbtData.() -> Unit = {}) = allEntities(true) {
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
