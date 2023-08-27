package arguments.types.resources

import arguments.Argument
import arguments.types.CatVariantOrTagArgument
import arguments.types.ResourceLocationArgument
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
