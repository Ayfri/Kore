package commands

import arguments.types.literals.allEntities
import arguments.types.literals.self
import assertions.assertsIs
import functions.Function

fun Function.rideTests() {
	ride(allEntities(true)) {
		mount(self()) assertsIs "ride @e[limit=1] mount @s"
		dismount() assertsIs "ride @e[limit=1] dismount"
	}
}
