package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.serializers.splitNamespacedId
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.set
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtString

/**
 * Represents the `minecraft:block_entity_data` item component, which attaches custom NBT data to a block entity when the item is placed.
 *
 * The `id` must match the block entity type or the data is dropped on placement.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#block_entity_data
 */
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

			override fun deserialize(decoder: Decoder): BlockEntityDataComponent {
				val compound = decoder.decodeSerializableValue(NbtAsJsonSerializer) as NbtCompound
				val (name, namespace) = (compound.getValue("id") as NbtString).value.splitNamespacedId()
				val data = NbtCompound(compound.filterKeys { it != "id" })
				return BlockEntityDataComponent(BlockArgument(name, namespace), data.takeUnless(NbtCompound::isEmpty))
			}
		}
	}
}

/** Attaches custom NBT data to a block entity when the item is placed. */
fun ComponentsScope.blockEntityData(id: BlockArgument, data: NbtCompound? = null) = apply {
	this[ItemComponentTypes.BLOCK_ENTITY_DATA] = BlockEntityDataComponent(id, data)
}

fun ComponentsScope.blockEntityData(id: BlockArgument, block: NbtCompoundBuilder.() -> Unit) = apply {
	this[ItemComponentTypes.BLOCK_ENTITY_DATA] = BlockEntityDataComponent(id, nbt(block))
}
