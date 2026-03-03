package io.github.ayfri.kore.entities

import io.github.ayfri.kore.arguments.selector.SelectorArguments
import io.github.ayfri.kore.generated.EntityTypes

class Player(name: String) : Entity() {
	init {
		selector.name = name
		selector.type = EntityTypes.PLAYER
	}

	var name: String
		get() = selector.name ?: throw IllegalStateException("Player name is null")
		set(value) {
			selector.name = value
		}

	override val type = EntityTypes.PLAYER
}

fun player(name: String, nbtData: SelectorArguments.() -> Unit = {}) = Player(name).apply {
	selector.nbtData()
}

fun entity(nbtData: SelectorArguments.() -> Unit = {}) = Entity(SelectorArguments().apply(nbtData))
