package commands

import arguments.maths.Vec3
import arguments.types.literals.allEntities
import arguments.types.literals.self
import assertions.assertsIs
import functions.Function
import generated.DamageTypes

fun Function.damageTests() {
	damages(allEntities(true)) {
		apply(1f) assertsIs "damage @e[limit=1] 1"
		apply(1f, DamageTypes.ARROW) assertsIs "damage @e[limit=1] 1 minecraft:arrow"
		applyAt(1f, DamageTypes.FALL, Vec3(0, 0, 0)) assertsIs "damage @e[limit=1] 1 minecraft:fall at 0 0 0"
		applyBy(1f, DamageTypes.BAD_RESPAWN_POINT, self()) assertsIs "damage @e[limit=1] 1 minecraft:bad_respawn_point by @s"
		applyBy(1f, DamageTypes.EXPLOSION, self(), self()) assertsIs "damage @e[limit=1] 1 minecraft:explosion by @s from @s"
	}
}
