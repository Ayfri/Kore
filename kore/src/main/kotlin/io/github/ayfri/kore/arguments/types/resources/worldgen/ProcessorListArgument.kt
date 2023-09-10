package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ProcessorListArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(feature: String, namespace: String = "minecraft") = object : ProcessorListArgument {
			override val name = feature
			override val namespace = namespace
		}
	}
}
