package io.github.ayfri.kore.features.trimmaterial

import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ArmorMaterial.Companion.ArmorMaterialSerializer::class)
enum class ArmorMaterial {
	LEATHER,
	CHAINMAIL,
	IRON,
	GOLD,
	DIAMOND,
	NETHERITE,
	TURTLE;

	override fun toString() = "minecraft:${name.lowercase()}"

	companion object {
		data object ArmorMaterialSerializer : ToStringSerializer<ArmorMaterial>()
	}
}
