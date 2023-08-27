package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.BannerPatternOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BannerPatternTagArgument : BannerPatternOrTagArgument, TaggedResourceLocationArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : BannerPatternTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
