package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.PigVariantOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PigVariantArgument : ResourceLocationArgument, PigVariantOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : PigVariantArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
