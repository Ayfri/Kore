package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.PaintingVariantOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PaintingVariantTagArgument : TaggedResourceLocationArgument, PaintingVariantOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : PaintingVariantTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
