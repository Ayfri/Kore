package io.github.ayfri.kore.teams

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.colors.FormattingColor
import io.github.ayfri.kore.arguments.types.ScoreHolderArgument
import io.github.ayfri.kore.commands.CollisionRule
import io.github.ayfri.kore.commands.Visibility
import io.github.ayfri.kore.commands.teams
import io.github.ayfri.kore.entities.Entity
import io.github.ayfri.kore.functions.Function

/** Wraps a Minecraft team name and exposes convenience helpers around `/team`. */
data class Team(
	/** The team identifier used in commands. */
	val name: String,
)

/** Creates a [Team] and lets you configure it inline before reuse. */
fun team(name: String, init: Team.() -> Unit = {}) = Team(name).apply(init)

/** Ensures the team exists before applying other team-related commands. */
context(fn: Function)
fun Team.ensureExists() = fn.teams {
	add(name)
}

/** Deletes the team and removes all of its members. */
context(fn: Function)
fun Team.delete() = fn.teams {
	remove(name)
}

/** Updates the display name shown for this team. */
context(fn: Function)
fun Team.setDisplayName(displayName: ChatComponents) = fn.teams {
	modify(name) {
		displayName(displayName)
	}
}

/** Sets the prefix applied before member names. */
context(fn: Function)
fun Team.setPrefix(prefix: ChatComponents) = fn.teams {
	modify(name) {
		prefix(prefix)
	}
}

/** Sets the suffix applied after member names. */
context(fn: Function)
fun Team.setSuffix(suffix: ChatComponents) = fn.teams {
	modify(name) {
		suffix(suffix)
	}
}

/** Changes how members of this team collide with other entities. */
context(fn: Function)
fun Team.setCollisionRule(rule: CollisionRule) = fn.teams {
	modify(name) {
		collisionRule(rule)
	}
}

/** Enables or disables friendly fire between members of the team. */
context(fn: Function)
fun Team.setFriendlyFire(friendlyFire: Boolean) = fn.teams {
	modify(name) {
		friendlyFire(friendlyFire)
	}
}

/** Controls whether invisible teammates remain visible to one another. */
context(fn: Function)
fun Team.setSeeFriendlyInvisibles(seeFriendlyInvisibles: Boolean) = fn.teams {
	modify(name) {
		seeFriendlyInvisibles(seeFriendlyInvisibles)
	}
}

/** Controls how player nametags are rendered for this team. */
context(fn: Function)
fun Team.setNametagVisibility(visibility: Visibility) = fn.teams {
	modify(name) {
		nametagVisibility(visibility)
	}
}

/** Controls who can see death messages involving this team. */
context(fn: Function)
fun Team.setDeathMessageVisibility(visibility: Visibility) = fn.teams {
	modify(name) {
		deathMessageVisibility(visibility)
	}
}

/** Sets the formatting color used for the team and its chat display. */
context(fn: Function)
fun Team.setColor(color: FormattingColor) = fn.teams {
	modify(name) {
		color(color)
	}
}

/** Adds raw score holders to this team. */
context(fn: Function)
fun Team.addMembers(vararg members: ScoreHolderArgument) = members.forEach { fn.teams { join(name, it) } }

/** Adds entity selectors to this team, clearing their previous team first. */
context(fn: Function)
fun Team.addMembers(vararg members: Entity) = addMembers(*members.map { it.asSelector { team = null } }.toTypedArray())
