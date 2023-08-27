package arguments.types.resources

import arguments.Argument
import arguments.types.FunctionOrTagArgument
import arguments.types.ResourceLocationArgument
import utils.ifNotEmpty
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FunctionArgument : ResourceLocationArgument, FunctionOrTagArgument {
	var directory: String

	override fun asId() = "$namespace:${directory.ifNotEmpty { "$it/" }}$name"

	companion object {
		operator fun invoke(function: String, namespace: String = "minecraft", directory: String = "") = object : FunctionArgument {
			override val name = function
			override val namespace = namespace
			override var directory = directory
		}
	}
}
