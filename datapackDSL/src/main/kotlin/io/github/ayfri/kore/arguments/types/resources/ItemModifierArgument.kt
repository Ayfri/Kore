package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
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
