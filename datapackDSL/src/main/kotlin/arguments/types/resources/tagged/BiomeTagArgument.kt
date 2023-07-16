package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.BiomeOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomeTagArgument : TaggedResourceLocationArgument, BiomeOrTagArgument {
	companion object {
		operator fun invoke(biomeOrTag: String, namespace: String = "minecraft") = object : BiomeTagArgument {
			override val name = biomeOrTag
			override val namespace = namespace
		}
	}
}
