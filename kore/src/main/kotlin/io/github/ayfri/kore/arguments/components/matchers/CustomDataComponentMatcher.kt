package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtTag

@Serializable(with = CustomDataComponentMatcher.Companion.CustomDataComponentMatcherSerializer::class)
data class CustomDataComponentMatcher(
	var data: @Serializable(with = NbtAsJsonSerializer::class) NbtTag,
) : ComponentMatcher() {
	companion object {
		data object CustomDataComponentMatcherSerializer : InlineSerializer<CustomDataComponentMatcher, NbtTag>(
			NbtAsJsonSerializer,
			CustomDataComponentMatcher::data,
		)
	}
}

fun ItemStackSubPredicates.customData(init: NbtCompoundBuilder.() -> Unit) =
	apply { matchers += CustomDataComponentMatcher(nbt(init)) }

fun ItemStackSubPredicates.customData(string: String) = apply { matchers += CustomDataComponentMatcher(string.nbt) }
