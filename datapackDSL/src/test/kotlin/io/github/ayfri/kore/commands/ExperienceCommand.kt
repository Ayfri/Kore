package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.numbers.levels
import io.github.ayfri.kore.arguments.numbers.points
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

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
