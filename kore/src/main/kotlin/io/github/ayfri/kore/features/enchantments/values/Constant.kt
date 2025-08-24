package io.github.ayfri.kore.features.enchantments.values

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = Constant.Companion.ConstantSerializer::class)
data class Constant(var value: Int) : LevelBased() {
	companion object {
		data object ConstantSerializer : InlineAutoSerializer<Constant>(Constant::class)
	}
}

fun constantLevelBased(value: Int) = Constant(value)
