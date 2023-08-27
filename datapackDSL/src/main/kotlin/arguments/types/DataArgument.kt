package arguments.types

import arguments.Argument
import arguments.maths.Vec3
import arguments.types.literals.SelectorArgument
import arguments.types.literals.UUIDArgument
import arguments.types.resources.StorageArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DataArgument : Argument {
	val literalName
		get() = when (this) {
			is Vec3 -> "block"
			is SelectorArgument, is UUIDArgument -> "entity"
			is StorageArgument -> "storage"
			else -> error("Unknown data argument type: $this")
		}
}
