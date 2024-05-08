package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import io.github.ayfri.kore.utils.nbt
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder

@Serializable(with = BucketEntityDataComponent.Companion.BucketEntityDataComponentSerializer::class)
data class BucketEntityDataComponent(var data: NbtCompound) : Component() {
	companion object {
		object BucketEntityDataComponentSerializer : InlineSerializer<BucketEntityDataComponent, NbtCompound>(
			NbtCompound.serializer(),
			BucketEntityDataComponent::data
		)
	}
}

fun ComponentsScope.bucketEntityData(data: NbtCompound) = apply {
	this[ComponentTypes.BUCKET_ENTITY_DATA] = BucketEntityDataComponent(data)
}

fun ComponentsScope.bucketEntityData(block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ComponentTypes.BUCKET_ENTITY_DATA] = BucketEntityDataComponent(nbt(block))
}
