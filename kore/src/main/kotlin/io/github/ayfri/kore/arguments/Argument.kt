package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(Argument.ArgumentSerializer::class)
interface Argument {
	fun asString(): String

	data object ArgumentSerializer : ToStringSerializer<Argument>({ asString() } )
}
