package io.github.ayfri.kore.features.worldgen.environmentattributes

import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = EnvironmentAttributeModifier.Companion.AttributeModifierSerializer::class)
enum class EnvironmentAttributeModifier {
	ADD,
	ALPHA_BLEND,
	AND,
	MAXIMUM,
	MINIMUM,
	MULTIPLY,
	NAND,
	NOR,
	OR,
	OVERRIDE,
	SUBTRACT,
	XNOR,
	XOR,
	;

	companion object {
		data object AttributeModifierSerializer : LowercaseSerializer<EnvironmentAttributeModifier>(entries)
	}
}
