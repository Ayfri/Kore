package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DimensionTypeArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(dimension: String, namespace: String = "minecraft") = object : DimensionTypeArgument {
			override val name = dimension
			override val namespace = namespace
		}
	}
}
