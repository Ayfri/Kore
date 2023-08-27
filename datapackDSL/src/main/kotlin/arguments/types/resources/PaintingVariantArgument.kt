package arguments.types.resources

import arguments.Argument
import arguments.types.PaintingVariantOrTagArgument
import arguments.types.ResourceLocationArgument
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
