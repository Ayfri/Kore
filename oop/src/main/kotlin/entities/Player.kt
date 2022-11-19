package entities

class Player(name: String) : Entity() {
	init {
		selector.name = name
	}
	
	var name: String
		get() = selector.name ?: throw IllegalStateException("Player name is null")
		set(value) {
			selector.name = value
		}
	
	override val type = "player"
}

fun player(name: String) = Player(name)
