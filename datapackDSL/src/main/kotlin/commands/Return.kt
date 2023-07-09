package commands

import arguments.int
import functions.Function

fun Function.returnValue(value: Int) = addLine(command("return", int(value)))
