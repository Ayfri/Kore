package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.CatVariantOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface CatVariantArgument : ResourceLocationArgument, CatVariantOrTagArgument {
	companion object {
		operator fun invoke(catVariant: String, namespace: String = "minecraft") = object : CatVariantArgument {
			override val name = catVariant
			override val namespace = namespace
		}
	}
}
