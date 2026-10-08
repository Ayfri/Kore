package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.generated.arguments.PotionOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.InlinableListSerializer
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable


@Serializable(with = PotionContentsComponentMatcher.Companion.PotionContentsComponentMatcherSerializer::class)
data class PotionContentsComponentMatcher(
	@Serializable(PotionContentsListSerializer::class) var potions: InlinableList<PotionOrTagArgument> = emptyList(),
) : ComponentMatcher() {
	companion object {
		data object PotionContentsListSerializer : InlinableListSerializer<PotionOrTagArgument>(PotionOrTagArgument.serializer())

		data object PotionContentsComponentMatcherSerializer :
			InlineAutoSerializer<PotionContentsComponentMatcher, InlinableList<PotionOrTagArgument>>(
				PotionContentsListSerializer,
				PotionContentsComponentMatcher::potions,
				::PotionContentsComponentMatcher,
				"PotionContentsComponentMatcher",
			)
	}
}

fun DataComponentPredicate.potionContents(block: MutableList<PotionOrTagArgument>.() -> Unit) {
	matchers += PotionContentsComponentMatcher().apply { potions = buildList(block) }
}

fun DataComponentPredicate.potionContents(vararg potions: PotionOrTagArgument) = potionContents { addAll(potions) }
