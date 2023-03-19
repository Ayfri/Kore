package commands

import arguments.allEntities
import arguments.self
import functions.Function
import utils.assertsIs

fun Function.rideTests() {
	ride(allEntities(true)) {
		mount(self()) assertsIs "ride @e[limit=1] mount @s"
		dismount() assertsIs "ride @e[limit=1] dismount"
	}
}
