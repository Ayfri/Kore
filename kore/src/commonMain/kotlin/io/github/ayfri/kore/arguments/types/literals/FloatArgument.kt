package io.github.ayfri.kore.arguments.types.literals

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.numbers.toStringTruncatedIfRound
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class FloatArgument(val value: Double) : Argument {
	override fun asString() = value.toStringTruncatedIfRound()
}

fun float(value: Double) = FloatArgument(value)

/** Widens through the shortest decimal form, `0.2f.toDouble()` being `0.20000000298023224`. */
fun float(value: Float) = FloatArgument(value.toString().toDouble())
internal fun float(value: Double?) = value?.let { FloatArgument(it) }
internal fun float(value: Float?) = value?.let { float(it) }
