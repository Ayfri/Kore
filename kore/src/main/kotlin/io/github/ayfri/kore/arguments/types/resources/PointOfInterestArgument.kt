package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface PointOfInterestArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(pointOfInterest: String, namespace: String = "minecraft") = object : PointOfInterestArgument {
			override val name = pointOfInterest
			override val namespace = namespace
		}
	}
}
