package features.advancements.types

import arguments.enums.Gamemode
import arguments.selector.Advancements
import arguments.types.resources.CatVariantArgument
import arguments.types.resources.FrogVariantArgument
import arguments.types.resources.RecipeArgument
import arguments.types.resources.StatisticArgument
import features.advancements.serializers.IntRangeOrIntJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
sealed interface EntityTypeSpecific

@Serializable
object AnyTypeSpecific : EntityTypeSpecific

@Serializable
data class CatTypeSpecific(var variant: CatVariantArgument? = null) : EntityTypeSpecific

@Serializable
data class FishingHookTypeSpecific(var inOpenWater: Boolean? = null) : EntityTypeSpecific

@Serializable
data class FrogTypeSpecific(var variant: FrogVariantArgument? = null) : EntityTypeSpecific

@Serializable
data class LightningTypeSpecific(
	var blocksSetOnFire: IntRangeOrIntJson? = null,
	var entityStruck: Entity? = null,
) : EntityTypeSpecific

object AdvancementsJSONSerializer : JsonTransformingSerializer<Advancements>(Advancements.serializer()) {
	override fun transformSerialize(element: JsonElement): JsonElement {
		return buildJsonObject {
			element.jsonObject["advancements"]!!.jsonArray.forEach { value ->
				val criteria = value.jsonObject["criteria"]!!.jsonObject
				val name = value.jsonObject["advancement"]!!.jsonPrimitive.content
				val jsonObject = when {
					criteria.isNotEmpty() -> buildJsonObject {
						criteria.jsonObject.forEach { (key, value) -> put(key, value) }
					}

					else -> JsonPrimitive(value.jsonObject["done"]!!.jsonPrimitive.boolean)
				}

				put(name, jsonObject)
			}
		}
	}
}

@Serializable
data class PlayerTypeSpecific(
	var lookingAt: Entity? = null,
	@Serializable(AdvancementsJSONSerializer::class) var advancements: Advancements? = null,
	var gamemode: Gamemode? = null,
	var level: IntRangeOrIntJson? = null,
	var recipes: Map<RecipeArgument, Boolean>? = null,
	var stats: Map<StatisticArgument, Statistic>? = null,
) : EntityTypeSpecific

@Serializable
data class SlimeTypeSpecific(var size: IntRangeOrIntJson? = null) : EntityTypeSpecific
