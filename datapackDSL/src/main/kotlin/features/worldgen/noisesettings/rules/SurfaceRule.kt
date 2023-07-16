package features.worldgen.noisesettings.rules

import kotlinx.serialization.Serializable
import serializers.NamespacedPolymorphicSerializer

@Serializable(with = SurfaceRule.Companion.SurfaceRuleSerializer::class)
sealed class SurfaceRule {
	companion object {
		data object SurfaceRuleSerializer : NamespacedPolymorphicSerializer<SurfaceRule>(SurfaceRule::class)
	}
}
