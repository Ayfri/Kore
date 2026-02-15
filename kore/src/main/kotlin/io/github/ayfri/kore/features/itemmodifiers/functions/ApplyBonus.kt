package io.github.ayfri.kore.features.itemmodifiers.functions

import io.github.ayfri.kore.features.itemmodifiers.ItemModifier
import io.github.ayfri.kore.features.itemmodifiers.formulas.Formula
import io.github.ayfri.kore.features.predicates.PredicateAsList
import io.github.ayfri.kore.generated.arguments.types.EnchantmentArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Applies a predefined bonus formula to the item stack count based on an enchantment level.
 * Mirrors vanilla `minecraft:apply_bonus` with formula types such as binomial_with_bonus_count,
 * uniform_bonus_count, and ore_drops.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/item-modifiers
 * See also: https://minecraft.wiki/w/Item_modifier
 */
@Serializable(with = ApplyBonus.Companion.ApplyBonusSerializer::class)
data class ApplyBonus(
	override var conditions: PredicateAsList? = null,
	var enchantment: EnchantmentArgument,
	var formula: Formula,
) : ItemFunction() {
	companion object {
		data object ApplyBonusSerializer : KSerializer<ApplyBonus> {
			override val descriptor: SerialDescriptor = serialDescriptor<JsonElement>()

			override fun deserialize(decoder: Decoder) = error("ApplyBonus cannot be deserialized")

			override fun serialize(encoder: Encoder, value: ApplyBonus) {
				require(encoder is JsonEncoder)
				val json = encoder.json

				val formulaJson = json.encodeToJsonElement(Formula.serializer(), value.formula).jsonObject

				val result = buildJsonObject {
					value.conditions?.let {
						put("conditions", json.encodeToJsonElement(PredicateAsList.serializer(), it))
					}
					put("enchantment", json.encodeToJsonElement(value.enchantment))
					formulaJson.forEach { (key, v) -> put(key, v) }
				}

				encoder.encodeJsonElement(result)
			}
		}

	}
}

/** Add an `apply_bonus` step to this modifier. */
fun ItemModifier.applyBonus(enchantment: EnchantmentArgument, formula: Formula, block: ApplyBonus.() -> Unit = {}) {
	modifiers += ApplyBonus(enchantment = enchantment, formula = formula).apply(block)
}
