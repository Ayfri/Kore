package arguments.types.resources

import arguments.Argument
import arguments.types.BannerPatternOrTagArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BannerPatternArgument : ResourceLocationArgument, BannerPatternOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : BannerPatternArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
