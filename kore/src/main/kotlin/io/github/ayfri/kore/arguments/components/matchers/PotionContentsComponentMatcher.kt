package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.arguments.types.EffectOrTagArgument
import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PotionContentsComponentMatcher.Companion.PotionContentsComponentMatcherSerializer::class)
data class PotionContentsComponentMatcher(
	var potions: InlinableList<EffectOrTagArgument> = emptyList(),
) : ComponentMatcher() {
	companion object {
		data object PotionContentsComponentMatcherSerializer : InlineSerializer<PotionContentsComponentMatcher, List<EffectOrTagArgument>>(
			inlinableListSerializer(EffectOrTagArgument.serializer()),
			PotionContentsComponentMatcher::potions
		)
	}
}

fun ItemStackSubPredicates.potionContents(block: MutableList<EffectOrTagArgument>.() -> Unit) {
	matchers += PotionContentsComponentMatcher().apply { potions = buildList(block) }
}

fun ItemStackSubPredicates.potionContents(vararg potions: EffectOrTagArgument) = potionContents { addAll(potions) }
