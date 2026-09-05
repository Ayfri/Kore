package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.generated.arguments.VillagerTypeOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Tests the `minecraft:villager/variant` component against a set of villager types.
 *
 * Minecraft Wiki: [Predicate](https://minecraft.wiki/w/Predicate)
 */
@Serializable(with = VillagerVariantComponentMatcher.Companion.Serializer::class)
data class VillagerVariantComponentMatcher(
	var variants: InlinableList<VillagerTypeOrTagArgument> = emptyList(),
) : ComponentMatcher() {
	override val componentName get() = "villager/variant"

	companion object {
		data object Serializer : InlineAutoSerializer<VillagerVariantComponentMatcher, InlinableList<VillagerTypeOrTagArgument>>(
			InlinableListSerializer(serializer<VillagerTypeOrTagArgument>()),
			VillagerVariantComponentMatcher::variants,
			::VillagerVariantComponentMatcher,
			"villager/variant",
		)
	}
}

/** Tests the villager type of the entity against any of [variants]. */
fun DataComponentPredicate.villagerVariant(vararg variants: VillagerTypeOrTagArgument) {
	matchers += VillagerVariantComponentMatcher(variants.toList())
}
