package commands

import arguments.types.literals.int
import arguments.types.literals.literal
import arguments.types.resources.FunctionArgument
import functions.Function

fun Function.returnRun(function: FunctionArgument) = addLine(command("return", literal("run"), function))

fun Function.returnValue(value: Int) = addLine(command("return", int(value)))
