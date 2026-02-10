package io.github.ayfri.kore.features.zombienautilusvariants

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.arguments.types.resources.ModelArgument
import io.github.ayfri.kore.data.spawncondition.SpawnConditions
import io.github.ayfri.kore.data.spawncondition.VariantSpawnEntry
import io.github.ayfri.kore.generated.arguments.types.ZombieNautilusVariantArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven definition for a zombie nautilus variant, as used in Minecraft Java Edition.
 *
 * A zombie nautilus variant specifies a unique model and texture for zombie nautiluses.
 * It can be associated with spawn conditions to control when and where the variant appears.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Zombie_Nautilus
 */
@Serializable
data class ZombieNautilusVariant(
	@Transient
	override var fileName: String = "zombie_nautilus_variant",
	/** The texture asset to use for the zombie nautilus variant. */
	var assetId: ModelArgument,
	/** The model to use for the zombie nautilus variant. */
	var model: ZombieNautilusModel = ZombieNautilusModel.NORMAL,
	/** The spawn conditions for this zombie nautilus variant. */
	var spawnConditions: List<VariantSpawnEntry> = emptyList(),
) : Generator("zombie_nautilus_variant") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/** Sets the spawn conditions for this zombie nautilus variant. */
fun ZombieNautilusVariant.spawnConditions(block: SpawnConditions.() -> Unit) =
	apply { spawnConditions = SpawnConditions().apply(block).entries }

/**
 * Create and register a zombie nautilus variant in this [DataPack].
 *
 * Produces `data/<namespace>/zombie_nautilus_variant/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/variants
 * JSON format reference: https://minecraft.wiki/w/Mob_variant_definitions#Zombie_Nautilus
 */
fun DataPack.zombieNautilusVariant(
	fileName: String = "zombie_nautilus_variant",
	assetId: ModelArgument,
	model: ZombieNautilusModel = ZombieNautilusModel.NORMAL,
	block: ZombieNautilusVariant.() -> Unit = {},
): ZombieNautilusVariantArgument {
	val zombieNautilusVariant = ZombieNautilusVariant(fileName, assetId, model).apply(block)
	zombieNautilusVariants += zombieNautilusVariant
	return ZombieNautilusVariantArgument(fileName, zombieNautilusVariant.namespace ?: name)
}
