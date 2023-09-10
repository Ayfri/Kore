package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.BannerPatternOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
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
