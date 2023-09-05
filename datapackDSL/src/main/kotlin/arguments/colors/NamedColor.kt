package arguments.colors

import serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = NamedColor.Companion.NamedColorSerializer::class)
abstract class NamedColor(val name: String) : Color {
	override fun toString() = name

	companion object {
		data object NamedColorSerializer : ToStringSerializer<NamedColor>()
	}
}
