package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

@Serializable(BundleContentsComponent.Companion.BundleContentsSerializer::class)
data class BundleContentsComponent(
	val items: MutableList<ItemStack>,
) : Component() {
	companion object {
		object BundleContentsSerializer : InlineSerializer<BundleContentsComponent, List<ItemStack>>(
			ListSerializer(ItemStack.serializer()),
			BundleContentsComponent::items
		)
	}
}

fun ComponentsScope.bundleContents(items: List<ItemStack>) = apply {
	this[ItemComponentTypes.BUNDLE_CONTENTS] = BundleContentsComponent(items.toMutableList())
}

fun ComponentsScope.bundleContents(vararg items: ItemStack) = apply {
	this[ItemComponentTypes.BUNDLE_CONTENTS] = BundleContentsComponent(items.toMutableList())
}

fun ComponentsScope.bundleContents(block: BundleContentsComponent.() -> Unit) = apply {
	this[ItemComponentTypes.BUNDLE_CONTENTS] = BundleContentsComponent(mutableListOf()).apply(block)
}

fun ComponentsScope.bundleContent(id: ItemArgument, count: Short? = null, itemComponents: Components? = null) = apply {
	this[ItemComponentTypes.BUNDLE_CONTENTS] = BundleContentsComponent(mutableListOf(ItemStack(id.asId(), count, itemComponents)))
}

fun ComponentsScope.bundleContents(vararg id: ItemArgument) = apply {
	this[ItemComponentTypes.BUNDLE_CONTENTS] = BundleContentsComponent(id.map { ItemStack(it.asId()) }.toMutableList())
}

fun BundleContentsComponent.item(item: ItemStack) = apply {
	items += item
}

fun BundleContentsComponent.item(
	id: ItemArgument,
	count: Short? = null,
	components: Components? = null,
) = apply {
	items += ItemStack(id.asId(), count, components)
}

fun BundleContentsComponent.item(
	id: ItemArgument,
	count: Short? = null,
	block: Components.() -> Unit,
) = apply {
	items += ItemStack(id.asId(), count, Components().apply(block))
}
