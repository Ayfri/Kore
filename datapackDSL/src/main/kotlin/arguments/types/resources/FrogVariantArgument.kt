package arguments.types.resources

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FrogVariantArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(frogVariant: String, namespace: String = "minecraft") = object : FrogVariantArgument {
			override val name = frogVariant
			override val namespace = namespace
		}
	}
}
