package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.data.item.ItemStack
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

fun Components.bundleContents(items: List<ItemStack>) = apply {
	components["bundle_contents"] = BundleContentsComponent(items.toMutableList())
}

fun Components.bundleContents(vararg items: ItemStack) = apply {
	components["bundle_contents"] = BundleContentsComponent(items.toMutableList())
}

fun Components.bundleContents(block: BundleContentsComponent.() -> Unit) = apply {
	components["bundle_contents"] = BundleContentsComponent(mutableListOf()).apply(block)
}

fun Components.bundleContent(id: ItemArgument, count: Short? = null, itemComponents: Components? = null) = apply {
	components["bundle_contents"] = BundleContentsComponent(mutableListOf(ItemStack(id.asId(), count, itemComponents)))
}

fun Components.bundleContents(vararg id: ItemArgument) = apply {
	components["bundle_contents"] = BundleContentsComponent(id.map { ItemStack(it.asId()) }.toMutableList())
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
