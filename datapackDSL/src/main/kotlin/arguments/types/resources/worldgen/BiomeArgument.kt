package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.BiomeOrTagArgument
import arguments.types.ResourceLocationArgument
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
