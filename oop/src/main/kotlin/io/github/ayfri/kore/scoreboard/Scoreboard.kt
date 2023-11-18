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

context(Function)
fun Scoreboard.getScore(entity: Entity) = ScoreboardEntity(name, entity)

context(Function)
fun Scoreboard.create(criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY) = scoreboard {
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
