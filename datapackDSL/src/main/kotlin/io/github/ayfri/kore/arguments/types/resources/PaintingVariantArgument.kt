package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.PaintingVariantOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PaintingVariantArgument : ResourceLocationArgument, PaintingVariantOrTagArgument {
	companion object {
		operator fun invoke(paintingVariant: String, namespace: String = "minecraft") = object : PaintingVariantArgument {
			override val name = paintingVariant
			override val namespace = namespace
		}
	}
}
