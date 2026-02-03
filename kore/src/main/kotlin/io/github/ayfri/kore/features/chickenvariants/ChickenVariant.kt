package io.github.ayfri.kore.features.chickenvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.arguments.types.ChickenVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven chicken variant definition.
 *
 * A chicken variant specifies a unique model and texture for chickens. It can be associated with a biome
 * and is used to create different colored chickens in the world.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Chicken
 */
@Serializable
data class ChickenVariant(
	@Transient
	override var fileName: String = "chicken_variant",
	/** The texture asset to use for the chicken variant. */
	var assetId: ModelArgument,
	/** The model to use for the chicken variant. */
	var model: ChickenModel,
	/** The spawn conditions for this chicken variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList(),
) : Generator("chicken_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}


/** Sets the spawn conditions for this chicken variant. */
fun ChickenVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply {
	spawnConditions = SpawnConditions().apply(block).entries
}


/**
 * Create and register a chicken variant in this [DataPack].
 *
 * Produces `data/<namespace>/chicken_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Chicken#Variants
 */
fun DataPack.chickenVariant(
	fileName: String = "chicken_variant",
	assetId: ModelArgument,
	model: ChickenModel,
	block: ChickenVariant.() -> Unit = {}
): ChickenVariantArgument {
	val chickenVariant = ChickenVariant(fileName, assetId, model).apply(block)
	chickenVariants += chickenVariant
	return ChickenVariantArgument(fileName, chickenVariant.namespace ?: name)
}
