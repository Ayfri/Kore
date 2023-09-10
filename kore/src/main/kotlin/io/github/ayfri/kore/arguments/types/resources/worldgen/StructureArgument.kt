package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.StructureOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface StructureArgument : ResourceLocationArgument, StructureOrTagArgument {
	companion object {
		operator fun invoke(structure: String, namespace: String = "minecraft") = object : StructureArgument {
			override val name = structure
			override val namespace = namespace
		}
	}
}
