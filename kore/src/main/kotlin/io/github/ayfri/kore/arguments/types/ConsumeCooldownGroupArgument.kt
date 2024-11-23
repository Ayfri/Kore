package io.github.ayfri.kore.arguments.types

import io.github.ayfri.kore.arguments.Argument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ConsumeCooldownGroupArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : ConsumeCooldownGroupArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
