package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.PointOfInterestOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PointOfInterestTagArgument : TaggedResourceLocationArgument, PointOfInterestOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : PointOfInterestTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
