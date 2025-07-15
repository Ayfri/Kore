package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.PigVariantOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PigVariantTagArgument : TaggedResourceLocationArgument, PigVariantOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : PigVariantTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
