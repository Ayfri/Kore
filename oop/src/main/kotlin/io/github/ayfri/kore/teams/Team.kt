package io.github.ayfri.kore.teams

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.CollisionRule
import io.github.ayfri.kore.commands.Visibility
import io.github.ayfri.kore.commands.teams
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

data class Team(val name: String)

fun team(name: String, init: Team.() -> Unit = {}) = Team(name).apply(init)

context(fn: Function)
fun Team.ensureExists() = fn.teams {
	add(name)
}

context(fn: Function)
fun Team.delete() = fn.teams {
	remove(name)
}

context(fn: Function)
fun Team.setDisplayName(displayName: ChatComponents) = fn.teams {
	modify(name) {
		displayName(displayName)
	}
}

context(fn: Function)
fun Team.setPrefix(prefix: ChatComponents) = fn.teams {
	modify(name) {
		prefix(prefix)
	}
}

context(fn: Function)
fun Team.setSuffix(suffix: ChatComponents) = fn.teams {
	modify(name) {
		suffix(suffix)
	}
}

context(fn: Function)
fun Team.setCollisionRule(rule: CollisionRule) = fn.teams {
	modify(name) {
		collisionRule(rule)
	}
}

context(fn: Function)
fun Team.setFriendlyFire(friendlyFire: Boolean) = fn.teams {
	modify(name) {
		friendlyFire(friendlyFire)
	}
}

context(fn: Function)
fun Team.setSeeFriendlyInvisibles(seeFriendlyInvisibles: Boolean) = fn.teams {
	modify(name) {
		seeFriendlyInvisibles(seeFriendlyInvisibles)
	}
}

context(fn: Function)
fun Team.setNametagVisibility(visibility: Visibility) = fn.teams {
	modify(name) {
		nametagVisibility(visibility)
	}
}

context(fn: Function)
fun Team.setDeathMessageVisibility(visibility: Visibility) = fn.teams {
	modify(name) {
		deathMessageVisibility(visibility)
	}
}

context(fn: Function)
fun Team.setColor(color: FormattingColor) = fn.teams {
	modify(name) {
		color(color)
	}
}

context(fn: Function)
fun Team.addMembers(vararg members: ScoreHolderArgument) = members.forEach { fn.teams { join(name, it) } }

context(fn: Function)
fun Team.addMembers(vararg members: Entity) = addMembers(*members.map { it.asSelector { team = null } }.toTypedArray())
