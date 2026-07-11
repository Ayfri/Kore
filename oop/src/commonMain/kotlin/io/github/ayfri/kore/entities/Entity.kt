package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.ItemSlotType
import io.github.ayfri.kore.arguments.enums.DataType
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.maths.coordinate
import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.arguments.types.literals.RotationArgument
import io.github.ayfri.kore.arguments.types.literals.allEntities
import io.github.ayfri.kore.arguments.types.literals.rotation
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.arguments.types.resources.FunctionArgument
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
import io.github.ayfri.kore.commands.*
import io.github.ayfri.kore.commands.execute.Execute
import io.github.ayfri.kore.commands.execute.execute
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.functions.generatedFunction
import io.github.ayfri.kore.scoreboard.ScoreboardEntity
import io.github.ayfri.kore.teams.Team
import io.github.ayfri.kore.teams.addMembers
import io.github.ayfri.kore.commands.function as functionCommand

/** Wraps a selector into an object-oriented entity handle with helper extensions. */
open class Entity(
	val selector: SelectorArguments = SelectorArguments(),
	open val limitToOne: Boolean = true,
) {
	/** Whether this entity currently resolves to a player selector. */
	open val isPlayer get() = type?.name == "player"

	/** The selected entity type, if one is constrained on the selector. */
	open val type = selector.type

	/** The team filter currently applied to this selector. */
	var team: String?
		get() = selector.team
		set(value) {
			selector.team = value
		}

	/** Builds an `@e` selector mirroring this entity, with optional extra modifications. */
	fun asSelector(limitToOne: Boolean = this.limitToOne, modification: SelectorArguments.() -> Unit = {}) =
		allEntities(limitToOne) {
			copyFrom(selector)
			modification()
		}
}

/** Executes a block as this entity. */
context(fn: Function)
fun <T : Entity> T.executeAs(block: Execute.(T) -> FunctionArgument) = fn.execute {
	asTarget(asSelector())
	block(this@executeAs)
}

/** Executes a block as and at this entity. */
context(fn: Function)
fun <T : Entity> T.executeAsAt(block: Execute.(T) -> FunctionArgument) = fn.execute {
	asTarget(asSelector())
	at(asSelector())
	block(this@executeAsAt)
}

/** Executes a block at this entity's position. */
context(fn: Function)
fun <T : Entity> T.executeAt(block: Execute.(T) -> FunctionArgument) = fn.execute {
	at(asSelector())
	block(this@executeAt)
}

/** Groups multiple entity-scoped commands into one generated batch function. */
context(fn: Function)
fun Entity.batch(name: String, block: Function.() -> Unit): FunctionArgument {
	val batchFn = fn.datapack.generatedFunction("batch_$name", block = block)
	fn.execute {
		asTarget(asSelector())
		run { functionCommand(batchFn) }
	}
	return batchFn
}

/** Returns a scoreboard handle tied to this entity. */
context(fn: Function)
fun Entity.getScoreEntity(name: String) = ScoreboardEntity(name, this)

/** Stores how many entities currently match this selector into [score]. */
context(fn: Function)
fun Entity.storeCountIn(score: ScoreboardEntity) = fn.execute {
	storeResult { score(score.entity.asSelector(), score.name) }
	run {
		execute {
			ifCondition {
				entity(this@storeCountIn.asSelector(limitToOne = false))
			}
		}
	}
}

/** Stores how many entities currently match this selector into an entity NBT path. */
context(fn: Function)
fun Entity.storeCountIn(target: Entity, path: String, type: DataType = DataType.INT, scale: Double = 1.0) = fn.execute {
	storeResult { entity(target.asSelector(), path, type, scale) }
	run {
		execute {
			ifCondition {
				entity(this@storeCountIn.asSelector(limitToOne = false))
			}
		}
	}
}

/** Stores how many entities currently match this selector into a storage NBT path. */
context(fn: Function)
fun Entity.storeCountIn(target: StorageArgument, path: String, type: DataType = DataType.INT, scale: Double = 1.0) =
	fn.execute {
		storeResult { storage(target, path, type, scale) }
		run {
			execute {
				ifCondition {
					entity(this@storeCountIn.asSelector(limitToOne = false))
				}
			}
		}
	}

/** Gives an item stack to this entity. */
context(fn: Function)
fun Entity.giveItem(item: ItemStack) = fn.give(asSelector(), item.toItemArgument(), item.count?.toInt())

/** Gives an item to this entity. */
context(fn: Function)
fun Entity.giveItem(item: ItemArgument, count: Int = 1) = fn.give(asSelector(), item, count)

/** Adds this entity to the team named [team]. */
context(fn: Function)
fun Entity.joinTeam(team: String) = fn.teams {
	join(team, asSelector { this.team = null })
}.also { this.team = team }

/** Adds this entity to an existing [Team] handle. */
context(fn: Function)
fun Entity.joinTeam(team: Team) = team.addMembers(asSelector()).also { this.team = team.name }

/** Removes this entity from any current team. */
context(fn: Function)
fun Entity.leaveAnyTeam() = fn.teams {
	leave(asSelector())
}.also { team = null }

/** Replaces the item in [slot] for this entity. */
context(fn: Function)
fun Entity.replaceItem(slot: ItemSlotType, item: ItemStack) = fn.items {
	replace(asSelector(), slot, item.toItemArgument(), item.count?.toInt())
}

/** Replaces the item in [slot] for this entity. */
context(fn: Function)
fun Entity.replaceItem(slot: ItemSlotType, item: ItemArgument, count: Int = 1) = fn.items {
	replace(asSelector(), slot, item, count)
}

/** Sets this entity's score in the objective named [name]. */
context(fn: Function)
fun Entity.setScore(name: String, value: Int) = fn.scoreboard.players.set(asSelector(), name, value)

/** Teleports this entity to [coordinate] with an optional [rotation]. */
context(fn: Function)
fun Entity.teleportTo(coordinate: Vec3, rotation: RotationArgument? = null) =
	fn.teleport(asSelector(), coordinate, rotation)

/** Teleports this entity to another entity. */
context(fn: Function)
fun Entity.teleportTo(entity: Entity) = fn.teleport(asSelector(), if (entity == this) self() else entity.asSelector())

/** Teleports this entity to explicit coordinates and optional yaw/pitch. */
context(fn: Function)
fun Entity.teleportTo(x: Number, y: Number, z: Number, yaw: Number? = null, pitch: Number? = null): Command {
	val rotation = if (yaw != null && pitch != null) rotation(yaw, pitch) else null
	return fn.teleport(asSelector(), coordinate(x, y, z), rotation)
}

/** Casts this entity to [T] or throws when the conversion is not possible. */
inline fun <reified T : Entity> Entity.toEntity() =
	toEntityOrNull<T>() ?: throw IllegalArgumentException("Cannot cast entity '$this' to type '${T::class.simpleName}'")

/** Casts this entity to [T] when possible, otherwise returns `null`. */
inline fun <reified T : Entity> Entity.toEntityOrNull() = when (this) {
	is T -> this
	else -> when (T::class) {
		Player::class -> selector.name?.let { Player(it) }
		else -> null
	} as T?
}
