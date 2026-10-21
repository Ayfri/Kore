package io.github.ayfri.kore.features.recipes.data

import io.github.ayfri.kore.arguments.components.ComponentsPatch
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable

/**
 * Result of a transmute-like recipe, where every field is optional.
 *
 * Without [id] the result is the input item, so an empty result writes `"result": {}`. Only setting [id] writes the bare item ID.
 *
 * ```kotlin
 * recipes {
 *     craftingTransmute("map_cloning") { // "result": {}
 *         input(Items.FILLED_MAP)
 *         material(Items.MAP)
 *     }
 *     craftingTransmute("red_bundle") { // "result": "minecraft:red_bundle"
 *         input(Items.BLACK_BUNDLE)
 *         material(Items.RED_DYE)
 *         result(Items.RED_BUNDLE)
 *     }
 * }
 * ```
 *
 * `result(Items.RED_BUNDLE, count = 2)` writes `{"count": 2, "id": "minecraft:red_bundle"}`.
 */
@KeepGeneratedSerializer
@Serializable(with = TransmuteResult.Companion.TransmuteResultSerializer::class)
data class TransmuteResult(
	var components: ComponentsPatch? = null,
	var count: Short? = null,
	var id: String? = null,
) {
	companion object {
		data object TransmuteResultSerializer :
			SinglePropertySimplifierSerializer<TransmuteResult>(generatedSerializer(), "id")
	}
}

/** Sets the result item to [item], keeping the components it carries. */
fun TransmuteResult.item(item: ItemArgument) = apply {
	id = item.asId()
	components = item.components
}

/** Sets the component patch applied to the result. */
fun TransmuteResult.components(block: ComponentsPatch.() -> Unit) = apply { components = ComponentsPatch().apply(block) }
