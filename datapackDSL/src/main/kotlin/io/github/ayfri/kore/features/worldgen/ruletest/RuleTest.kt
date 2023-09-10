package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RuleTest.Companion.RuleTestSerializer::class)
sealed class RuleTest {
	companion object {
		data object RuleTestSerializer : NamespacedPolymorphicSerializer<RuleTest>(RuleTest::class, outputName = "predicate_type")
	}
}
