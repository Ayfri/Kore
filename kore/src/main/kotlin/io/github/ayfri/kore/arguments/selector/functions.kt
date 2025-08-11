package io.github.ayfri.kore.arguments.selector

import io.github.ayfri.kore.arguments.scores.Scores
import io.github.ayfri.kore.arguments.scores.SelectorScore

/** DSL helper to configure `advancements` on a `SelectorArguments`. */
fun SelectorArguments.advancements(block: AdvancementBuilder.() -> Unit) {
	advancements = AdvancementBuilder().apply(block).build()
}

/** DSL helper to configure `scores` on a `SelectorArguments`. */
fun SelectorArguments.scores(block: Scores<SelectorScore>.() -> Unit) {
	scores = Scores<SelectorScore>().apply(block)
}
