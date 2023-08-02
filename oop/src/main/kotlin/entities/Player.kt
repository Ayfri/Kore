package entities

import arguments.selector.SelectorNbtData
import generated.EntityTypes

class Player(name: String) : Entity() {
	init {
		selector.name = name
	}

	var name: String
		get() = selector.name ?: throw IllegalStateException("Player name is null")
		set(value) {
			selector.name = value
		}

	override val type = EntityTypes.PLAYER
}

fun player(name: String, nbtData: SelectorNbtData.() -> Unit = {}) = Player(name).apply {
	selector.nbtData()
}
