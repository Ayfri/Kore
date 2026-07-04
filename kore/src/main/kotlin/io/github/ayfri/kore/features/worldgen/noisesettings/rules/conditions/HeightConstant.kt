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

@Serializable
data class Absolute(var absolute: Int) : HeightConstant()

@Serializable
data class AboveBottom(var aboveBottom: Int) : HeightConstant()

@Serializable
data class BelowTop(var belowTop: Int) : HeightConstant()

fun absolute(absolute: Int) = Absolute(absolute)
fun aboveBottom(aboveBottom: Int) = AboveBottom(aboveBottom)
fun belowTop(belowTop: Int) = BelowTop(belowTop)
