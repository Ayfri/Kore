package features.worldgen.noisesettings.rules

import kotlinx.serialization.Serializable
import serializers.NamespacePolymorphicSerializer

@Serializable(with = SurfaceRule.Companion.SurfaceRuleSerializer::class)
sealed class SurfaceRule {
	companion object {
		data object SurfaceRuleSerializer : NamespacePolymorphicSerializer<SurfaceRule>(SurfaceRule::class)
	}
}
