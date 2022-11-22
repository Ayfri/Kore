package scoreboard

import commands.SetDisplaySlot
import commands.scoreboard
import entities.Entity
import functions.Function

open class Scoreboard(val name: String)

fun scoreboard(name: String, init: Scoreboard.() -> Unit = {}) = Scoreboard(name).apply(init)

context(Function)
fun Scoreboard.getScore(entity: Entity) = ScoreboardEntity(name, entity)

context(Function)
fun Scoreboard.create(criteria: String = "dummy") = scoreboard {
	objectives {
		add(name, criteria)
	}
}

context(Function)
fun Scoreboard.remove() = scoreboard {
	objectives {
		remove(name)
	}
}

context(Function)
fun Scoreboard.setDisplaySlot(slot: SetDisplaySlot) = scoreboard {
	objectives {
		setDisplay(slot, name)
	}
}

context(Function)
fun Scoreboard.setRenderType(type: String) = scoreboard {
	objectives {
		modify(name, type)
	}
}

context(Function)
fun Scoreboard.setDisplayName(displayName: String) = scoreboard {
	objectives {
		modify(name, displayName)
	}
}
