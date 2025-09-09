package io.github.ayfri.kore.arguments.types

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ResourceLocationArgument : Argument {
	val name: String
	val namespace: String

	fun asId() = "$namespace:$name"
	override fun asString() = asId()

	companion object {
		data object ResourceLocationArgumentSimpleSerializer : ToStringSerializer<ResourceLocationArgument>({
			"$namespace:${name.lowercase()}"
		})
	}
}
