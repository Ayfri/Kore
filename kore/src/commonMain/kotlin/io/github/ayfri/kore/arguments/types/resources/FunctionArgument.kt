package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.FunctionOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.utils.ifNotEmpty
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
