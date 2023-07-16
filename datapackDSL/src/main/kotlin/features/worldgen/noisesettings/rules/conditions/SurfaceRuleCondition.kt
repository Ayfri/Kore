package features.worldgen.noisesettings.rules.conditions

import kotlinx.serialization.Serializable
import serializers.NamespacePolymorphicSerializer

@Serializable(with = SurfaceRuleCondition.Companion.SurfaceRuleConditionSerializer::class)
sealed class SurfaceRuleCondition {
	companion object {
		data object SurfaceRuleConditionSerializer : NamespacePolymorphicSerializer<SurfaceRuleCondition>(SurfaceRuleCondition::class)
	}
}
