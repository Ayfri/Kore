package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.BiomeOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomeTagArgument : TaggedResourceLocationArgument, BiomeOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : BiomeTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
