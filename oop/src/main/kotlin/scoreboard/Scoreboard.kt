package scoreboard

import arguments.DisplaySlot
import arguments.chatcomponents.ChatComponents
import arguments.chatcomponents.textComponent
import arguments.scores.ScoreboardCriterion
import commands.RenderType
import commands.scoreboard
import entities.Entity
import functions.Function

open class Scoreboard(val name: String)

fun scoreboard(name: String, init: Scoreboard.() -> Unit = {}) = Scoreboard(name).apply(init)

context(Function)
fun Scoreboard.getScore(entity: Entity) = ScoreboardEntity(name, entity)

context(Function)
fun Scoreboard.create(criteria: ScoreboardCriterion) = scoreboard {
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
fun Scoreboard.setDisplaySlot(slot: DisplaySlot) = scoreboard {
	objectives {
		setDisplay(slot, name)
	}
}

context(Function)
fun Scoreboard.setRenderType(type: RenderType) = scoreboard {
	objectives {
		modify(name, type)
	}
}

context(Function)
fun Scoreboard.setDisplayName(displayName: ChatComponents) = scoreboard {
	objectives {
		modify(name, displayName)
	}
}

context(Function)
fun Scoreboard.setDisplayName(displayName: String) = scoreboard {
	objectives {
		modify(name, textComponent(displayName))
	}
}
