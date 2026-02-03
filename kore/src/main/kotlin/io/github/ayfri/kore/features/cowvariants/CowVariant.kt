package io.github.ayfri.kore.features.cowvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.arguments.types.CowVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven cow variant definition.
 *
 * A cow variant specifies a unique model and texture for cows. It can be associated with a biome
 * and is used to create different colored cows in the world.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Cow
 */
@Serializable
data class CowVariant(
	@Transient
	override var fileName: String = "cow_variant",
	/** The texture asset to use for the cow variant. */
	var assetId: ModelArgument,
	/** The model to use for the cow variant. */
	var model: CowModel,
	/** The spawn conditions for this cow variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList(),
) : Generator("cow_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}


/** Sets the spawn conditions for this cow variant. */
fun CowVariant.spawnConditions(block: SpawnConditions.() -> Unit) = apply {
	spawnConditions = SpawnConditions().apply(block).entries
}


/**
 * Create and register a cow variant in this [DataPack].
 *
 * Produces `data/<namespace>/cow_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Cow#Variants
 */
fun DataPack.cowVariant(
	fileName: String = "cow_variant",
	assetId: ModelArgument,
	model: CowModel,
	block: CowVariant.() -> Unit = {}
): CowVariantArgument {
	val cowVariant = CowVariant(fileName, assetId, model).apply(block)
	cowVariants += cowVariant
	return CowVariantArgument(fileName, cowVariant.namespace ?: name)
}
