package arguments.types.literals

import arguments.Argument
import arguments.numbers.str
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class FloatArgument(val value: Double) : Argument {
	override fun asString() = value.str
}

fun float(value: Double) = FloatArgument(value)
fun float(value: Float) = FloatArgument(value.toDouble())
internal fun float(value: Double?) = value?.let { FloatArgument(it) }
internal fun float(value: Float?) = value?.let { FloatArgument(it.toDouble()) }
