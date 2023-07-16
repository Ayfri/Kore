package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable
import serializers.NamespacedPolymorphicSerializer

@Serializable(with = SurfaceRuleCondition.Companion.SurfaceRuleConditionSerializer::class)
sealed class SurfaceRuleCondition {
	companion object {
		data object SurfaceRuleConditionSerializer : NamespacedPolymorphicSerializer<SurfaceRuleCondition>(SurfaceRuleCondition::class)
	}
}
