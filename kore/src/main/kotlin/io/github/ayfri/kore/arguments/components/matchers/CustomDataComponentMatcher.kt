package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

@Serializable
data class CustomDataComponentMatcher(
	var data: @Serializable(with = NbtAsJsonSerializer::class) NbtCompound? = null,
) : ComponentMatcher()

fun ItemStackSubPredicates.customData(init: CustomDataComponentMatcher.() -> Unit) =
	apply { matchers += CustomDataComponentMatcher().apply(init) }

fun CustomDataComponentMatcher.data(init: NbtCompoundBuilder.() -> Unit) = apply {
	data = nbt(init)
}
