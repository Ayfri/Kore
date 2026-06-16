package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.generated.EntityTypes
import io.github.ayfri.kore.generated.arguments.types.EntityTypeArgument

/** Specialised [Entity] handle targeting one named Minecraft player. */
class Player(name: String) : Entity() {
	init {
		selector.name = name
		selector.type = EntityTypes.PLAYER
	}

	/** The exact player name used by the selector. */
	var name: String
		get() = selector.name ?: throw IllegalStateException("Player name is null")
		set(value) {
			selector.name = value
		}

	override val type = EntityTypes.PLAYER
}

/** Creates a [Player] handle and optionally refines its selector arguments. */
fun player(name: String, nbtData: SelectorArguments.() -> Unit = {}) = Player(name).apply {
	selector.nbtData()
}

/** Creates a generic [Entity] handle from selector arguments. */
fun entity(limitToOne: Boolean = true, nbtData: SelectorArguments.() -> Unit = {}) =
	Entity(SelectorArguments().apply(nbtData), limitToOne)

/** Creates a generic [Entity] handle for the entity named [name] and optionally refines its selector arguments. */
fun entity(name: String, limitToOne: Boolean = true, nbtData: SelectorArguments.() -> Unit = {}) =
	entity(limitToOne) {
		this.name = name
		nbtData()
	}

/**
 * Creates an [Entity] OOP handle targeting all entities of this type, with [limitToOne] and optional selector refinements.
 *
 * Example: `EntityTypes.ZOMBIE.toEntity { team = "arena" }`
 */
fun EntityTypeArgument.toEntity(limitToOne: Boolean = true, block: SelectorArguments.() -> Unit = {}) =
	Entity(SelectorArguments().apply {
		type = this@toEntity
		block()
	}, limitToOne)

/**
 * Creates an [Entity] OOP handle targeting all entities of [type], with [limitToOne] and optional selector refinements.
 *
 * Example: `entity(EntityTypes.ZOMBIE, limitToOne = false) { team = "arena" }`
 */
fun entity(type: EntityTypeArgument, limitToOne: Boolean = true, block: SelectorArguments.() -> Unit = {}) =
	type.toEntity(limitToOne, block)
