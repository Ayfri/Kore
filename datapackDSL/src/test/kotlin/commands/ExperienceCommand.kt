package commands

import arguments.numbers.levels
import arguments.numbers.points
import arguments.types.literals.self
import functions.Function
import utils.assertsIs

fun Function.experienceTests() {
	experience(self()) {
		add(1.points) assertsIs "experience add @s 1"
		add(1.levels) assertsIs "experience add @s 1 levels"

		queryLevels() assertsIs "experience query @s levels"
		queryPoints() assertsIs "experience query @s points"

		remove(1.points) assertsIs "experience add @s -1"

		set(1.points) assertsIs "experience set @s 1"
	}

	xp(self()) {
		add(1.levels) assertsIs "experience add @s 1 levels"
	}
}
