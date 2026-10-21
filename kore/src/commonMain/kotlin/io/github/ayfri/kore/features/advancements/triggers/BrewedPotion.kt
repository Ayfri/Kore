package io.github.ayfri.kore.features.advancements.triggers

import io.github.ayfri.kore.arguments.components.matchers.PotionContentsComponentMatcher
import io.github.ayfri.kore.features.advancements.AdvancementCriteria
import io.github.ayfri.kore.features.advancements.EntityOrPredicates
import io.github.ayfri.kore.generated.arguments.PotionOrTagArgument
import kotlinx.serialization.Serializable

/**
 * Triggered when a potion is brewed, matching the [potion] contents (a `potion_contents` predicate: potions and effects).
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/advancements/triggers#brewedpotion
 * Minecraft Wiki: https://minecraft.wiki/w/Advancement/JSON_format
 */
@Serializable
data class BrewedPotion(
	override var player: EntityOrPredicates? = null,
	var potion: PotionContentsComponentMatcher? = null,
) : AdvancementTriggerCondition()

/** Add a `brewedPotion` criterion, triggered when a potion is brewed. */
fun AdvancementCriteria.brewedPotion(name: String, block: BrewedPotion.() -> Unit = {}) {
	criteria[name] = BrewedPotion().apply(block)
}

/**
 * Match any of the brewed [potions], potion IDs or potion tags.
 *
 * ```
 * potion(Potions.SWIFTNESS)  // "potion": { "potions": "minecraft:swiftness" }
 * ```
 */
fun BrewedPotion.potion(vararg potions: PotionOrTagArgument) {
	potion = PotionContentsComponentMatcher(potions = potions.toList())
}

/** Match the brewed potion contents, its `potions` and `effects`. */
fun BrewedPotion.potion(block: PotionContentsComponentMatcher.() -> Unit) {
	potion = PotionContentsComponentMatcher().apply(block)
}
