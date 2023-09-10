package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.teamsTests() {
	teams {
		add("test") assertsIs "team add test"
		add("test", "Test") assertsIs "team add test \"Test\""
		empty("test") assertsIs "team empty test"
		join("test", self()) assertsIs "team join test @s"
		leave(self()) assertsIs "team leave @s"
		list() assertsIs "team list"
		list("test") assertsIs "team list test"

		modify("test") {
			collisionRule(CollisionRule.NEVER) assertsIs "team modify test collisionRule never"
			color(Color.RED) assertsIs "team modify test color red"
			deathMessageVisibility(Visibility.NEVER) assertsIs "team modify test deathMessageVisibility never"
			displayName("Test") assertsIs "team modify test displayName \"Test\""
			friendlyFire(true) assertsIs "team modify test friendlyFire true"
			nametagVisibility(Visibility.NEVER) assertsIs "team modify test nametagVisibility never"
			prefix("Test") assertsIs "team modify test prefix \"Test\""
			seeFriendlyInvisibles(true) assertsIs "team modify test seeFriendlyInvisibles true"
			suffix("Test") assertsIs "team modify test suffix \"Test\""
		}

		remove("test") assertsIs "team remove test"
	}
}
