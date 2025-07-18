package io.github.ayfri.kore.scoreboard

import io.github.ayfri.kore.arguments.DisplaySlot
import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.scores.ScoreboardCriteria
import io.github.ayfri.kore.arguments.scores.ScoreboardCriterion
import io.github.ayfri.kore.commands.scoreboard.RenderType
import io.github.ayfri.kore.commands.scoreboard.scoreboard
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

open class Scoreboard(val name: String)

fun scoreboard(name: String, init: Scoreboard.() -> Unit = {}) = Scoreboard(name).apply(init)

context(fn: Function)
fun Scoreboard.getScore(entity: Entity) = ScoreboardEntity(name, entity)

context(fn: Function)
fun Scoreboard.create(criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY) = fn.scoreboard {
	objectives {
		add(name, criteria)
	}
}

context(fn: Function)
fun Scoreboard.remove() = fn.scoreboard {
	objectives {
		remove(name)
	}
}

context(fn: Function)
fun Scoreboard.setDisplaySlot(slot: DisplaySlot) = fn.scoreboard {
	objectives {
		setDisplay(slot, name)
	}
}

context(fn: Function)
fun Scoreboard.setRenderType(type: RenderType) = fn.scoreboard {
	objectives {
		modifyRenderType(name, type)
	}
}

context(fn: Function)
fun Scoreboard.setDisplayName(displayName: ChatComponents) = fn.scoreboard {
	objectives {
		modifyDisplayName(name, displayName)
	}
}

context(fn: Function)
fun Scoreboard.setDisplayName(displayName: String) = fn.scoreboard {
	objectives {
		modifyDisplayName(name, textComponent(displayName))
	}
}
