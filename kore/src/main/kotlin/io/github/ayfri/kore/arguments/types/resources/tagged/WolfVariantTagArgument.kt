package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.WolfVariantOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface WolfVariantTagArgument : TaggedResourceLocationArgument, WolfVariantOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : WolfVariantTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
