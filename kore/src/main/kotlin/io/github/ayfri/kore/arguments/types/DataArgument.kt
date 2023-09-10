package io.github.ayfri.kore.arguments.types

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.maths.Vec3
import io.github.ayfri.kore.arguments.types.literals.SelectorArgument
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.arguments.types.resources.StorageArgument
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
