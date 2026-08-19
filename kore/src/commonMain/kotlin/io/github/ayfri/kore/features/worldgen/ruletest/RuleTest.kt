package io.github.ayfri.kore.features.worldgen.ruletest

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * A test on a block, used to decide whether a structure processor rule or an ore target applies to it.
 *
 * Every builder is an extension on [RuleTestScope], so they only resolve inside a block accepting a rule test, such
 * as `rule { }` or `target { }`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Processor_list#Rule_test
 */
@GeneratedSealedSerializer
@Serializable(with = RuleTest.Companion.RuleTestSerializer::class)
sealed class RuleTest {
	companion object {
		@OptIn(InternalSerializationApi::class)
		data object RuleTestSerializer :
			NamespacedPolymorphicSerializer<RuleTest>(ruleTestSealedSerializer(), outputName = "predicate_type")
	}
}
