package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.BooleanValue
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.EnvironmentAttributesType
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.FloatValue
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import kotlinx.serialization.Serializable

/**
 * Exactly matches the value of an environment attribute at a given position.
 *
 * Requires a loot context with an origin position set as long as the environment attribute can vary positionally.
 *
 * The JSON shape of [value] varies by attribute type: booleans serialize as `true`/`false`,
 * floats as numbers, string-typed attributes (moon phase, villager activity, etc.) as resource
 * location strings, and compound types (ambient sounds, background music, etc.) as objects.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - environment_attribute_check](https://minecraft.wiki/w/Predicate#environment_attribute_check)
 */
@Serializable
data class EnvironmentAttributeCheck(
	var attribute: EnvironmentAttributeArgument,
	var value: EnvironmentAttributesType,
) : PredicateCondition()

/**
 * Exactly matches [value] against environment attribute [attribute] at the current origin position.
 *
 * Requires a loot context with an origin position set as long as the attribute can vary positionally.
 */
fun Predicate.environmentAttributeCheck(attribute: EnvironmentAttributeArgument, value: EnvironmentAttributesType) {
	predicateConditions += EnvironmentAttributeCheck(attribute, value)
}

/**
 * Exactly matches [value] against a boolean environment attribute at the current origin position.
 *
 * Requires a loot context with an origin position set as long as the attribute can vary positionally.
 */
fun Predicate.environmentAttributeCheck(attribute: EnvironmentAttributeArgument, value: Boolean) {
	predicateConditions += EnvironmentAttributeCheck(attribute, BooleanValue(value))
}

/**
 * Exactly matches [value] against a float environment attribute at the current origin position.
 *
 * Requires a loot context with an origin position set as long as the attribute can vary positionally.
 */
fun Predicate.environmentAttributeCheck(attribute: EnvironmentAttributeArgument, value: Float) {
	predicateConditions += EnvironmentAttributeCheck(attribute, FloatValue(value))
}

/**
 * Adds one [EnvironmentAttributeCheck] condition per attribute set in [block].
 *
 * The block runs on an [EnvironmentAttributesScope] - call any number of typed builders
 * (e.g. `moonPhase(...)`, `beesStayInHive(...)`) to declare each attribute and its expected value.
 * Multiple attributes produce multiple conditions that all must pass (AND).
 *
 * Requires a loot context with an origin position set as long as any attribute can vary positionally.
 */
fun Predicate.environmentAttributeCheck(block: EnvironmentAttributesScope.() -> Unit) {
	val scope = EnvironmentAttributesScope().apply(block)
	scope.attributes.forEach { (attr, attrValue) ->
		predicateConditions += EnvironmentAttributeCheck(attr, attrValue.argument)
	}
}
