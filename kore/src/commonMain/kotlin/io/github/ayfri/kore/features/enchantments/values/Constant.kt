package io.github.ayfri.kore.features.enchantments.values

import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * The same [value] whatever the enchantment level is, serialized as a bare number instead of an object.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Enchantment_definition#constant
 *
 * @property value The number returned for every level.
 */
@Serializable(with = Constant.Companion.ConstantSerializer::class)
data class Constant(var value: Float) : LevelBased() {
	companion object {
		data object ConstantSerializer :
			InlineAutoSerializer<Constant, Float>(serializer<Float>(), Constant::value, ::Constant)
	}
}

/** Creates a [Constant] returning [value] for every enchantment level. */
fun LevelBasedScope.constantLevelBased(value: Float) = Constant(value)

/** Creates a [Constant] returning [value] for every enchantment level. */
fun LevelBasedScope.constantLevelBased(value: Int) = Constant(value.toFloat())
