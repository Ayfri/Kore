package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.inlinableListSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PotionContentsComponentMatcher.Companion.PotionContentsComponentMatcherSerializer::class)
data class PotionContentsComponentMatcher(
	var potions: InlinableList<MobEffectOrTagArgument> = emptyList(),
) : ComponentMatcher() {
	companion object {
		data object PotionContentsComponentMatcherSerializer : InlineSerializer<PotionContentsComponentMatcher, List<MobEffectOrTagArgument>>(
			inlinableListSerializer(MobEffectOrTagArgument.serializer()),
			PotionContentsComponentMatcher::potions
		)
	}
}

fun ItemStackSubPredicates.potionContents(block: MutableList<MobEffectOrTagArgument>.() -> Unit) {
	matchers += PotionContentsComponentMatcher().apply { potions = buildList(block) }
}

fun ItemStackSubPredicates.potionContents(vararg potions: MobEffectOrTagArgument) = potionContents { addAll(potions) }
