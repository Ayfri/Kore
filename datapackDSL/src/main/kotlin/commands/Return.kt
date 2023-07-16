package commands

import arguments.types.literals.int
import functions.Function

fun Function.returnValue(value: Int) = addLine(command("return", int(value)))
