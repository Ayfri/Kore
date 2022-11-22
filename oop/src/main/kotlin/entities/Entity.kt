@file:Suppress("UNSUPPORTED_FEATURE")

package entities

import arguments.Argument
import arguments.Coordinate
import arguments.SlotEntry
import arguments.allEntities
import arguments.coordinate
import arguments.rotation
import arguments.selector.SelectorNbtData
import arguments.self
import commands.Command
import commands.Execute
import commands.execute
import commands.give
import commands.items
import commands.teams
import commands.teleport
import functions.Function
import items.ItemStack
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

context(Function)
fun Entity.giveItem(item: ItemStack) = give(asSelector(), item.asArgument(), item.count)

context(Function)
fun Entity.replaceItem(slot: SlotEntry, item: ItemStack) = items {
	replaceEntity(asSelector(), slot, item.asArgument(), item.count)
}

context(Function)
fun <T : Entity> T.executeAs(block: Execute.(T) -> Command) = execute {
	asTarget(asSelector())
	block(this@executeAs)
}

context(Function)
fun <T : Entity> T.executeAt(block: Execute.(T) -> Command) = execute {
	at(asSelector())
	block(this@executeAt)
}

context(Function)
fun <T : Entity> T.executeAsAt(block: Execute.(T) -> Command) = execute {
	asTarget(asSelector())
	at(self())
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
fun Entity.teleportTo(coordinate: Coordinate, rotation: Argument.Rotation? = null) = teleport(asSelector(), coordinate, rotation)
