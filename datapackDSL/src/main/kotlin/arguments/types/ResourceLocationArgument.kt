package arguments.types

import arguments.Argument

interface ResourceLocationArgument : Argument {
	val name: String
	val namespace: String

	fun asId() = "$namespace:$name"
	override fun asString() = asId()
}
