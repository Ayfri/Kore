package commands

import functions.Function
import utils.assertsIs

fun Function.returnCommand() {
	returnValue(0) assertsIs "return 0"
}
