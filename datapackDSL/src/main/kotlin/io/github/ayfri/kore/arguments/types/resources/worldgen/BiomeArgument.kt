package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomeArgument : ResourceLocationArgument, BiomeOrTagArgument {
	companion object {
		operator fun invoke(biome: String, namespace: String = "minecraft") = object : BiomeArgument {
			override val name = biome
			override val namespace = namespace
		}
	}
}
