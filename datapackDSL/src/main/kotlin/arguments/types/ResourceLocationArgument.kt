package arguments.types

import arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ResourceLocationArgument : Argument {
	val name: String
	val namespace: String

	fun asId() = "$namespace:$name"
	override fun asString() = asId()
}
