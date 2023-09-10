package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.StructureOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface StructureTagArgument : TaggedResourceLocationArgument, StructureOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : StructureTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
