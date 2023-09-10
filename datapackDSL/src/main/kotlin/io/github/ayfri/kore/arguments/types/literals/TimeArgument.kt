package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.TimeNumber
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class TimeArgument(val value: TimeNumber) : Argument {
	override fun asString() = value.toString()
}

fun time(value: TimeNumber) = TimeArgument(value)
