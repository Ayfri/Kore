package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.colors.BossBarColor
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.bossBarTests() {
	bossBars.add("bar", displayName = "foo") assertsIs "bossbar add minecraft:bar \"foo\""
	bossBars.list() assertsIs "bossbar list"
	bossBars.get("bar", "foo").remove() assertsIs "bossbar remove foo:bar"

	bossBar("bar", "foo") {
		add("bar") assertsIs "bossbar add foo:bar \"bar\""

		getMax() assertsIs "bossbar get foo:bar max"
		getPlayers() assertsIs "bossbar get foo:bar players"
		getValue() assertsIs "bossbar get foo:bar value"
		getVisible() assertsIs "bossbar get foo:bar visible"

		setColor(BossBarColor.BLUE) assertsIs "bossbar set foo:bar color blue"
		setMax(1) assertsIs "bossbar set foo:bar max 1"
		setName("bar") assertsIs "bossbar set foo:bar name bar"
		setPlayers(self()) assertsIs "bossbar set foo:bar players @s"
		setStyle(BossBarStyle.NOTCHED_6) assertsIs "bossbar set foo:bar style notched_6"
		setValue(1) assertsIs "bossbar set foo:bar value 1"
		setVisible(true) assertsIs "bossbar set foo:bar visible true"
	}
}
