package commands

import arguments.types.literals.bool
import arguments.types.literals.int
import arguments.types.literals.literal
import functions.Function
import generated.Gamerules
import utils.asArg

fun Function.gamerule(rule: String, value: Boolean? = null) = addLine(command("gamerule", literal(rule), bool(value)))
fun Function.gamerule(rule: String, value: Int) = addLine(command("gamerule", literal(rule), int(value)))
fun Function.gamerule(rule: Gamerules.Int, value: Int? = null) = addLine(command("gamerule", literal(rule.asArg()), int(value)))
fun Function.gamerule(rule: Gamerules.Boolean, value: Boolean? = null) = addLine(command("gamerule", literal(rule.asArg()), bool(value)))
