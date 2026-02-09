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

@Serializable
data class BedRule(
	var canSleep: BedSleepRule,
	var canSetSpawn: BedSleepRule,
	var explodes: Boolean? = null,
	var errorMessage: ChatComponents? = null,
) : EnvironmentAttributesType()

fun EnvironmentAttributesScope.bedRule(rule: BedRule, mod: EnvironmentAttributeModifier? = null, block: BedRule.() -> Unit = {}) =
	apply {
		this[EnvironmentAttributes.Gameplay.BED_RULE] = environmentAttributeValue(rule.apply(block), mod)
	}

fun EnvironmentAttributesScope.bedRule(
	canSleep: BedSleepRule = BedSleepRule.WHEN_DARK,
	canSetSpawn: BedSleepRule = BedSleepRule.WHEN_DARK,
	explodes: Boolean? = null,
	errorMessage: ChatComponents? = null,
	mod: EnvironmentAttributeModifier? = null,
	block: BedRule.() -> Unit = {},
) = apply {
	this[EnvironmentAttributes.Gameplay.BED_RULE] = environmentAttributeValue(
		BedRule(canSleep, canSetSpawn, explodes, errorMessage).apply(block), mod
	)
}

fun BedRule.errorMessage(text: String, color: Color? = null, block: PlainTextComponent.() -> Unit) = apply {
	errorMessage = textComponent(text, color, block)
}
