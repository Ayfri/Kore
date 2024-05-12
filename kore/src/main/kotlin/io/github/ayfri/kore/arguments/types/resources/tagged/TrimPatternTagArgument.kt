package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.TrimPatternOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface TrimPatternTagArgument : ResourceLocationArgument, TrimPatternOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : TrimPatternTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
