package teams

import arguments.Argument
import arguments.Color
import commands.CollisionRule
import commands.Visibility
import commands.teams
import entities.Entity
import functions.Function
import net.benwoodworth.knbt.NbtTag

data class Team(val name: String)

fun team(name: String, init: Team.() -> Unit = {}) = Team(name).apply(init)

context(Function)
fun Team.ensureExists() = teams {
	add(name)
}

context(Function)
fun Team.delete() = teams {
	remove(name)
}

context(Function)
fun Team.setDisplayName(displayName: NbtTag) = teams {
	modify(name) {
		displayName(displayName)
	}
}

context(Function)
fun Team.setPrefix(prefix: NbtTag) = teams {
	modify(name) {
		prefix(prefix)
	}
}

context(Function)
fun Team.setSuffix(suffix: NbtTag) = teams {
	modify(name) {
		suffix(suffix)
	}
}

context(Function)
fun Team.setCollisionRule(rule: CollisionRule) = teams {
	modify(name) {
		collisionRule(rule)
	}
}

context(Function)
fun Team.setFriendlyFire(friendlyFire: Boolean) = teams {
	modify(name) {
		friendlyFire(friendlyFire)
	}
}

context(Function)
fun Team.setSeeFriendlyInvisibles(seeFriendlyInvisibles: Boolean) = teams {
	modify(name) {
		seeFriendlyInvisibles(seeFriendlyInvisibles)
	}
}

context(Function)
fun Team.setNametagVisibility(visibility: Visibility) = teams {
	modify(name) {
		nametagVisibility(visibility)
	}
}

context(Function)
fun Team.setDeathMessageVisibility(visibility: Visibility) = teams {
	modify(name) {
		deathMessageVisibility(visibility)
	}
}

context(Function)
fun Team.setColor(color: Color) = teams {
	modify(name) {
		color(color)
	}
}

context(Function)
fun Team.addMembers(vararg members: Argument.ScoreHolder) = teams {
	members.forEach { member ->
		join(name, member)
	}
}

context(Function)
fun Team.addMembers(vararg members: Entity) = addMembers(*members.map { it.asSelector() }.toTypedArray())
