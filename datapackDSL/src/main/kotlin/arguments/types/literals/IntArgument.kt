package arguments.types.literals

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
data class IntArgument(val value: Long) : Argument {
	override fun asString() = value.toString()
}

fun int(value: Long) = IntArgument(value)
fun int(value: Int) = IntArgument(value.toLong())
internal fun int(value: Int?) = value?.let { IntArgument(it.toLong()) }
internal fun int(value: Long?) = value?.let { IntArgument(it) }
