package arguments.types.resources

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemModifierArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(itemModifier: String, namespace: String = "minecraft") = object : ItemModifierArgument {
			override val name = itemModifier
			override val namespace = namespace
		}
	}
}
