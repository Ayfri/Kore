package features.worldgen.ruletest

import serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RuleTest.Companion.RuleTestSerializer::class)
sealed class RuleTest {
	companion object {
		data object RuleTestSerializer : NamespacedPolymorphicSerializer<RuleTest>(RuleTest::class, outputName = "predicate_type")
	}
}
