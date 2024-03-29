package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

@Serializable(with = BlockEntityDataComponent.Companion.BlockEntityDataComponentSerializer::class)
data class BlockEntityDataComponent(
	var id: BlockArgument,
	var data: NbtCompound? = null,
) : Component() {
	companion object {
		object BlockEntityDataComponentSerializer : KSerializer<BlockEntityDataComponent> {
			override val descriptor = NbtCompound.serializer().descriptor

			override fun serialize(encoder: Encoder, value: BlockEntityDataComponent) = when (encoder) {
				is NbtEncoder -> {
					encoder.encodeNbtTag(nbt {
						this["id"] = value.id.asId()
						value.data?.forEach { (key, value) -> this[key] = value }
					})
				}

				is JsonEncoder -> {
					encoder.encodeJsonElement(buildJsonObject {
						val data = value.data ?: nbt {}
						val dataAsJson = encoder.json.encodeToJsonElement(NbtAsJsonSerializer, data).jsonObject

						put("id", value.id.asId())
						dataAsJson.forEach { (key, value) -> put(key, value) }
					})
				}

				else -> error("BlockEntityDataComponent is not serializable.")
			}

			override fun deserialize(decoder: Decoder) = error("BlockEntityDataComponent is not deserializable.")
		}
	}
}

fun Components.blockEntityData(id: BlockArgument, data: NbtCompound? = null) = apply {
	this[ComponentTypes.BLOCK_ENTITY_DATA] = BlockEntityDataComponent(id, data)
}

fun Components.blockEntityData(id: BlockArgument, block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ComponentTypes.BLOCK_ENTITY_DATA] = BlockEntityDataComponent(id, nbt(block))
}
