package io.github.ayfri.kore.features

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.arguments.types.literals.self
import io.github.ayfri.kore.assertions.assertsIs
import io.github.ayfri.kore.commands.damage
import io.github.ayfri.kore.features.damagetypes.damageType
import io.github.ayfri.kore.features.damagetypes.types.DeathMessageType
import io.github.ayfri.kore.features.damagetypes.types.Effects
import io.github.ayfri.kore.features.damagetypes.types.Scaling
import io.github.ayfri.kore.functions.load

fun DataPack.damageTypeTests() {
	val damageType = damageType("test_damage_type", "test_message_id", Scaling.NEVER) {
		exhaustion = 1f
		effects = Effects.BURNING
		deathMessageType = DeathMessageType.DEFAULT
	}
	damageTypes.last() assertsIs """
		{
			"message_id": "test_message_id",
			"exhaustion": 1.0,
			"scaling": "never",
			"effects": "burning",
			"death_message_type": "default"
		}
	""".trimIndent()

	load {
		damage(self(), 1f, damageType)
	}
}
