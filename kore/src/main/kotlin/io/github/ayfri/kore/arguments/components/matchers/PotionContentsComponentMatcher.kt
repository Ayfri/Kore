package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.MobEffectOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable


@Serializable(with = PotionContentsComponentMatcher.Companion.PotionContentsComponentMatcherSerializer::class)
data class PotionContentsComponentMatcher(
	@Serializable(PotionContentsListSerializer::class) var potions: InlinableList<MobEffectOrTagArgument> = emptyList(),
) : ComponentMatcher() {
	companion object {
		data object PotionContentsListSerializer : InlinableListSerializer<MobEffectOrTagArgument>(MobEffectOrTagArgument.serializer())

		data object PotionContentsComponentMatcherSerializer : InlineAutoSerializer<PotionContentsComponentMatcher>(
			PotionContentsComponentMatcher::class
		)
	}
}

fun ItemStackSubPredicates.potionContents(block: MutableList<MobEffectOrTagArgument>.() -> Unit) {
	matchers += PotionContentsComponentMatcher().apply { potions = buildList(block) }
}

fun ItemStackSubPredicates.potionContents(vararg potions: MobEffectOrTagArgument) = potionContents { addAll(potions) }
