package io.github.ayfri.kore.features.enchantments.values

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

@Serializable(with = Constant.Companion.ConstantSerializer::class)
data class Constant(var value: Int) : LevelBased() {
	companion object {
		data object ConstantSerializer :
			InlineAutoSerializer<Constant, Int>(serializer<Int>(), Constant::value, ::Constant)
	}
}

fun constantLevelBased(value: Int) = Constant(value)
