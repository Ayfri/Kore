package io.github.ayfri.kore.features.worldgen.environmentattributes.types

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.PlainTextComponent
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributesScope
import io.github.ayfri.kore.features.worldgen.environmentattributes.environmentAttributeValue
import io.github.ayfri.kore.generated.EnvironmentAttributes
import io.github.ayfri.kore.serializers.LowercaseSerializer
import kotlinx.serialization.Serializable

/** Defines when a Bed can be used to sleep or set a respawn point. */
@Serializable(with = BedSleepRule.Companion.BedSleepRuleSerializer::class)
enum class BedSleepRule {
	ALWAYS,
	NEVER,
	WHEN_DARK,
	;

	companion object {
		data object BedSleepRuleSerializer : LowercaseSerializer<BedSleepRule>(entries)
	}
}

/**
 * Controls Bed behavior: sleeping, respawn point setting, destruction, and error messages.
 *
 * @property destroyOnLeave Whether the bed is destroyed when the player leaves it. Defaults to `false`.
 * @property destroyOnUse Whether the bed is destroyed (it explodes) when used. Defaults to `false`.
 */
@Serializable
data class BedRule(
	var canSleep: BedSleepRule,
	var canSetSpawn: BedSleepRule,
	var destroyOnLeave: Boolean? = null,
	var destroyOnUse: Boolean? = null,
	var errorMessage: ChatComponents? = null,
) : EnvironmentAttributesType()

/** Sets the bed rule attribute from an existing [BedRule] instance. */
fun EnvironmentAttributesScope.bedRule(
	rule: BedRule,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: BedRule.() -> Unit = {},
) =
	apply {
		this[EnvironmentAttributes.Gameplay.BED_RULE] = environmentAttributeValue(rule.apply(block), mod)
	}

/** Sets the bed rule attribute, controlling sleep, respawn, and destruction behavior. */
fun EnvironmentAttributesScope.bedRule(
	canSleep: BedSleepRule = BedSleepRule.WHEN_DARK,
	canSetSpawn: BedSleepRule = BedSleepRule.WHEN_DARK,
	destroyOnLeave: Boolean? = null,
	destroyOnUse: Boolean? = null,
	errorMessage: ChatComponents? = null,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: BedRule.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Gameplay.BED_RULE] = environmentAttributeValue(
		BedRule(canSleep, canSetSpawn, destroyOnLeave, destroyOnUse, errorMessage).apply(block), mod
	)
}

/** Sets the straw bed rule attribute from an existing [BedRule] instance. */
fun EnvironmentAttributesScope.strawBedRule(
	rule: BedRule,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: BedRule.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Gameplay.STRAW_BED_RULE] = environmentAttributeValue(rule.apply(block), mod)
}

/** Sets the straw bed rule attribute, controlling sleep, respawn, and destruction behavior of straw beds. */
fun EnvironmentAttributesScope.strawBedRule(
	canSleep: BedSleepRule = BedSleepRule.WHEN_DARK,
	canSetSpawn: BedSleepRule = BedSleepRule.WHEN_DARK,
	destroyOnLeave: Boolean? = null,
	destroyOnUse: Boolean? = null,
	errorMessage: ChatComponents? = null,
	mod: EnvironmentAttributeModifier.OVERRIDE? = null,
	block: BedRule.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Gameplay.STRAW_BED_RULE] = environmentAttributeValue(
		BedRule(canSleep, canSetSpawn, destroyOnLeave, destroyOnUse, errorMessage).apply(block), mod
	)
}

fun BedRule.errorMessage(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit) = apply {
	errorMessage = textComponent(text, color, block)
}
