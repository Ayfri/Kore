package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ModelArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(model: String, namespace: String = "minecraft") = object : ModelArgument {
			override val name = model
			override val namespace = namespace
		}
	}
}

fun model(model: String, namespace: String = "minecraft") = ModelArgument(model, namespace) 