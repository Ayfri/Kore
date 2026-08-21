package io.github.ayfri.kore.features.worldgen.configuredcarver

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProvider
import io.github.ayfri.kore.features.worldgen.floatproviders.FloatProviderScope
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProvider
import io.github.ayfri.kore.features.worldgen.heightproviders.HeightProviderScope
import io.github.ayfri.kore.features.worldgen.verticalanchors.VerticalAnchor
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * The configuration of a [ConfiguredCarver], serialized as `{ "type": "<carver>", "config": { ... } }`.
 *
 * Every carver type shares the properties declared here and adds its own on top: see [Cave], [NetherCave] and [Canyon].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/carvers
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 *
 * @property probability Chance for each chunk to attempt a carve, between `0` and `1`.
 * @property y The height at which the carve starts.
 * @property yScale Vertical scaling of the carved shape.
 * @property lavaLevel Y level at or below which carved areas are filled with lava, ignored by [NetherCave].
 * @property replaceable Blocks the carver is allowed to remove.
 * @property debugSettings Replaces the blocks the carver places, to make the carved volume visible.
 */
@GeneratedSealedSerializer
@Serializable(with = Config.Companion.ConfigSerializer::class)
sealed class Config : FloatProviderScope, HeightProviderScope {
	abstract var probability: Double
	abstract var y: HeightProvider
	abstract var yScale: FloatProvider
	abstract var lavaLevel: VerticalAnchor
	abstract var replaceable: InlinableList<BlockOrTagArgument>
	abstract var debugSettings: DebugSettings?

	companion object {
		@OptIn(InternalSerializationApi::class)
		data object ConfigSerializer :
			NamespacedPolymorphicSerializer<Config>(configSealedSerializer(), moveIntoProperty = "config")
	}
}

/**
 * Replaces the blocks the carver places, to make the carved volume visible without exploring it.
 *
 * ```kotlin
 * cave("my_cave") {
 *     debugSettings {
 *         debugMode = true
 *         airState(Blocks.RED_STAINED_GLASS)
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Carver_definition
 */
fun Config.debugSettings(init: DebugSettings.() -> Unit = {}) {
	debugSettings = DebugSettings().apply(init)
}

/** Sets [Config.replaceable] to [blocks], replacing whatever the carver type defaults to. */
fun Config.replaceable(vararg blocks: BlockOrTagArgument) {
	replaceable = blocks.toList()
}

/** Sets [Config.replaceable] from a list built in [init], replacing whatever the carver type defaults to. */
fun Config.replaceable(init: MutableList<BlockOrTagArgument>.() -> Unit) {
	replaceable = buildList(init)
}
