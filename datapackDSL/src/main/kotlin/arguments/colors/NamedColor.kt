package arguments.colors

import kotlinx.serialization.Serializable
import serializers.ToStringSerializer

internal object NamedColorSerializer : ToStringSerializer<NamedColor>()

@Serializable(with = NamedColorSerializer::class)
open class NamedColor(val name: String) : Color {
	override fun toString() = name
}
