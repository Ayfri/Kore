package io.github.ayfri.kore.commands

import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.functions.Function

fun Function.worldBorderTests() {
	worldBorder {
		add(1.0) assertsIs "worldborder add 1"
		add(1.0, 5) assertsIs "worldborder add 1 5"
		center(1.0, 2.0) assertsIs "worldborder center 1 2"
		damageAmount(1f) assertsIs "worldborder damage amount 1"
		damageBuffer(1.0) assertsIs "worldborder damage buffer 1"
		damagePerBlock(1f) assertsIs "worldborder damage amount 1"
		get() assertsIs "worldborder get"
		set(1.0) assertsIs "worldborder set 1"
		set(1.0, 5) assertsIs "worldborder set 1 5"
		setWarningDistance(1) assertsIs "worldborder warning distance 1"
		setWarningTime(1) assertsIs "worldborder warning time 1"
	}

	worldBorder.get() assertsIs "worldborder get"
	worldBorder() assertsIs "worldborder get"
}
