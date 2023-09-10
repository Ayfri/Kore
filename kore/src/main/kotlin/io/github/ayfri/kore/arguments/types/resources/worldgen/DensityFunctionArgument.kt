package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DensityFunctionArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(densityFunctionType: String, namespace: String = "minecraft") = object : DensityFunctionArgument {
			override val name = densityFunctionType
			override val namespace = namespace
		}
	}
}
