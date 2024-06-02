package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.scores.SelectorScore

fun SelectorArguments.advancements(block: AdvancementBuilder.() -> Unit) {
	advancements = AdvancementBuilder().apply(block).build()
}

fun SelectorArguments.scores(block: Scores<SelectorScore>.() -> Unit) {
	scores = Scores<SelectorScore>().apply(block)
}
