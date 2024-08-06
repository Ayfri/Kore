package io.github.ayfri.kore.features.enchantment.values

import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = Constant.Companion.ConstantSerializer::class)
data class Constant(
	var value: Int,
) : LevelBased() {
	companion object {
		data object ConstantSerializer : InlineSerializer<Constant, Int>(Int.serializer(), Constant::value)
	}
}

fun constantLevelBased(value: Int) = Constant(value)
