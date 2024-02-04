package io.github.ayfri.kore.website.utils

import org.jetbrains.compose.web.ExperimentalComposeWebApi
import org.jetbrains.compose.web.css.*

typealias CSSTimeValue = CSSSizeValue<out CSSUnitTime>

fun StyleScope.transition(
	duration: CSSTimeValue,
	vararg properties: String,
) = transition(
	duration,
	AnimationTimingFunction.EaseInOut,
	null,
	*properties
)

fun StyleScope.transition(
	duration: CSSTimeValue,
	function: AnimationTimingFunction = AnimationTimingFunction.EaseInOut,
	vararg properties: String,
) = transition(
	duration,
	function,
	null,
	*properties
)

@OptIn(ExperimentalComposeWebApi::class)
fun StyleScope.transition(
	duration: CSSTimeValue,
	function: AnimationTimingFunction = AnimationTimingFunction.EaseInOut,
	delay: CSSTimeValue? = null,
	vararg properties: String,
) = transitions {
	delay?.let { defaultDelay(it) }
	defaultDuration(duration)
	defaultTimingFunction(function)
	properties(*properties)
}
