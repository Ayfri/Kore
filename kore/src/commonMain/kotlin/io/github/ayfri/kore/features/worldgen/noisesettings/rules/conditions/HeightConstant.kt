package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = HeightConstant.Companion.HeightConstantSerializer::class)
sealed class HeightConstant {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object HeightConstantSerializer :
			NamespacedPolymorphicSerializer<HeightConstant>(heightConstantSealedSerializer(), skipOutputName = true)
	}
}

/**
 * A height expressed as an absolute Y coordinate.
 *
 * @property absolute The Y coordinate.
 */
@Serializable
data class Absolute(var absolute: Int) : HeightConstant()

/**
 * A height expressed relative to the bottom of the world.
 *
 * @property aboveBottom The number of blocks above the bottom of the world.
 */
@Serializable
data class AboveBottom(var aboveBottom: Int) : HeightConstant()

/**
 * A height expressed relative to the top of the world.
 *
 * @property belowTop The number of blocks below the top of the world.
 */
@Serializable
data class BelowTop(var belowTop: Int) : HeightConstant()

/** Creates an [Absolute] height at the given Y coordinate. */
fun absolute(absolute: Int) = Absolute(absolute)

/** Creates an [AboveBottom] height, [aboveBottom] blocks above the bottom of the world. */
fun aboveBottom(aboveBottom: Int) = AboveBottom(aboveBottom)

/** Creates a [BelowTop] height, [belowTop] blocks below the top of the world. */
fun belowTop(belowTop: Int) = BelowTop(belowTop)
