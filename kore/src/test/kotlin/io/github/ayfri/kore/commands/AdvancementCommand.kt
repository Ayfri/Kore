package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Advancements

fun Function.advancementTests() {
	advancement {
		grantEverything(self()) assertsIs "advancement grant @s everything"
		revokeEverything(self()) assertsIs "advancement revoke @s everything"

		grant(
			self(),
			AdvancementRoute.ONLY,
			Advancements.Adventure.KILL_A_MOB,
			"test"
		) assertsIs "advancement grant @s only minecraft:adventure/kill_a_mob test"

		revoke(
			self(),
			AdvancementRoute.ONLY,
			Advancements.Adventure.KILL_A_MOB
		) assertsIs "advancement revoke @s only minecraft:adventure/kill_a_mob"

		grant(self(), Advancements.Adventure.KILL_A_MOB) assertsIs "advancement grant @s only minecraft:adventure/kill_a_mob"
		revoke(self(), Advancements.Adventure.KILL_A_MOB) assertsIs "advancement revoke @s only minecraft:adventure/kill_a_mob"
	}

	advancement(self()) {
		grantEverything() assertsIs "advancement grant @s everything"
		revokeEverything() assertsIs "advancement revoke @s everything"

		grant(
			AdvancementRoute.ONLY,
			Advancements.Adventure.KILL_A_MOB,
			"test"
		) assertsIs "advancement grant @s only minecraft:adventure/kill_a_mob test"

		grant(AdvancementRoute.ONLY, Advancements.Adventure.KILL_A_MOB) assertsIs "advancement grant @s only minecraft:adventure/kill_a_mob"
		revoke(Advancements.Adventure.KILL_A_MOB) assertsIs "advancement revoke @s only minecraft:adventure/kill_a_mob"
		revoke(Advancements.Adventure.KILL_A_MOB) assertsIs "advancement revoke @s only minecraft:adventure/kill_a_mob"
	}
}
