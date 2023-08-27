package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.CatVariantOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface CatVariantTagArgument : TaggedResourceLocationArgument, CatVariantOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : CatVariantTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
