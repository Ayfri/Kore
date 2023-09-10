package io.github.ayfri.kore.commands

import io.github.ayfri.kore.arguments.types.literals.bool
import io.github.ayfri.kore.arguments.types.literals.int
import io.github.ayfri.kore.arguments.types.literals.literal
import io.github.ayfri.kore.functions.Function
import io.github.ayfri.kore.generated.Gamerules
import io.github.ayfri.kore.utils.asArg

fun Function.gamerule(rule: String, value: Boolean? = null) = addLine(command("gamerule", literal(rule), bool(value)))
fun Function.gamerule(rule: String, value: Int) = addLine(command("gamerule", literal(rule), int(value)))
fun Function.gamerule(rule: Gamerules.Int, value: Int? = null) = addLine(command("gamerule", literal(rule.asArg()), int(value)))
fun Function.gamerule(rule: Gamerules.Boolean, value: Boolean? = null) = addLine(command("gamerule", literal(rule.asArg()), bool(value)))
