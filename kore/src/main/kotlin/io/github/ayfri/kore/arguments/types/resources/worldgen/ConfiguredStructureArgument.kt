package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ConfiguredStructureOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ConfiguredStructureArgument : ResourceLocationArgument, ConfiguredStructureOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : ConfiguredStructureArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
