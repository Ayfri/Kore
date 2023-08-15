package commands

import functions.Function
import utils.assertsIs

fun Function.triggerTests() {
	trigger("test") {
		add(1) assertsIs "trigger test add 1"
		set(1) assertsIs "trigger test set 1"
		remove(1) assertsIs "trigger test add -1"
	}
}