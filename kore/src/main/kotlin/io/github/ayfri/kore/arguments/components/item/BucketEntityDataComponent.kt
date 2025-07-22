package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

@Serializable(with = BucketEntityDataComponent.Companion.BucketEntityDataComponentSerializer::class)
data class BucketEntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		object BucketEntityDataComponentSerializer : InlineSerializer<BucketEntityDataComponent, NbtCompound>(
			NbtAsJsonSerializer,
			BucketEntityDataComponent::data
		)
	}
}

fun ComponentsScope.bucketEntityData(data: NbtCompound) = apply {
	this[ItemComponentTypes.BUCKET_ENTITY_DATA] = BucketEntityDataComponent(data)
}

fun ComponentsScope.bucketEntityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ItemComponentTypes.BUCKET_ENTITY_DATA] = BucketEntityDataComponent(nbt(block))
}
