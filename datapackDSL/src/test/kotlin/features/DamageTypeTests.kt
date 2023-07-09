package features

import DataPack
import arguments.self
import commands.damage
import features.damagetypes.damageType
import features.damagetypes.types.DeathMessageType
import features.damagetypes.types.Effects
import features.damagetypes.types.Scaling
import functions.load

fun DataPack.damageTypeTests() {
	val damageType = damageType("test_damage_type", "test_message_id", Scaling.NEVER) {
		exhaustion = 1f
		effects = Effects.BURNING
		deathMessageType = DeathMessageType.DEFAULT
	}

	load {
		damage(self(), 1f, damageType)
	}
}
