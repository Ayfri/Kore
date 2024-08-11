package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.TrimMaterialOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface TrimMaterialTagArgument : TaggedResourceLocationArgument, TrimMaterialOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : TrimMaterialTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
