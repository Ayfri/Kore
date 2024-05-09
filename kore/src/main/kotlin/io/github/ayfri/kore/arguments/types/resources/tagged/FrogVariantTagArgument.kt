package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.FrogVariantOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FrogVariantTagArgument : TaggedResourceLocationArgument, FrogVariantOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : FrogVariantTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
