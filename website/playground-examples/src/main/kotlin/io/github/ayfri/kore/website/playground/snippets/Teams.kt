package io.github.ayfri.kore.website.playground.snippets.teams

import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.commands.CollisionRule
import io.github.ayfri.kore.dataPack
import io.github.ayfri.kore.entities.joinTeam
import io.github.ayfri.kore.entities.player
import io.github.ayfri.kore.functions.load
import io.github.ayfri.kore.pack.pack
import io.github.ayfri.kore.teams.ensureExists
import io.github.ayfri.kore.teams.setCollisionRule
import io.github.ayfri.kore.teams.setColor
import io.github.ayfri.kore.teams.setFriendlyFire
import io.github.ayfri.kore.teams.setPrefix
import io.github.ayfri.kore.teams.team

fun playground() = dataPack("team_battle") {
	pack { description = textComponent("Two teams with colors, prefixes and friendly fire off") }

	val steve = player("Steve")
	val alex = player("Alex")

	load("setup_teams") {
		val red = team("red") {
			ensureExists()
			setColor(Color.RED)
			setPrefix(textComponent("RED ", Color.RED))
			setFriendlyFire(false)
			setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS)
		}

		val blue = team("blue") {
			ensureExists()
			setColor(Color.BLUE)
			setPrefix(textComponent("BLUE ", Color.BLUE))
			setFriendlyFire(false)
			setCollisionRule(CollisionRule.PUSH_OTHER_TEAMS)
		}

		steve.joinTeam(red)
		alex.joinTeam(blue)
	}
}
