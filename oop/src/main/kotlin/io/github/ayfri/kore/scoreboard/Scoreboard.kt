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

/** Represents a scoreboard objective that can later be created or configured. */
open class Scoreboard(val name: String)

/** Creates a scoreboard wrapper and optionally configures it inline. */
fun scoreboard(name: String, init: Scoreboard.() -> Unit = {}) = Scoreboard(name).apply(init)

/** Returns a score handle scoped to [entity] for this scoreboard objective. */
context(fn: Function)
fun Scoreboard.getScore(entity: Entity) = ScoreboardEntity(name, entity)

/** Emits the command that creates this objective. */
context(fn: Function)
fun Scoreboard.create(criteria: ScoreboardCriterion = ScoreboardCriteria.DUMMY) = fn.scoreboard {
	objectives {
		add(name, criteria)
	}
}

/** Removes this objective from the scoreboard system. */
context(fn: Function)
fun Scoreboard.remove() = fn.scoreboard {
	objectives {
		remove(name)
	}
}

/** Displays this objective in the requested [slot]. */
context(fn: Function)
fun Scoreboard.setDisplaySlot(slot: DisplaySlot) = fn.scoreboard {
	objectives {
		setDisplay(slot, name)
	}
}

/** Changes how scores are rendered for this objective. */
context(fn: Function)
fun Scoreboard.setRenderType(type: RenderType) = fn.scoreboard {
	objectives {
		modifyRenderType(name, type)
	}
}

/** Sets the display name from chat components. */
context(fn: Function)
fun Scoreboard.setDisplayName(displayName: ChatComponents) = fn.scoreboard {
	objectives {
		modifyDisplayName(name, displayName)
	}
}

/** Sets the display name from a plain string. */
context(fn: Function)
fun Scoreboard.setDisplayName(displayName: String) = fn.scoreboard {
	objectives {
		modifyDisplayName(name, textComponent(displayName))
	}
}
