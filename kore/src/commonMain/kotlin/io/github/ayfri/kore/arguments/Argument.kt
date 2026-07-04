package io.github.ayfri.kore.arguments

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(Argument.ArgumentSerializer::class)
interface Argument {
	fun asString(): String

	data object ArgumentSerializer : ToStringSerializer<Argument>(
		transform = { asString() },
		fromString = ::createArgumentProxy
	)

	companion object {
		fun createArgumentProxy(value: String) = createArgumentProxyInternal(value)

		internal fun parse(value: String) = parseArgument(value)
	}
}
