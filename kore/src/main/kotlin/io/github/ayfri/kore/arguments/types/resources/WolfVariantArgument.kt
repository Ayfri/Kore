package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.WolfVariantOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface WolfVariantArgument : ResourceLocationArgument, WolfVariantOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : WolfVariantArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
