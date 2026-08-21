package io.github.ayfri.kore.features.worldgen.dimension.generator

import io.github.ayfri.kore.arguments.types.resources.BlockArgument
import io.github.ayfri.kore.generated.arguments.worldgen.StructureSetOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.BiomeArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * The settings of a superflat world, shared by the [Flat] dimension generator and the flat level generator presets.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 *
 * @property biome The biome every chunk of the world uses.
 * @property lakes Whether lava lakes generate, `false` by default.
 * @property features Whether the biome placed features generate, `false` by default.
 * @property layers The block layers, read from the bottom of the world upwards.
 * @property structureOverrides The structure sets allowed to generate, every structure set by default.
 */
@Serializable
data class FlatGeneratorSettings(
	var biome: BiomeArgument,
	var lakes: Boolean? = null,
	var features: Boolean? = null,
	var layers: List<Layer> = emptyList(),
	var structureOverrides: InlinableList<StructureSetOrTagArgument> = emptyList(),
)

/**
 * One layer of a superflat world, a stack of [height] blocks of [block].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 *
 * @property block The block filling the layer.
 * @property height How many blocks tall the layer is, between `0` and `4064`.
 */
@Serializable
data class Layer(
	var block: BlockArgument,
	var height: Int,
)

/**
 * Builder scope for the [layers][FlatGeneratorSettings.layers] of a superflat world, added from the bottom of the
 * world upwards.
 */
class FlatLayersScope internal constructor() {
	internal val layers = mutableListOf<Layer>()

	/** Adds a layer of [height] blocks of [block] on top of the layers added so far. */
	fun layer(block: BlockArgument, height: Int = 1) {
		layers += Layer(block, height)
	}
}

/**
 * Adds a layer of [height] blocks of [block] on top of the layers declared so far.
 *
 * ```kotlin
 * flatGenerator(Biomes.NETHER_WASTES) {
 *     layer(Blocks.NETHERRACK, 1)
 *     layer(Blocks.LAVA, 2)
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 */
fun FlatGeneratorSettings.layer(block: BlockArgument, height: Int = 1) = Layer(block, height).also { layers += it }

/** Sets [FlatGeneratorSettings.layers] to [layers], read from the bottom of the world upwards. */
fun FlatGeneratorSettings.layers(vararg layers: Layer) {
	this.layers = layers.toList()
}

/**
 * Sets [FlatGeneratorSettings.layers] from the layers added in [block], read from the bottom of the world upwards.
 *
 * ```kotlin
 * flatGenerator(Biomes.PLAINS) {
 *     layers {
 *         layer(Blocks.BEDROCK)
 *         layer(Blocks.DIRT, height = 2)
 *         layer(Blocks.GRASS_BLOCK)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 */
fun FlatGeneratorSettings.layers(block: FlatLayersScope.() -> Unit) {
	layers = FlatLayersScope().apply(block).layers
}

/**
 * Sets [FlatGeneratorSettings.structureOverrides] to [structures], the only structure sets allowed to generate.
 *
 * A single structure set tag can be given instead of a list of structure sets.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Superflat
 */
fun FlatGeneratorSettings.structureOverrides(vararg structures: StructureSetOrTagArgument) {
	structureOverrides = structures.toList()
}

/** Sets [FlatGeneratorSettings.structureOverrides] from the list built in [block]. */
fun FlatGeneratorSettings.structureOverrides(block: MutableList<StructureSetOrTagArgument>.() -> Unit) {
	structureOverrides = buildList(block)
}
