package io.github.ayfri.kore.features.worldgen.noisesettings.rules.conditions

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = SurfaceRuleCondition.Companion.SurfaceRuleConditionSerializer::class)
sealed class SurfaceRuleCondition {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object SurfaceRuleConditionSerializer :
			NamespacedPolymorphicSerializer<SurfaceRuleCondition>(surfaceRuleConditionSealedSerializer())
	}
}
