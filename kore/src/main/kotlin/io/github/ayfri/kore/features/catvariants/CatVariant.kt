package io.github.ayfri.kore.features.catvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.arguments.types.CatVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven cat variant definition.
 *
 * Cat variants are used to define the appearance of cats in the world.
 * They can be associated with a biome and are used to create different colored cats in the world.
 *
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Cat
 */
@Serializable
data class CatVariant(
	@Transient
	override var fileName: String = "cat_variant",
	/** The texture asset to use for the cat variant. */
	var assetId: ModelArgument,
	/** The spawn conditions for this cat variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList()
) : Generator("cat_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the spawn conditions for this cat variant. */
fun CatVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply { spawnConditions = SpawnConditions().apply(block).entries }

/**
 * Create and register a cat variant in this [DataPack].
 *
 * Produces `data/<namespace>/cat_variant/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Cat#Variants
 */
fun DataPack.catVariant(
	fileName: String = "cat_variant",
	assetId: ModelArgument,
	spawnConditions: List<VariantSpawnEntry> = emptyList(),
	init: CatVariant.() -> Unit = {},
): CatVariantArgument {
	val catVariant = CatVariant(fileName, assetId, spawnConditions).apply(init)
	catVariants += catVariant
	return CatVariantArgument(fileName, catVariant.namespace ?: name)
}
